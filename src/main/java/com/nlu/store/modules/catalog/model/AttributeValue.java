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
public class AttributeValue extends AbstractModel implements Serializable {

    public AttributeValue(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
    }
}
