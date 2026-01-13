package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ProductImage extends AbstractModel implements Serializable {
    private ULID productId;
    private List<ULID> variantIds; // JSONB: Ảnh này thuộc về variant nào?
    private String url;
    private String altText;
    private boolean isThumbnail;
    private Integer position;

    @Builder
    public ProductImage(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                        ULID productId, List<ULID> variantIds, String url,
                        String altText, boolean isThumbnail, Integer position) {
        super(id, createdAt, updatedAt);
        this.productId = productId;
        this.variantIds = variantIds;
        this.url = url;
        this.altText = altText;
        this.isThumbnail = isThumbnail;
        this.position = position;
    }
}
