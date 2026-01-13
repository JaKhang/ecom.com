package com.nlu.store.core.jdbc;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The central facade for all JDBC interactions within the application.
 * <p>
 * {@code JdbcOperations} simplifies the use of JDBC by handling the creation and
 * release of resources, which helps avoid common errors like forgetting to close
 * a connection. It provides a variety of methods including:
 * <ul>
 *     <li>Standard CRUD operations (Query, Update, Batch).</li>
 *     <li>Advanced pagination and dynamic sorting.</li>
 *     <li>Transaction management via functional callbacks.</li>
 * </ul>
 * </p>
 *
 * @author Ja Khang
 * @version 1.0
 */
public interface JdbcOperations {

    /**
     * Obtains a raw connection to the database.
     * <p>
     * Use this only when you need low-level control that the template methods
     * do not provide. Remember to close it using {@link #closeConnection(Connection)}.
     * </p>
     *
     * @return a valid {@link Connection} object.
     * @throws SQLException if a database access error occurs.
     */
    Connection connect() throws SQLException;

    /**
     * Executes a query and processes the entire {@link ResultSet} using a
     * {@link ResultSetExtractor}.
     *
     * @param <U>                the type of the result object.
     * @param sql                the SQL query to execute.
     * @param resultSetExtractor the extractor that will iterate over the rows.
     * @param parameters         arguments to bind to the query.
     * @return an {@link Optional} containing the extracted data, or empty if no data was found.
     */
    <U> Optional<U> executeQuery(String sql, ResultSetExtractor<U> resultSetExtractor, Object... parameters);

    /**
     * Executes a query and maps each row to a Java object via a {@link RowMapper}.
     *
     * @param <U>        the type of the result elements.
     * @param sql        the SQL query to execute.
     * @param mapper     the mapper to use per row.
     * @param parameters arguments to bind to the query.
     * @return a {@link List} of mapped objects; never {@code null}.
     */
    <U> List<U> queryForList(String sql, RowMapper<U> mapper, Object... parameters);

    /**
     * Executes a query and returns the result as a Stream.
     * <p>
     * This is highly efficient for large result sets as it processes rows one by one
     * using a cursor, rather than loading everything into memory.
     * </p>
     * <p>
     * <b>Note:</b> The returned Stream must be closed to release database resources
     * (Connection, PreparedStatement, and ResultSet). Always use try-with-resources.
     * </p>
     *
     * @param <U>        the type of the result elements.
     * @param sql        the SQL query to execute.
     * @param mapper     the row mapper to use per row.
     * @param parameters arguments to bind to the query.
     * @return a Stream of mapped objects.
     * @throws DataAccessException if a database access error occurs.
     */
    <U> Stream<U> queryForStream(String sql, RowMapper<U> mapper, Object... parameters);


    /**
     * Executes a query with dynamic sorting capabilities.
     *
     * @param <T>       the type of the result elements.
     * @param baseSql   the SQL query (without an ORDER BY clause).
     * @param sort      the sorting criteria (column and direction).
     * @param rowMapper the mapper to use per row.
     * @param args      arguments to bind to the query.
     * @return a sorted {@link List} of objects.
     */
    <T> List<T> queryForList(String baseSql, Sort sort, RowMapper<T> rowMapper, Object... args);

    /**
     * Performs a paginated query, returning a {@link Page} object.
     * <p>
     * This method handles the complexity of combining the data results with
     * the total record count for UI pagination components.
     * </p>
     *
     * @param <U>      the type of the result elements.
     * @param querySql the SQL for fetching the page slice.
     * @param countSql the SQL for fetching the total number of records.
     * @param pageable pagination information (page number, size).
     * @param mapper   the row mapper for the data.
     * @param params   arguments to bind to both queries.
     * @return a {@link Page} containing the data and metadata.
     */
    <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, RowMapper<U> mapper, Object... params);

    /**
     * Performs a paginated query using a {@link ResultSetExtractor} for the data slice.
     *
     * @param <U>      the type of the result elements.
     * @param querySql the SQL for fetching the page slice.
     * @param countSql the SQL for fetching the total number of records.
     * @param pageable pagination information.
     * @param mapper   the extractor for the data slice.
     * @param params   arguments to bind to the queries.
     * @return a {@link Page} containing the data and metadata.
     */
    <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, ResultSetExtractor<List<U>> mapper, Object... params);

    /**
     * Executes a query and returns the first row as a {@link Map}.
     * <p>
     * Useful for quick data inspection where a dedicated DTO is not yet available.
     * </p>
     *
     * @param querySql the SQL query.
     * @param params   arguments to bind to the query.
     * @return a Map where keys are column names and values are column data.
     */
    Map<String, Object> queryForMap(String querySql, Object... params);

    /**
     * Executes a query intended to result in a single object.
     *
     * @param <U>        the type of the result object.
     * @param sql        the SQL query.
     * @param mapper     the row mapper.
     * @param parameters arguments to bind to the query.
     * @return an {@link Optional} containing the object, or empty if no row was found.
     */
    <U> Optional<U> queryForObject(String sql, RowMapper<U> mapper, Object... parameters);

    /**
     * Executes a query that returns a single numeric count.
     *
     * @param sql        the SQL query (e.g., SELECT COUNT(*)...).
     * @param parameters arguments to bind to the query.
     * @return the resulting count.
     */
    int count(String sql, Object... parameters);

    /**
     * Executes multiple update statements as a single batch for optimized performance.
     *
     * @param sql             the SQL update/insert string.
     * @param batchParameters a list of parameter arrays.
     * @return an array of update counts (one for each command in the batch).
     * @throws SQLException if a database access error occurs.
     */
    int[] executeBatch(String sql, List<Object[]> batchParameters) throws SQLException;

    /**
     * Executes a batch update using an existing connection.
     *
     * @param connection      the connection to use.
     * @param sql             the SQL update/insert string.
     * @param batchParameters a list of parameter arrays.
     * @return an array of update counts.
     * @throws SQLException if a database access error occurs.
     */
    int[] executeBatch(Connection connection, String sql, List<Object[]> batchParameters) throws SQLException;

    /**
     * Executes a single SQL statement (INSERT, UPDATE, or DELETE).
     *
     * @param sql        the SQL query.
     * @param parameters arguments to bind to the query.
     * @return the number of rows affected.
     */
    int update(String sql, Object... parameters);

    /**
     * Executes a single SQL update using an existing connection.
     *
     * @param connection the connection to use.
     * @param sql        the SQL query.
     * @param parameters arguments to bind to the query.
     * @return the number of rows affected.
     */
    int update(Connection connection, String sql, Object... parameters);

    /**
     * Wraps a set of operations in a transaction.
     * <p>
     * If the callback throws an exception, the transaction will be rolled back
     * automatically. Otherwise, it will be committed upon successful completion.
     * </p>
     *
     * @param <U>      the result type.
     * @param callback the block of code to execute.
     * @return an {@link Optional} containing the result, or empty if the transaction failed.
     */
    <U> Optional<U> executeTransaction(TransactionExecutor<U> callback);

    /**
     * Safely closes a {@link Connection}, suppressing checked exceptions.
     *
     * @param connection the connection to close; may be {@code null}.
     * @throws DataAccessException if closing the connection fails.
     */
    default void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessException("Failed to close connection", e);
            }
        }
    }

    /**
     * Safely closes a {@link ResultSet}, suppressing checked exceptions.
     *
     * @param resultSet the result set to close; may be {@code null}.
     * @throws DataAccessException if closing the result set fails.
     */
    default void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DataAccessException("Failed to close ResultSet", e);
            }
        }
    }
}
