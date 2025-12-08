package com.nlu.store.core.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Default implementation of JdbcOperations for database operations.
 */
public class DefaultJdbcOperations implements JdbcOperations {
    private final DataSource dataSource;
    private final SortToSqlConvertor sortToSqlConvertor;

    public DefaultJdbcOperations(DataSource dataSource) {
        this.dataSource = dataSource;
        this.sortToSqlConvertor = new SortToSqlConvertor();
    }

    @Override
    public Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public <U> Optional<U> executeQuery(String sql, ResultSetExtractor<U> resultSetExtractor, Object... parameters) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            setParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                U data = resultSetExtractor.extractData(resultSetReader(resultSet));
                return data == null ? Optional.empty() : Optional.of(data);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Query execution failed", e);
        }
    }

    private ResultSetReader resultSetReader(ResultSet resultSet) {
        return new JdbcResultSetReader(resultSet);
    }

    @Override
    public <U> List<U> queryForList(String sql, RowMapper<U> mapper, Object... parameters) {
        return executeQuery(sql, new ListExtractor<>(mapper), parameters).orElseThrow(() ->
                new DataAccessException("Failed to execute query for list"));
    }

    @Override
    public <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, RowMapper<U> mapper, Object... params) {
        String sql = createPaginationSql(querySql, pageable);
        List<U> content = queryForList(sql, mapper, params);
        int total = count(countSql, params);

        return Page.of(content, total, pageable);
    }

    @Override
    public <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, ResultSetExtractor<List<U>> mapper, Object... params) {
        String sql = createPaginationSql(querySql, pageable);
        List<U> content = executeQuery(sql, mapper, params).orElseThrow(() ->
                new DataAccessException("Failed to execute query for list"));
        ;
        int total = count(countSql, params);
        return Page.of(content, total, pageable);
    }

    private String createPaginationSql(String querySql, Pageable pageable) {
        return new StringJoiner(" ")
                .add(querySql)
                .add(sortToSqlConvertor.convert(pageable.getSort()))
                .add("LIMIT")
                .add(String.valueOf(pageable.getLimit()))
                .add("OFFSET")
                .add(String.valueOf(pageable.getOffset()))
                .toString();
    }

    @Override
    public Map<String, Object> queryForMap(String querySql, Object... params) {
        return Map.of();
    }

    @Override
    public <U> Optional<U> queryForObject(String sql, RowMapper<U> mapper, Object... parameters) {
        return executeQuery(sql, new ObjectExtractor<>(mapper), parameters);
    }

    @Override
    public int count(String sql, Object... parameters) {
        return executeQuery(sql, new ObjectExtractor<>(new PrimitiveMapper<>(Integer.class)), parameters)
                .orElse(0);
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> batchParameters) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Object[] parameters : batchParameters) {
                setParameters(statement, parameters);
                statement.addBatch();
            }

            return statement.executeBatch();
        }
    }

    @Override
    public int[] executeBatch(Connection connection, String sql, List<Object[]> batchParameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] parameters : batchParameters) {
                setParameters(statement, parameters);
                statement.addBatch();
            }
            return statement.executeBatch();
        }
    }

    @Override
    public int update(String sql, Object... parameters) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Update operation failed", e);
        }
    }

    @Override
    public int update(Connection connection, String sql, Object... parameters) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Update operation failed", e);
        }
    }

    @Override
    public <U> Optional<U> executeTransaction(TransactionCallback<U> callback) {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                U result = callback.doCallBack(connection);
                connection.commit();
                return result == null ? Optional.empty() : Optional.of(result);
            } catch (SQLException e) {
                connection.rollback();
                throw new DataAccessException("Transaction failed, rolled back", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Transaction failed", e);
        }
    }

    @Override
    public void close() {
        // No-op: DataSource handles connection pooling and resource management
    }

    @Override
    public Connection startTransaction() {
        return null;
    }

    @Override
    public void endTransaction() {

    }

    /**
     * Safely sets parameters for a PreparedStatement.
     *
     * @param statement  the PreparedStatement
     * @param parameters the parameters to set
     */
    private void setParameters(PreparedStatement statement, Object... parameters) {
        try {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                int parameterIndex = i + 1;

                if (parameter == null) {
                    statement.setObject(parameterIndex, null);
                } else if (parameter instanceof String) {
                    statement.setString(parameterIndex, (String) parameter);
                } else if (parameter instanceof Integer) {
                    statement.setInt(parameterIndex, (Integer) parameter);
                } else if (parameter instanceof Boolean) {
                    statement.setBoolean(parameterIndex, (Boolean) parameter);
                } else if (parameter instanceof java.sql.Date) {
                    statement.setDate(parameterIndex, (java.sql.Date) parameter);
                } else if (parameter instanceof Timestamp) {
                    statement.setTimestamp(parameterIndex, (Timestamp) parameter);
                } else if (parameter instanceof Long) {
                    statement.setLong(parameterIndex, (Long) parameter);
                } else if (parameter instanceof Double) {
                    statement.setDouble(parameterIndex, (Double) parameter);
                } else if (parameter instanceof Float) {
                    statement.setFloat(parameterIndex, (Float) parameter);
                } else if (parameter instanceof Short) {
                    statement.setShort(parameterIndex, (Short) parameter);
                } else if (parameter instanceof Byte) {
                    statement.setByte(parameterIndex, (Byte) parameter);
                } else if (parameter instanceof BigDecimal) {
                    statement.setBigDecimal(parameterIndex, (BigDecimal) parameter);
                } else if (parameter instanceof LocalDate) {
                    statement.setDate(parameterIndex, java.sql.Date.valueOf((LocalDate) parameter));
                } else if (parameter instanceof LocalDateTime) {
                    statement.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf((LocalDateTime) parameter));
                } else if (parameter instanceof Locale) {
                    statement.setString(parameterIndex, ((Locale) parameter).toLanguageTag());
                } else if (parameter instanceof ULID) {
                    statement.setString(parameterIndex, parameter.toString());
                } else {
                    statement.setObject(parameterIndex, parameter);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to set parameters", e);
        }
    }


}
