package com.nlu.store.core.jdbc.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to build SQL Strings dynamically using Fluent API.
 */
public class SqlBuilder {

    private SqlBuilder() {}

    public static InsertBuilder insert(String tableName) {
        return new InsertBuilder(tableName);
    }
    public static UpdateBuilder update(String tableName) {
        return new UpdateBuilder(tableName);
    }


    // =========================================================================
    // INSERT BUILDER
    // =========================================================================
    public static class InsertBuilder {
        private final String tableName;
        private final List<String> columns = new ArrayList<>();
        private final List<Object> values = new ArrayList<>();

        public InsertBuilder(String tableName) {
            this.tableName = tableName;
        }


        public InsertBuilder map(String columnName, Object value) {
            this.columns.add(columnName);
            this.values.add(value);
            return this;
        }


        public InsertBuilder column(String columnName) {
            this.columns.add(columnName);
            return this;
        }


        public InsertBuilder columns(String... columnNames) {
            Collections.addAll(this.columns, columnNames);
            return this;
        }

        public String getSql() {
            if (columns.isEmpty()) {
                throw new IllegalStateException("No columns defined for INSERT");
            }

            String cols = String.join(", ", columns);
            String placeholders = columns.stream()
                    .map(c -> "?")
                    .collect(Collectors.joining(", "));

            return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, cols, placeholders);
        }


        public Object[] getParams() {
            return values.toArray();
        }
    }

    public static class UpdateBuilder {
        private final String tableName;
        private final List<String> setClauses = new ArrayList<>();
        private final List<Object> setValues = new ArrayList<>();
        private final List<String> whereClauses = new ArrayList<>();
        private final List<Object> whereValues = new ArrayList<>();

        public UpdateBuilder(String tableName) {
            this.tableName = tableName;
        }

        // Gán giá trị cho cột: SET column = value
        public UpdateBuilder set(String columnName, Object value) {
            this.setClauses.add(columnName + " = ?");
            this.setValues.add(value);
            return this;
        }

        // Điều kiện bằng: WHERE column = value
        public UpdateBuilder where(String columnName, Object value) {
            this.whereClauses.add(columnName + " = ?");
            this.whereValues.add(value);
            return this;
        }

        // Điều kiện tùy chỉnh: WHERE age > ?
        public UpdateBuilder whereCondition(String condition, Object value) {
            this.whereClauses.add(condition);
            this.whereValues.add(value);
            return this;
        }

        // Điều kiện không tham số: WHERE is_active = 1
        public UpdateBuilder whereSql(String condition) {
            this.whereClauses.add(condition);
            return this;
        }

        public String getSql() {
            if (setClauses.isEmpty()) {
                throw new IllegalStateException("No columns defined for UPDATE");
            }

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ").append(tableName).append(" SET ");
            sql.append(String.join(", ", setClauses));

            if (!whereClauses.isEmpty()) {
                sql.append(" WHERE ").append(String.join(" AND ", whereClauses));
            }

            return sql.toString();
        }

        public Object[] getParams() {
            List<Object> allParams = new ArrayList<>(setValues);
            allParams.addAll(whereValues);
            return allParams.toArray();
        }
    }

}
