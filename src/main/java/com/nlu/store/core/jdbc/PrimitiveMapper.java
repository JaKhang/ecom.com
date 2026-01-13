package com.nlu.store.core.jdbc;

import java.sql.SQLException;

public class PrimitiveMapper<T> implements RowMapper<T> {

    private Class<T> claszz;

    public PrimitiveMapper(Class<T> claszz) {
        this.claszz = claszz;
    }

    @Override
    public T mapRow(ResultSetReader reader, int row) throws SQLException {
        return reader.get(1, claszz);
    }
}
