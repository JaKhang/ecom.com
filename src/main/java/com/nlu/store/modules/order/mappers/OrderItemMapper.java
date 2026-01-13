package com.nlu.store.modules.order.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.order.models.OrderItem;

import java.sql.SQLException;




public class OrderItemMapper implements RowMapper<OrderItem> {

    private final String prefix;

    /**
     * Constructor with prefix support.
     * <p>
     * Useful for SQL JOIN queries where column names might collide or be aliased
     * (e.g., mapping "oi_id" instead of just "id").
     * </p>
     *
     * @param prefix The prefix string (e.g., "oi_").
     */
    public OrderItemMapper(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Default constructor (no prefix).
     * <p>
     * Suitable for simple queries (e.g., {@code SELECT * FROM order_items}).
     * </p>
     */
    public OrderItemMapper() {
        this("");
    }

    @Override
    public OrderItem mapRow(ResultSetReader reader, int row) throws SQLException {
        return OrderItem.builder()
                // 1. Mapping IDs & Foreign Keys (ULID)
                .id(reader.getULID(column("id")))
                .productId(reader.getULID(column("product_id")))
                .variantId(reader.getULID(column("variant_id")))

                // 2. Mapping Product Snapshot
                // These fields represent the product state at the time of purchase.
                .productName(reader.getString(column("product_name")))
                .sku(reader.getString(column("sku")))
                .thumbnail(reader.getString(column("thumbnail")))

                // 3. Mapping JSON Snapshot -> Map<String, String>
                // The ResultSetReader automatically parses the JSON column into a Java Map.
                // Example DB value: {"Color": "Blue", "Size": "M"}
                .variantSnapshot(reader.getJson(column("variant_snapshot")))

                // 4. Mapping Financials
                // Using getNullableInt ensures safety if quantity is ever NULL (though unlikely based on schema).
                .quantity(reader.getNullableInt(column("quantity")))
                .price(reader.getBigDecimal(column("price")))
                .totalPrice(reader.getBigDecimal(column("total_price")))

                .build();
    }

    @Override
    public String prefix() {
        return prefix;
    }
}
