package com.nlu.store.core.jdbc;

public interface Convertor<S, T> {
    T convert(S s);
}
