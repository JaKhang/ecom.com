package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Data;

@Data
public class SimpleObject {
    private ULID id;
    private String name;
    private String slug;
    private String image;

    @Builder
    public SimpleObject(ULID id, String name, String slug, String image) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.image = image;
    }

    public SimpleObject() {
    }
}
