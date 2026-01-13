package com.nlu.store.core.jdbc.query;

public interface LotusClient {
    <T> SelectStep<T> query(String table);

    <T> SelectStep<T> query(String table, String alias);
}
