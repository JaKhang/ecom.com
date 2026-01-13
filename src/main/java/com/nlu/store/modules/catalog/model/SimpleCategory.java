package com.nlu.store.modules.catalog.model;

import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class SimpleCategory extends AbstractModel implements Serializable {
    private String name;
    private String slug;
    private String icon;        // Map từ cột 'icon'
    private int productCount;   // Map từ kết quả COUNT()


    @Builder
    public SimpleCategory(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                          String name, String slug, String icon, int productCount) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.icon = icon;
        this.productCount = productCount;
    }

    public SimpleCategory() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleCategory{");
        sb.append("name='").append(name).append('\'');
        sb.append("id='").append(getId()).append('\'');
        sb.append(", slug='").append(slug).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", productCount=").append(productCount);
        sb.append('}');
        return sb.toString();
    }
}
