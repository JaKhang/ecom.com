package com.nlu.store.modules.catalog.model.details;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ProductImage {
    private ULID id;
    private String url;
    private String altText;
    private List<ULID> attributeValueId;

    @Builder
    public ProductImage(ULID id, String url, String altText, List<ULID> attributeValueId) {
        this.id = id;
        this.url = url;
        this.altText = altText;
        this.attributeValueId = attributeValueId;
    }

    public ProductImage() {
    }
}
