package com.nlu.store.core.dao;

import java.sql.SQLException;

public interface ResultSetExtractor<T> {

    T extractData(ResultSetReader reader) throws SQLException;

}
