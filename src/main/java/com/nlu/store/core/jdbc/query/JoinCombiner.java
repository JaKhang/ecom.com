package com.nlu.store.core.jdbc.query;

import java.util.function.BiConsumer;

public interface JoinCombiner<T, R> {
    ExecuteQueryStep<T> map(BiConsumer<T, R> combiner);
}
