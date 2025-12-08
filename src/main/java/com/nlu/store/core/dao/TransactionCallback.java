package com.nlu.store.core.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface    TransactionCallback<T> {
    T doCallBack(Connection connection) throws SQLException;
}
