package com.nlu.store.modules.catalog.dao.mappers;


import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.Brand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BrandMapper implements RowMapper<Brand> {
    private final String prefix;

    public BrandMapper() {
        this("");
    }

    public BrandMapper(String prefix) {
        this.prefix = (prefix == null) ? "" : prefix;
    }

    private String col(String name) {
        return prefix + name;
    }


    @Override
    public Brand mapRow(ResultSetReader rs, int row) throws SQLException {

        Map<String, Object> extras = new HashMap<>();

        if (rs.hasColumn(column("products_count"))) {
            extras.put("productsCount", rs.getInt(column("products_count")));
        }
        return Brand.builder()
                .id(rs.getULID(col("id")))
                .createdAt(rs.getLocalDateTime(col("created_at")))
                .updatedAt(rs.getLocalDateTime(col("updated_at")))
                .deletedAt(rs.getLocalDateTime(col("deleted_at")))
                .name(rs.getString(col("name")))
                .slug(rs.getString(col("slug")))
                .logo(rs.getString(col("logo")))
                .isActive(rs.getBoolean(col("is_active")))
                .extras(extras)
                .build();
    }

    @Override
    public String prefix() {
        return prefix;
    }
}
