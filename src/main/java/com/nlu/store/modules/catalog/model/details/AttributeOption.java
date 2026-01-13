package com.nlu.store.modules.catalog.model.details;

import com.nlu.store.core.data.ULID;
import lombok.Data;

@Data
public class AttributeOption {
    private ULID id;
    private String label;
    private String visualValue;
    private String visualType;

    public AttributeOption() {
    }

    public AttributeOption(ULID id, String label, String visualValue, String visualType) {
        this.id = id;
        this.label = label;
        this.visualValue = visualValue;
        this.visualType = visualType;
    }
}