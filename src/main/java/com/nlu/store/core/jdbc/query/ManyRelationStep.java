package com.nlu.store.core.jdbc.query;


/**
 * Interface defining the configuration steps for a One-to-Many or Many-to-One relationship join.
 * <p>
 * T: Source Table Entity
 * R: Target Table Entity
 */
public interface ManyRelationStep<T, R> {

    /**
     * Appends a custom raw SQL condition to the ON clause.
     * <p>
     * Example: <code>.on("a.id = b.user_id AND b.status = 'PENDING'")</code>
     *
     * @param sqlCondition The raw SQL string to be appended.
     * @return The current step instance for further configuration.
     */
    ManyRelationStep<T, R> on(String sqlCondition);

    // -----------------------------------------------------------------------
    // Configuration Methods (Stay in ManyRelationStep)
    // -----------------------------------------------------------------------

    /**
     * Configures the join using a specific column in the Target table (R),
     * assuming the Source table (T) uses its default Primary Key.
     * <p>
     * <b>Note:</b> This method returns {@link ManyRelationStep}, allowing you to add
     * more conditions (like {@code .on()}) before finalizing.
     *
     * @param targetColumn The column name in the Target table (R).
     * @return The current step instance for further configuration.
     */
    ManyRelationStep<T, R> joinColumns(String targetColumn);

    /**
     * Configures the join using explicit columns for both Target and Source tables.
     * <p>
     * <b>Note:</b> This method returns {@link ManyRelationStep}, allowing you to add
     * more conditions before finalizing.
     *
     * @param targetColumn The column name in the Target table (R).
     * @param sourceColumn The column name in the Source table (T).
     * @return The current step instance for further configuration.
     */
    ManyRelationStep<T, R> joinColumns(String targetColumn, String sourceColumn);

    // -----------------------------------------------------------------------
    // Transition Methods (Move to JoinBuilder)
    // -----------------------------------------------------------------------

    /**
     * Configures the join using a target column and <b>immediately transitions</b>
     * to the {@link JoinBuilder}.
     * <p>
     * Use this when you are done configuring the join and want to select fields.
     *
     * @param targetColumn The column name in the Target table (R).
     * @return The JoinBuilder to proceed with field selection.
     */
    JoinBuilder<T, R> joinColumn(String targetColumn);

    /**
     * Configures the join using explicit columns and <b>immediately transitions</b>
     * to the {@link JoinBuilder}.
     * <p>
     * Use this when you are done configuring the join and want to select fields.
     *
     * @param targetColumn The column name in the Target table (R).
     * @param sourceColumn The column name in the Source table (T).
     * @return The JoinBuilder to proceed with field selection.
     */
    JoinBuilder<T, R> joinColumn(String targetColumn, String sourceColumn);

    // -----------------------------------------------------------------------
    // Junction Table Support (Fixed return types)
    // -----------------------------------------------------------------------

    /**
     * Enables joining via an inferred intermediate junction table (Many-to-Many).
     * <p>
     * <b>Optimization:</b> Returns {@link ManyRelationStep} instead of void to allow chaining.
     *
     * @return The current step instance for further configuration.
     */
    JoinBuilder<T, R> joinTable();

    /**
     * Enables joining via a specific intermediate junction table.
     * <p>
     * <b>Optimization:</b> Returns {@link ManyRelationStep} instead of void to allow chaining.
     *
     * @param tableName The name of the junction table.
     * @return The current step instance for further configuration.
     */
    JoinBuilder<T, R> joinTable(String tableName);
}

