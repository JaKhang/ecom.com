package com.nlu.store.modules.user.dao;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.user.models.Role;

import java.sql.SQLException;


public class RoleMapper implements RowMapper<Role> {
    private final String prefix;
    private static final String DEFAULT_PREFIX = "";

    public RoleMapper(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    // Constructor mặc định
    public RoleMapper() {
        this(DEFAULT_PREFIX);
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public Role mapRow(ResultSetReader reader, int rowNum) throws SQLException {
        return Role.builder()
                // Sử dụng method column() từ interface để tự động ghép prefix
                // Ví dụ: prefix="r_" -> column("id") thành "r_id"
                .id(reader.getULID(column("id")))

                // Các trường từ AbstractModel
                .createdAt(reader.getLocalDateTime(column("created_at")))
                .updatedAt(reader.getLocalDateTime(column("updated_at")))

                .name(reader.getString(column("name")))
                .code(reader.getString(column("code")))
                .build();
    }
}

