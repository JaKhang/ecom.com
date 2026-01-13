package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class SimpleProduct extends AbstractModel implements Serializable {

    private String name;
    private String slug;
    private String thumbnail;
    private SimpleObject brand;
    private String description;
    private String shortDescription;
    private ProductStatus status;
    private boolean isFeatured;
    private Map<String, String> specsSnapshot;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Map<String, Object> extras = new HashMap<>();

    @Builder
    public SimpleProduct(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                         String name, String slug, String thumbnail,
                         SimpleObject brand, String description, String shortDescription,
                         ProductStatus status, boolean isFeatured,
                         Map<String, String> specsSnapshot,
                         BigDecimal minPrice, BigDecimal maxPrice,
                         Map<String, Object> extras) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.thumbnail = thumbnail;
        this.brand = brand;
        this.description = description;
        this.shortDescription = shortDescription;
        this.status = status;
        this.isFeatured = isFeatured;
        this.specsSnapshot = specsSnapshot;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.extras = (extras != null) ? extras : new HashMap<>();
    }

    public SimpleProduct() {
    }

    // --- Logic tính toán (Vẫn cần giữ lại) ---

    public int getDiscountPercentage() {
        if (maxPrice == null || minPrice == null ||
                maxPrice.compareTo(BigDecimal.ZERO) == 0 ||
                maxPrice.compareTo(minPrice) <= 0) {
            return 0;
        }
        BigDecimal diff = maxPrice.subtract(minPrice);
        return diff.divide(maxPrice, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)).intValue();
    }
}
