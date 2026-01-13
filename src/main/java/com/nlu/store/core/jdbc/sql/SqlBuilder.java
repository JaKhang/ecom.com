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
}
