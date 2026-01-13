package com.nlu.store.core.jdbc.query;

import com.nlu.store.core.jdbc.RowMapper;

import java.util.function.Function;

public interface SelectStep<T> {
    SelectStep<T> select(String... cols);
    SelectStep<T> selectAll();
    SelectStep<T> select(RowMapper<T> mapper, String... columns);
    ExecuteQueryStep<T> mapper(Function<String, RowMapper<T>> mapperFactory);
}
