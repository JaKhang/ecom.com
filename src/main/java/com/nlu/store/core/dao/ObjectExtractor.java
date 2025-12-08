package com.nlu.store.core.dao;



import java.sql.SQLException;

public class ObjectExtractor<T> implements ResultSetExtractor<T> {

    private RowMapper<T> mapper;

    public ObjectExtractor(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    public ObjectExtractor() {

    }

    @Override
    public T extractData(ResultSetReader reader) throws SQLException {
        if (reader.next()) {
            return mapper == null ? (T) reader.getRawResultSet().getObject(1) : mapper.mapRow(reader, 1);
        } else {
            return null;
        }
    }
}
