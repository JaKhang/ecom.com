package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class AttributeOption extends AbstractModel implements Serializable {
    private ULID attributeId;
    private String value;
    private String metaValue;
    private Integer sortOrder;

    public AttributeOption(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
    }
}
