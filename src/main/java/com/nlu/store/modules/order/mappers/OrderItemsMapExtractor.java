package com.nlu.store.modules.order.mappers;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jdbc.ResultSetExtractor;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.modules.order.models.OrderItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts {@link OrderItem} rows and groups them by their parent {@code order_id}.
 * <p>
 * This extractor is designed for the "2-Step Query" pattern:
 * 1. Fetch a page of Orders.
 * 2. Fetch all Items for those Orders (e.g., {@code WHERE order_id IN (...)}).
 * 3. Use this extractor to convert the flat result set into a Map for efficient in-memory assignment.
 * </p>
 */
public class OrderItemsMapExtractor implements ResultSetExtractor<Map<ULID, List<OrderItem>>> {

    private final OrderItemMapper mapper;

    /**
     * Constructor with column prefix support.
     * <p>
     * Use this when the query involves joins or aliases (e.g., "oi_order_id").
     * </p>
     *
     * @param prefix the column prefix (e.g., "oi_" or empty string).
     */
    public OrderItemsMapExtractor(String prefix) {
        this.mapper = new OrderItemMapper(prefix);
    }

    /**
     * Default constructor.
     */
    public OrderItemsMapExtractor() {
        this("");
    }

    @Override
    public Map<ULID, List<OrderItem>> extractData(ResultSetReader reader) throws SQLException {
        // Use LinkedHashMap to preserve the insertion order.
        // If the SQL query is sorted (e.g., ORDER BY created_at), this map will respect that order.
        Map<ULID, List<OrderItem>> itemsByOrder = new LinkedHashMap<>();

        while (reader.next()) {
            // 1. Identify the grouping key (order_id)
            // We use mapper.column() to ensure we respect any configured prefix (e.g., "oi_order_id").
            String orderIdColumn = mapper.column("order_id");
            ULID orderId = reader.getULID(orderIdColumn);

            // Safety check: Skip if order_id is null (though unlikely given the schema constraints).
            if (orderId == null) {
                continue;
            }

            // 2. Map the current row to an OrderItem object
            // We delegate to OrderItemMapper to reuse the mapping logic and avoid code duplication.
            OrderItem item = mapper.mapRow(reader, reader.getRawResultSet().getRow());

            // 3. Group the item into the list corresponding to its order_id
            // computeIfAbsent creates a new ArrayList if the key doesn't exist yet.
            itemsByOrder
                    .computeIfAbsent(orderId, k -> new ArrayList<>())
                    .add(item);
        }

        return itemsByOrder;
    }
}
