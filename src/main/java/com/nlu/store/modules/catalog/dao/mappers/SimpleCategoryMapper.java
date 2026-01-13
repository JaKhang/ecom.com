package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.SimpleCategory;

import java.sql.SQLException;

public class SimpleCategoryMapper implements RowMapper<SimpleCategory> {

    private final String prefix;

    public SimpleCategoryMapper() {
        this("");
    }

    public SimpleCategoryMapper(String prefix) {
        this.prefix = (prefix == null) ? "" : prefix;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public SimpleCategory mapRow(ResultSetReader rs, int row) throws SQLException {
        return SimpleCategory.builder()
                .id(rs.getULID(column("id")))
                .createdAt(rs.getLocalDateTime(column("created_at")))
                .updatedAt(rs.getLocalDateTime(column("updated_at")))
                .name(rs.getString(column("name")))
                .slug(rs.getString(column("slug")))
                .icon(rs.getString(column("icon")))
                .productCount(rs.getInt(column("products_count")))
                .build();
    }
}
