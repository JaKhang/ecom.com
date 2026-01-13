package com.nlu.store.core.jdbc;

import java.sql.SQLException;

/**
 * An interface used by {@code JdbcTemplate} or similar data access components
 * for mapping rows of a {@link ResultSetReader} on a per-row basis.
 * <p>
 * Implementations of this interface perform the actual work of mapping each row
 * to a result object (such as a DTO or Domain Model).
 * </p>
 *
 * @param <T> the type of the result object produced by this mapper.
 * @author NLU Store Team
 * @version 1.0
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Maps a single row of data from the {@link ResultSetReader} into an object of type {@code T}.
     *
     * @param reader the reader wrapping the current row of the result set.
     * @param row    the current row number (starting from 0 or 1 depending on implementation).
     * @return the populated result object.
     * @throws SQLException if a database access error occurs or mapping fails.
     */
    T mapRow(ResultSetReader reader, int row) throws SQLException;

    /**
     * Defines an optional prefix for column names.
     * <p>
     * This is particularly useful when performing SQL {@code JOIN} operations where
     * multiple tables might have overlapping column names (e.g., "u_id" vs "p_id").
     * </p>
     *
     * @return the string prefix to be prepended to column names; defaults to an empty string.
     */
    default String prefix() {
        return "";
    }

    /**
     * Combines the defined {@link #prefix()} with a specific column name.
     * <p>
     * Use this helper method inside {@link #mapRow} to ensure that column lookups
     * remain consistent with the mapper's prefix configuration.
     * </p>
     *
     * @param name the base name of the column.
     * @return the fully qualified column label (prefix + name).
     */
    default String column(String name) {
        return prefix() + name;
    }
}
