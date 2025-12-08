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
public class Brand extends AbstractModel implements Serializable {
    private String name;
    private String slug;
    private String logo;
    private boolean isActive;

    public Brand(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String slug, String logo, boolean isActive) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.logo = logo;
        this.isActive = isActive;
    }
}
