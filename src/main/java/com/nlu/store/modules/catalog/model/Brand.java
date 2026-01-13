package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class Brand extends AbstractModel implements Serializable {
    private String name;
    private String slug;
    private String logo;
    private boolean isActive;
    private LocalDateTime deletedAt;
    private Map<String, Object> extras;

    @Builder
    public Brand(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String slug, String logo, boolean isActive, LocalDateTime deletedAt, Map<String, Object> extras) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.logo = logo;
        this.isActive = isActive;
        this.deletedAt = deletedAt;
        this.extras = extras;
    }
}
