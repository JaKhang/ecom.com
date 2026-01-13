package com.nlu.store.core.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build JDBC parameters using a Fluent API.
 * Supports both single-row parameters (for simple queries)
 * and multi-row parameters (for batch operations).
 */
public class JdbcParamBuilder {

    private final List<Object[]> batchRows = new ArrayList<>();
    private List<Object> currentRow = new ArrayList<>();

    private JdbcParamBuilder() {
        // Private constructor to enforce static factory usage
    }

    /**
     * Starts a new parameter builder.
     */
    public static JdbcParamBuilder create() {
        return new JdbcParamBuilder();
    }

    /**
     * Adds a parameter to the current row.
     * @param value The value to add (can be null).
     */
    public JdbcParamBuilder param(Object value) {
        this.currentRow.add(value);
        return this;
    }

    /**
     * Conditionally adds a parameter.
     * Useful for dynamic queries.
     */
    public JdbcParamBuilder paramIf(boolean condition, Object value) {
        if (condition) {
            this.currentRow.add(value);
        }
        return this;
    }

    /**
     * Completes the current row and prepares for the next row.
     * Used specifically for Batch operations.
     */
    public JdbcParamBuilder endRow() {
        if (!currentRow.isEmpty()) {
            this.batchRows.add(currentRow.toArray());
            this.currentRow = new ArrayList<>(); // Reset for next row
        }
        return this;
    }

    /**
     * Builds the parameter array for a single query/update.
     * @return Object[] of parameters.
     */
    public Object[] build() {
        return currentRow.toArray();
    }

    /**
     * Builds the list of parameter arrays for a batch execution.
     * Automatically calls endRow() if the current row has pending data.
     * @return List<Object[]> for executeBatch.
     */
    public List<Object[]> buildBatch() {
        // If there is data in currentRow that hasn't been flushed, flush it now.
        if (!currentRow.isEmpty()) {
            this.batchRows.add(currentRow.toArray());
        }
        return new ArrayList<>(this.batchRows);
    }
}
