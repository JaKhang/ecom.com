package com.nlu.store.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A functional interface designed to encapsulate a block of database operations
 * that should be executed within a single transactional context.
 * <p>
 * This pattern allows the infrastructure to handle the complexities of
 * transaction management (begin, commit, rollback) and connection lifecycle,
 * while the implementation focuses purely on the business logic.
 * </p>
 *
 * @param <T> the type of the result object returned by the callback.
 *            Use {@code Void} if no result is needed.
 * @author Ja Khang
 * @version 1.0
 */
@FunctionalInterface
public interface TransactionExecutor<T> {

    /**
     * Executes the database operations using the provided {@link Connection}.
     * <p>
     * Implementation code should not manually close the connection or
     * commit/rollback the transaction unless specifically required by the
     * architectural design, as the caller (usually a Transaction Manager)
     * handles these lifecycle events.
     * </p>
     *
     * @param connection the active database connection to be used for operations.
     * @return a result object of type {@code T}, or {@code null}.
     * @throws SQLException if a database access error occurs during execution.
     */
    T execute(Connection connection) throws SQLException;
}
