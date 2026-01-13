package com.nlu.store.modules.order.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.models.OrderStatus;
import com.nlu.store.modules.order.models.PaymentStatus;
import com.nlu.store.modules.order.models.ShippingDetails;
import com.nlu.store.modules.payment.models.PaymentMethod;

import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {

    private final String prefix;
    private final ShippingDetailsMapper shippingMapper;

    /**
     * Full constructor for JOIN scenarios.
     *
     * @param orderPrefix    Prefix for the orders table (e.g., "o_").
     * @param shippingPrefix Prefix for the shipping table (e.g., "os_").
     *                       If null, shipping details will not be mapped.
     */
    public OrderMapper(String orderPrefix, String shippingPrefix) {
        this.prefix = (orderPrefix == null) ? "" : orderPrefix;

        // Only initialize the child mapper if a prefix is provided (mapping requested)
        if (shippingPrefix != null) {
            this.shippingMapper = new ShippingDetailsMapper(shippingPrefix);
        } else {
            this.shippingMapper = null;
        }
    }

    /**
     * Default constructor.
     * Use this for simple "SELECT * FROM orders" queries (no prefix, no shipping).
     */
    public OrderMapper() {
        this("", null);
    }

    /**
     * Constructor with order prefix only (no shipping mapping).
     *
     * @param orderPrefix Prefix for the orders table.
     */
    public OrderMapper(String orderPrefix) {
        this(orderPrefix, null);
    }

    @Override
    public String prefix() {
        return this.prefix;
    }

    @Override
    public Order mapRow(ResultSetReader reader, int row) throws SQLException {
        Order.OrderBuilder builder = Order.builder();

        // 1. Identity
        builder.id(reader.getULID(column("id")));
        builder.code(reader.getString(column("code")));
        builder.userId(reader.getULID(column("user_id")));

        // 2. Status & Lifecycle
        // Using getEnum is cleaner and handles nulls automatically
        builder.status(reader.getEnum(column("status"), OrderStatus.class));
        builder.cancelReason(reader.getString(column("cancel_reason")));

        // 3. Payment
        builder.paymentMethod(reader.getEnum(column("payment_method"), PaymentMethod.class));
        builder.paymentStatus(reader.getEnum(column("payment_status"), PaymentStatus.class));

        builder.transactionRef(reader.getString(column("transaction_ref")));
        builder.paidAt(reader.getLocalDateTime(column("paid_at")));

        // 4. Financials
        builder.currency(reader.getString(column("currency")));
        builder.subTotal(reader.getBigDecimal(column("sub_total")));
        builder.shippingFee(reader.getBigDecimal(column("shipping_fee")));
        builder.discountAmount(reader.getBigDecimal(column("discount_amount")));
        builder.couponCode(reader.getString(column("coupon_code")));
        builder.grandTotal(reader.getBigDecimal(column("grand_total")));

        // 5. Audit & Meta
        builder.note(reader.getString(column("note")));
        builder.ipAddress(reader.getString(column("ip_address")));
        builder.userAgent(reader.getString(column("user_agent")));
        builder.createdAt(reader.getLocalDateTime(column("created_at")));
        builder.updatedAt(reader.getLocalDateTime(column("updated_at")));

        // 6. Map Shipping Details (Delegate to ShippingDetailsMapper)
        // If shippingMapper is initialized, we use it to map data from the current row
        if (this.shippingMapper != null) {
            ShippingDetails shippingDetails = shippingMapper.mapRow(reader, row);
            builder.shippingDetails(shippingDetails);
        }

        return builder.build();
    }
}
