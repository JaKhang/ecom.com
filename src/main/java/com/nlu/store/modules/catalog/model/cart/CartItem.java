package com.nlu.store.modules.catalog.model.cart;

import com.nlu.store.core.data.ULID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private ULID variantId;   // ID của phiên bản cụ thể (màu, dung lượng)
    private ULID productId;   // ID của sản phẩm cha
    private String slug;
    private String sku;
    private String variantName;
    private String thumbnail;
    private BigDecimal unitPrice;
    private BigDecimal unitOriginalPrice;
    private int quantity;
    private int reviewsCount;
    private double ratingAvg;
    @Builder.Default
    private boolean inStock = true;
    // Calculate SubTotal (Original Price * Quantity)
    public BigDecimal getSubTotal() {
        // If not in stock, it should not contribute to the order value
        if (!inStock || unitOriginalPrice == null) return BigDecimal.ZERO;
        return unitOriginalPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Calculate Total (Actual Price * Quantity)
    public BigDecimal getTotal(){
        // If not in stock, it should not contribute to the order value
        if (!inStock || unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }



}
