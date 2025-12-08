package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class Attribute extends AbstractModel implements Serializable {
    private String name;
    private String code;
    private AttributeType type;
    private String unit;
    private boolean isVariantAxis;
    private boolean isFilterable;

    @Builder
    public Attribute(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                     String name, String code, AttributeType type,
                     String unit, boolean isVariantAxis, boolean isFilterable) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.code = code;
        this.type = type;
        this.unit = unit;
        this.isVariantAxis = isVariantAxis;
        this.isFilterable = isFilterable;
    }
}