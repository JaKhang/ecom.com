package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;



@Setter
@Getter
public class Product extends AbstractModel implements Serializable {

    private String name;
    private String slug;
    private Brand brandId;        // Mapping cho brand_id

    private String description;
    private String shortDesc;    // Mapping cho short_desc

    private ProductStatus status;
    private boolean isFeatured;  // Mapping cho is_featured

    // JSONB: Thường được map thành String (raw json) hoặc JsonNode (Jackson)
    // Ở đây dùng String để đảm bảo tính độc lập.
    // Khi dùng, bạn sẽ parse chuỗi này thành Object.
    private Map<String, Object> specsSnapshot;

    private Double minPrice; // Mapping cho numeric(12,2)
    private Double maxPrice; // Mapping cho numeric(12,2)

    private LocalDateTime deletedAt;

    public Product(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String slug, Brand brandId, String description, String shortDesc, ProductStatus status, boolean isFeatured, Map<String, Object> specsSnapshot, Double minPrice, Double maxPrice, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.brandId = brandId;
        this.description = description;
        this.shortDesc = shortDesc;
        this.status = status;
        this.isFeatured = isFeatured;
        this.specsSnapshot = specsSnapshot;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.deletedAt = deletedAt;
    }
}