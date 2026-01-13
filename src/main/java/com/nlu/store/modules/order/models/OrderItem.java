package com.nlu.store.modules.order.models;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
public class OrderItem {
    private ULID id;
    private ULID productId;
    private ULID variantId;

    // --- Product Snapshot (Fixed at time of purchase) ---
    private String productName;
    private String sku;
    private String thumbnail;

    // --- Variant Attributes Snapshot ---
    // Stores specific attributes selected by user.
    // Mapped from JSON column in DB.
    // Example: {"Color": "Titanium Blue", "Storage": "256GB"}
    private Map<String, String> variantSnapshot;

    // --- Financials ---
    private Integer quantity;

    // Unit price at the moment of purchase
    private BigDecimal price;

    // Total line item price (quantity * price)
    private BigDecimal totalPrice;

}
