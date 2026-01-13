package com.nlu.store.core.jdbc.query;

public interface OneRelationStep<T, R> {

    /**
     * Defines a custom raw SQL condition for the join.
     * <p>
     * Example: <code>.on("a.status = 'ACTIVE' AND a.user_id = b.id")</code>
     *
     * @param sqlCondition The raw SQL string to be used after the ON keyword.
     */
    JoinBuilder<T, R> on(String sqlCondition);

    /**
     * Joins a column from the Source table (T) to the default Primary Key of the Target table (R).
     * <p>
     * Logic: <code>ON T.sourceColumn = R.id</code>
     *
     * @param sourceColumn The column name in the Source table (T) acting as the Foreign Key.
     */
    OneRelationStep<T, R> joinColumns(String sourceColumn);

    /**
     * Joins a specific column from the Target table (R) with a specific column from the Source table (T).
     * <p>
     * Logic: <code>ON T.sourceColumn = R.targetColumn</code>
     *
     * @param targetColumn The column name in the Target table (R) (often the referenced PK).
     * @param sourceColumn The column name in the Source table (T) (often the FK).
     */
    OneRelationStep<T, R> joinColumns(String sourceColumn, String targetColumn);


    /**
     * Joins using a target column (Foreign Key) and transitions to JoinBuilder.
     * <p>
     * Example: <code>.joinColumn("user_id").select("email")</code>
     */
    JoinBuilder<T, R> joinColumn(String targetColumn);

    /**
     * Joins using explicit columns and transitions to JoinBuilder.
     * <p>
     * Example: <code>.joinColumn("user_id", "id").select("email")</code>
     */
    JoinBuilder<T, R> joinColumn(String targetColumn, String sourceColumn);
}

