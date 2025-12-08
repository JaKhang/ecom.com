package com.nlu.store.core.dao;

public interface Convertor<S, T> {
    T convert(S s);
}
