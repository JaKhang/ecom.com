package com.nlu.store.core.jdbc;

import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of JdbcOperations for database operations.
 */
public class DefaultJdbcOperations implements JdbcOperations {
    private final DataSource dataSource;
    private final NamingConvention namingConvention;
    private static final String NAMING_CONFIG_KEY = "app.db.naming-convention";

    public DefaultJdbcOperations(DataSource dataSource, PropertySource propertySource) {
        this.dataSource = dataSource;
        String configValue = propertySource.getProperty(NAMING_CONFIG_KEY);
        this.namingConvention = resolveConvention(configValue);
    }

    private NamingConvention resolveConvention(String value) {
        if (value == null || value.isBlank()) {
            return NamingConvention.SNAKE_CASE;
        }

        try {
            return NamingConvention.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NamingConvention.SNAKE_CASE;
        }
    }

    @Override
    public Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public <U> Optional<U> executeQuery(String sql, ResultSetExtractor<U> resultSetExtractor, Object... parameters) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            setParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                U data = resultSetExtractor.extractData(resultSetReader(resultSet));
                return Optional.ofNullable(data);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Query execution failed: " + sql, e);
        }
    }

    private ResultSetReader resultSetReader(ResultSet resultSet) {
        return new JdbcResultSetReader(resultSet);
    }

    @Override
    public <U> List<U> queryForList(String sql, RowMapper<U> mapper, Object... parameters) {
        return executeQuery(sql, new ListExtractor<>(mapper), parameters).orElse(Collections.emptyList());
    }

    @Override
    public <U> Stream<U> queryForStream(String sql, RowMapper<U> mapper, Object... parameters) {
        return Stream.empty();
    }

    @Override
    public <T> List<T> queryForList(String baseSql, Sort sort, RowMapper<T> rowMapper, Object... args) {
        String sql = new StringJoiner(" ")
                .add(baseSql)
                .add(buildOrderByClause(sort))
                .toString();

        return queryForList(sql, rowMapper, args);
    }


    @Override
    public <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, RowMapper<U> mapper, Object... params) {
        // Nên chạy count trước, nếu total = 0 thì không cần query data
        int total = count(countSql, params);
        if (total == 0) return Page.of(Collections.emptyList(), 0, pageable);
        String sql = buildPagingSql(querySql, pageable);
        List<U> content = queryForList(sql, mapper, params);

        return Page.of(content, total, pageable);
    }

    @Override
    public <U> Page<U> queryForPage(String querySql, String countSql, Pageable pageable, ResultSetExtractor<List<U>> mapper, Object... params) {
        int total = count(countSql, params);
        if (total == 0) return Page.of(Collections.emptyList(), 0, pageable);


        String sql = buildPagingSql(querySql, pageable);
        List<U> content = executeQuery(sql, mapper, params).orElse(Collections.emptyList());

        return Page.of(content, total, pageable);
    }

    private String buildPagingSql(String querySql, Pageable pageable) {
        return new StringJoiner(" ")
                .add(querySql)
                .add(buildOrderByClause(pageable.getSort()))
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
        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }, parameters).orElse(0);
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> batchParameters) throws SQLException {
        try (Connection connection = connect()) {
            return executeBatch(connection, sql, batchParameters);
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
    public <U> Optional<U> executeTransaction(TransactionExecutor<U> executor) {
        try (Connection connection = connect()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                U result = executor.execute(connection);
                connection.commit();
                return Optional.ofNullable(result);
            } catch (Exception e) {
                connection.rollback();
                throw new DataAccessException("Transaction failed, rolled back", e);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Transaction connection failed", e);
        }
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
                } else if (parameter instanceof Enum<?>) {
                    statement.setString(parameterIndex, ((Enum<?>) parameter).name().toLowerCase());
                } else {
                    statement.setObject(parameterIndex, parameter);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to set parameters", e);
        }
    }


    private String buildOrderByClause(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "";
        }

        String orderBy = sort.getOrders().stream()
                .map(order -> {
                    String property = namingConvention.convert(order.getProperty());

                    // 🛡️ SECURITY CHECK: Quan trọng!
                    // Chỉ cho phép các ký tự an toàn: chữ cái, số, gạch dưới (_), và dấu chấm (.)
                    // Ngăn chặn việc inject mã độc như: "id; DROP TABLE users; --"
                    if (!property.matches("^[a-zA-Z0-9_.]+$")) {
                        throw new IllegalArgumentException("Invalid sort property (potential SQL Injection): " + property);
                    }

                    // Lấy hướng sắp xếp (ASC/DESC) từ Enum Direction
                    String direction = order.getDirection().isAscending() ? "ASC" : "DESC";

                    return property + " " + direction;
                })
                .collect(Collectors.joining(", "));

        // Trả về chuỗi rỗng nếu không có gì để sort, ngược lại thêm từ khóa ORDER BY
        return orderBy.isEmpty() ? "" : "ORDER BY " + orderBy;
    }
}
