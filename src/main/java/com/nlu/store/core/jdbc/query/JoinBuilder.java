package com.nlu.store.core.jdbc.query;

import com.nlu.store.core.jdbc.RowMapper;

import java.util.function.Function;

public interface JoinBuilder<T, R> {

    /**
     * Selects specific columns from the joined table (Target R) and automatically applies aliases.
     * <p>
     * Mechanism:
     * <br> Input: <code>"id", "name"</code>
     * <br> SQL Output: <code>table_alias.id AS table_alias_id, table_alias.name AS table_alias_name</code>
     *
     * @param targetColumns The list of column names from the target table to select.
     * @return The current builder instance for chaining.
     */
    JoinBuilder<T, R> select(String... targetColumns);

    /**
     * Selects columns and provides a specific RowMapper instance immediately.
     * <p>
     * Use this when you have a pre-configured mapper that doesn't need a dynamic prefix.
     *
     * @param mapper The specific RowMapper to handle the result of these columns.
     * @param targetColumns The list of column names to select.
     * @return The current builder instance.
     */
    JoinCombiner<T, R> select(RowMapper<R> mapper, String... targetColumns);

    /**
     * Finalizes the join configuration by providing a Mapper Factory.
     * <p>
     * The builder will generate a unique alias prefix (e.g., "t2_") and pass it to this factory.
     * The factory should return a RowMapper configured to read columns starting with that prefix.
     *
     * @param mapperFactory A function that accepts a String (alias prefix) and returns a RowMapper<R>.
     * @return A JoinCombiner to merge the results of T and R.
     */
    JoinCombiner<T, R> mapper(Function<String, RowMapper<R>> mapperFactory);
}

