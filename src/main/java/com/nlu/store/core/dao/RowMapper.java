package com.nlu.store.core.dao;

import java.sql.SQLException;

public interface RowMapper<T> {
    T mapRow(ResultSetReader reader, int row) throws SQLException;

    default String getPrefix() {
        return "";
    }

    default String column(String name) {
        return getPrefix() + name;
    }
}
