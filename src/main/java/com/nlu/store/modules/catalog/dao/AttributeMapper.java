package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.dao.ResultSetReader;
import com.nlu.store.core.dao.RowMapper;
import com.nlu.store.modules.catalog.model.Attribute;
import com.nlu.store.modules.catalog.model.AttributeType;

import java.sql.SQLException;


public class AttributeMapper implements RowMapper<Attribute> {

    private final String prefix;
    // Bạn có thể đặt default là "" hoặc "attribute_" tùy thói quen
    private static final String DEFAULT_PREFIX = "";

    public AttributeMapper(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    // Constructor mặc định
    public AttributeMapper() {
        this(DEFAULT_PREFIX);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public Attribute mapRow(ResultSetReader reader, int row) throws SQLException {
        return Attribute.builder()
                // 1. Fields từ AbstractModel
                // Sử dụng column("id") sẽ tự động ghép prefix + "id"
                .id(reader.getULID(column("id")))
                .createdAt(reader.getLocalDateTime(column("created_at")))
                .updatedAt(reader.getLocalDateTime(column("updated_at")))

                // 2. Fields của Attribute
                .name(reader.getString(column("name")))
                .code(reader.getString(column("code")))

                // Map Enum
                .type(reader.getEnum(column("type"), AttributeType.class))

                .unit(reader.getString(column("unit")))
                .isVariantAxis(reader.getBoolean(column("is_variant_axis")))
                .isFilterable(reader.getBoolean(column("is_filterable")))
                .build();
    }
}

