package com.nlu.store.core.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for JDBC operations, providing methods for database connectivity, querying, and resource management.
 */
public interface JdbcOperations {



    /**
     * Provides a connection to the database.
     *
     * @return the database connection
     * @throws SQLException if a database access error occurs
     */
    Connection connect() throws SQLException;

    /**
     * Executes a query and processes the result set using a ResultSetExtractor.
     *
     * @param sql                the SQL query to execute
     * @param resultSetExtractor the extractor used to process the result set
     * @param parameters         the parameters for the query
     * @param <U>                the type of the result
     * @return an Optional containing the result of processing the result set, or an empty Optional if no result
     */
    <U> Optional<U> executeQuery(String sql, ResultSetExtractor<U> resultSetExtractor, Object... parameters);

    /**
     * Executes a query and maps the result set to a list of objects using a RowMapper.
     *
     * @param sql        the SQL query to execute
     * @param mapper     the row mapper to map each row
     * @param parameters the parameters for the query
     * @param <U>        the type of the result
     * @return a list of mapped objects, or an empty list if no results
     */
    <U> List<U> queryForList(String sql, RowMapper<U> mapper, Object... parameters);

    /**
     * Executes a paginated query and maps the result set to a page of objects.
     *
     * @param querySql the SQL query for retrieving data
     * @param countSql the SQL query for counting total rows
     * @param pageable the pagination information
     * @param mapper   the row mapper to map each row
     * @param params   the parameters for the query
     * @param <U>      the type of the result
     * @return a Page object containing the paginated results
     */
    <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, RowMapper<U> mapper, Object... params);

    <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, ResultSetExtractor<List<U>> mapper, Object... params);

    /**
     * Executes a query and maps the result set to a Map of column names and their corresponding values.
     *
     * @param querySql the SQL query to execute
     * @param params   the parameters for the query
     * @return a Map representing a single row, with column names as keys and values as the mapped data
     */
    Map<String, Object> queryForMap(String querySql, Object... params);

    /**
     * Executes a query and maps a single row to an object using a RowMapper.
     *
     * @param sql        the SQL query to execute
     * @param mapper     the row mapper to map the row
     * @param parameters the parameters for the query
     * @param <U>        the type of the result
     * @return an Optional containing the mapped object, or an empty Optional if no result
     */
    <U> Optional<U> queryForObject(String sql, RowMapper<U> mapper, Object... parameters);

    /**
     * Executes a query to count rows.
     *
     * @param sql        the SQL query to execute
     * @param parameters the parameters for the query
     * @return the count of rows as an integer
     */
    int count(String sql, Object... parameters);

    /**
     * Executes a batch update operation.
     *
     * @param sql             the SQL query to execute
     * @param batchParameters a list of parameter arrays for the batch
     * @return an array of update counts containing one element for each command in the batch
     * @throws SQLException if a database access error occurs
     */
    int[] executeBatch(String sql, List<Object[]> batchParameters) throws SQLException;

    /**
     * Executes a batch update operation using a provided connection.
     *
     * @param connection      the database connection to use
     * @param sql             the SQL query to execute
     * @param batchParameters a list of parameter arrays for the batch
     * @return an array of update counts containing one element for each command in the batch
     * @throws SQLException if a database access error occurs
     */
    int[] executeBatch(Connection connection, String sql, List<Object[]> batchParameters) throws SQLException;

    /**
     * Executes a single update operation.
     *
     * @param sql        the SQL query to execute
     * @param parameters the parameters for the query
     * @return the number of rows affected by the update
     */
    int update(String sql, Object... parameters);

    /**
     * Executes a single update operation using a provided connection.
     *
     * @param connection the database connection to use
     * @param sql        the SQL query to execute
     * @param parameters the parameters for the query
     * @return the number of rows affected by the update
     */
    int update(Connection connection, String sql, Object... parameters);

    /**
     * Executes a transaction with a custom callback.
     *
     * @param callback the transaction callback
     * @param <U>      the type of the result
     * @return an Optional containing the result of the transaction, or an empty Optional if the transaction fails
     */
    <U> Optional<U> executeTransaction(TransactionCallback<U> callback);

    /**
     * Closes the given connection safely.
     *
     * @param connection the connection to close
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
     * Closes the given ResultSet safely.
     *
     * @param resultSet the result set to close
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

    /**
     * Closes all resources managed by this operation.
     *
     * This method should be called to release any resources (e.g., connection pools, open connections)
     * when the application is shutting down or when the operations are no longer needed.
     */
    void close();

    Connection startTransaction();

    void endTransaction();
}
