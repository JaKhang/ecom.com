package com.nlu.store.modules.order.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.data.*;
import com.nlu.store.core.jdbc.DataAccessException;
import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.jdbc.sql.JdbcParamBuilder;
import com.nlu.store.core.jdbc.sql.SqlBuilder;
import com.nlu.store.core.jdbc.sql.WhereBuilder;
import com.nlu.store.modules.order.mappers.OrderItemsMapExtractor;
import com.nlu.store.modules.order.mappers.OrderMapper;
import com.nlu.store.modules.order.models.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class DefaultOrderDao implements OrderDao {


    private final JdbcOperations jdbc;
    private final ObjectMapper mapper;

    @Inject
    public DefaultOrderDao(JdbcOperations jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public ULID createOrderAndDecreaseStock(Order order) {
        return jdbc.executeTransaction((connection) -> {
            ULID orderId = order.getId() != null ? order.getId() : ULID.fast();
            LocalDateTime now = LocalDateTime.now();

            // ============================================================
            // 1. INSERT ORDER
            // ============================================================
            var insertOrder = SqlBuilder.insert("orders")
                    .map("id", orderId)
                    .map("code", order.getCode())
                    .map("user_id", order.getUserId())

                    // Enum -> String (Lowercase để khớp với MySQL ENUM)
                    .map("status", order.getStatus().name().toLowerCase())
                    .map("cancel_reason", order.getCancelReason())

                    // Payment & Audit
                    .map("payment_method", order.getPaymentMethod())
                    .map("payment_status", order.getPaymentStatus().name().toLowerCase())
                    .map("transaction_ref", order.getTransactionRef())
                    .map("paid_at", order.getPaidAt())

                    // Financials
                    .map("currency", order.getCurrency() != null ? order.getCurrency() : "VND")
                    .map("sub_total", order.getSubTotal())
                    .map("shipping_fee", order.getShippingFee())
                    .map("discount_amount", order.getDiscountAmount())
                    .map("coupon_code", order.getCouponCode())
                    .map("grand_total", order.getGrandTotal())

                    // Meta
                    .map("note", order.getNote())
                    .map("ip_address", order.getIpAddress())
                    .map("user_agent", order.getUserAgent())
                    .map("created_at", now)
                    .map("updated_at", now);

            jdbc.update(connection, insertOrder.getSql(), insertOrder.getParams());

            // ============================================================
            // 2. INSERT SHIPPING (Logistics)
            // ============================================================
            if (order.getShippingDetails() != null) {
                ShippingDetails ship = order.getShippingDetails();
                var insertShipping = SqlBuilder.insert("order_shipping")
                        .map("id", ULID.fast())
                        .map("order_id", orderId)

                        // Contact
                        .map("contact_name", ship.getContactName())
                        .map("contact_phone", ship.getContactPhone())
                        .map("contact_email", ship.getContactEmail())

                        // Address
                        .map("province", ship.getProvince())
                        .map("district", ship.getDistrict())
                        .map("ward", ship.getWard())
                        .map("address_detail", ship.getAddressDetail())
                        .map("full_address", ship.getFullAddress()) // Bắt buộc

                        // Geo Codes
                        .map("province_code", ship.getProvinceCode())
                        .map("district_code", ship.getDistrictCode())
                        .map("ward_code", ship.getWardCode())

                        // Tracking Info (Có thể null lúc tạo đơn)
                        .map("carrier_name", ship.getCarrierName())
                        .map("tracking_code", ship.getTrackingCode())
                        .map("estimated_delivery_date", ship.getEstimatedDeliveryDate())
                        .map("shipping_note", ship.getShippingNote());

                jdbc.update(connection, insertShipping.getSql(), insertShipping.getParams());
            }

            // ============================================================
            // 3. INSERT ITEMS & UPDATE STOCK
            // ============================================================
            if (order.getItems() != null && !order.getItems().isEmpty()) {

                String INSERT_ITEM_SQL = SqlBuilder.insert("order_items")
                        .column("id")
                        .column("order_id")
                        .column("product_id")
                        .column("variant_id")
                        // Snapshot Info
                        .column("product_name")
                        .column("sku")
                        .column("thumbnail")
                        .column("variant_snapshot") // [NEW] JSON Column
                        // Financials
                        .column("quantity")
                        .column("price")
                        .column("total_price")
                        .getSql();

                // Optimistic Locking: Chỉ trừ kho nếu stock >= quantity
                String UPDATE_STOCK_SQL = "UPDATE product_variants SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";

                JdbcParamBuilder itemParams = JdbcParamBuilder.create();
                JdbcParamBuilder updateStockParams = JdbcParamBuilder.create();

                for (OrderItem item : order.getItems()) {

                    itemParams
                            .param(ULID.fast())
                            .param(orderId)
                            .param(item.getProductId())
                            .param(item.getVariantId())
                            .param(item.getProductName())
                            .param(item.getSku())
                            .param(item.getThumbnail())
                            .param(stringify(item.getVariantSnapshot())) // Insert JSON String
                            .param(item.getQuantity())
                            .param(item.getPrice())
                            .param(item.getTotalPrice())
                            .endRow();

                    updateStockParams
                            .param(item.getQuantity())
                            .param(item.getVariantId())
                            .param(item.getQuantity()) // Điều kiện check tồn kho
                            .endRow();
                }

                jdbc.executeBatch(connection, INSERT_ITEM_SQL, itemParams.buildBatch());

                // Kiểm tra kết quả trừ kho
                int[] stockResults = jdbc.executeBatch(connection, UPDATE_STOCK_SQL, updateStockParams.buildBatch());

                for (int stockResult : stockResults) {
                    if (stockResult == 0) {
                        // Nếu row count = 0 nghĩa là WHERE stock >= quantity bị sai -> Hết hàng
                        throw new DataAccessException("out_of_stock");
                    }
                }
            }

            return orderId;
        }).orElse(null);
    }

    @Override
    public Page<Order> findOrderByUserId(ULID userId, Pageable pageable) {
        // 1. Prepare SQL for counting total records (for pagination)
        String countSql = "SELECT COUNT(*) FROM orders o WHERE o.user_id = ?";

        // 2. Prepare SQL for fetching orders with shipping details
        String sql = SELECT_ORDER_SQL + " WHERE o.user_id = ?";

        return find(sql, countSql, pageable, userId);
    }

    @Override
    public void paymentSuccess(String transactionId, String orderRef) {
        LocalDateTime now = LocalDateTime.now();
        SqlBuilder.UpdateBuilder update = SqlBuilder.update("orders")
                .set("paid_at", now)
                .set("transaction_ref", transactionId)
                .set("payment_status", PaymentStatus.PAID)
                .where("code", orderRef);
        jdbc.update(update.getSql(), update.getParams());
    }

    @Override
    public Page<Order> find(Pageable pageable, OrderStatus status, PaymentStatus paymentStatus) {
        // 1. Prepare SQL for counting total records (for pagination)
        String countSql = "SELECT COUNT(*) FROM orders o";

        // 2. Prepare SQL for fetching orders with shipping details

        return find(SELECT_ORDER_SQL, countSql, pageable);
    }

    @Override
    public Optional<Order> findById(ULID id) {
        // 1. Prepare SQL for counting total records (for pagination)
        String countSql = "SELECT COUNT(*) FROM orders o WHERE o.id = ?";

        // 2. Prepare SQL for fetching orders with shipping details
        String sql = SELECT_ORDER_SQL + " WHERE o.id = ?";

        Pageable pageable = new PageRequest(1,1, Sort.UNSORTED);
        Page<Order> orders = find(sql, countSql, pageable, id);
        System.out.println(orders);
        return orders.getContent().stream().findAny();
    }

    private Page<Order> find(String select, String count, Pageable pageable, Object... parms) {


        // 1. Execute the main query using OrderMapper (maps 'o_' to Order, 'os_' to ShippingDetails)
        Page<Order> orders = jdbc.queryForPage(select, count, pageable, new OrderMapper("o_", "os_"), parms);

        // Optimization: If no orders found, return immediately to avoid unnecessary DB calls
        if (orders.getContent().isEmpty()) {
            return orders;
        }

        // 2. Collect list of Order IDs to fetch associated items
        List<ULID> orderIds = orders.getContent().stream()
                .map(Order::getId)
                .toList();

        // 3. Build query to fetch items for these orders
        WhereBuilder selectItemBuilder = WhereBuilder.create(SELECT_ITEM_SQL)
                .andIn("oi.order_id", orderIds);

        // 4. Execute item query and map results (grouped by Order ID)
        Map<ULID, List<OrderItem>> orderItems = jdbc.executeQuery(
                selectItemBuilder.getSql(),
                new OrderItemsMapExtractor("oi_"),
                selectItemBuilder.getParams()
        ).orElse(Collections.emptyMap());

        // 5. Attach items to their respective orders
        // Note: Assuming Page implements Iterable, otherwise use orders.getContent().forEach(...)
        orders.forEach(order ->
                order.setItems(orderItems.getOrDefault(order.getId(), Collections.emptyList()))
        );

        return orders;
    }


    private static final String SELECT_ORDER_SQL = "SELECT o.id AS o_id, o.code AS o_code, o.user_id AS o_user_id, o.status AS o_status, o.cancel_reason AS o_cancel_reason, o.payment_method AS o_payment_method, o.payment_status AS o_payment_status, o.transaction_ref AS o_transaction_ref, o.paid_at AS o_paid_at, o.currency AS o_currency, o.sub_total AS o_sub_total, o.shipping_fee AS o_shipping_fee, o.discount_amount AS o_discount_amount, o.coupon_code AS o_coupon_code, o.grand_total AS o_grand_total, o.note AS o_note, o.ip_address AS o_ip_address, o.user_agent AS o_user_agent, o.created_at AS o_created_at, o.updated_at AS o_updated_at, os.id AS os_id, os.contact_name AS os_contact_name, os.contact_phone AS os_contact_phone, os.contact_email AS os_contact_email, os.province AS os_province, os.district AS os_district, os.ward AS os_ward, os.address_detail AS os_address_detail, os.full_address AS os_full_address, os.province_code AS os_province_code, os.district_code AS os_district_code, os.ward_code AS os_ward_code, os.carrier_name AS os_carrier_name, os.tracking_code AS os_tracking_code, os.estimated_delivery_date AS os_estimated_delivery_date, os.shipping_note AS os_shipping_note FROM orders o LEFT JOIN order_shipping os ON o.id = os.order_id";
    private static final String SELECT_ITEM_SQL = "SELECT oi.id AS oi_id, oi.order_id AS oi_order_id, oi.product_id AS oi_product_id, oi.variant_id AS oi_variant_id, oi.product_name AS oi_product_name, oi.sku AS oi_sku, oi.thumbnail AS oi_thumbnail, oi.variant_snapshot AS oi_variant_snapshot, oi.quantity AS oi_quantity, oi.price AS oi_price, oi.total_price AS oi_total_price FROM order_items oi";

    private String stringify(Map<String, String> variantSnapshot) {
        if (variantSnapshot == null || variantSnapshot.isEmpty()) {
            return "{}";
        }
        try {
            return mapper.writeValueAsString(variantSnapshot);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
