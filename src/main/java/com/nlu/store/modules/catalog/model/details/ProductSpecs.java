package com.nlu.store.modules.catalog.model.details;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSpecs {
    private ULID id;
    private String attributeLabel;
    private String value;
    private String unit;
    private boolean isHighlight;

    @Builder
    public ProductSpecs(ULID id, String attributeLabel, String value, String unit, boolean isHighlight) {
        this.id = id;
        this.attributeLabel = attributeLabel;
        this.value = value;
        this.unit = unit;
        this.isHighlight = isHighlight;
    }
}
