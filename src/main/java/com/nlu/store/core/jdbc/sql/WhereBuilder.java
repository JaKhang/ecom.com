package com.nlu.store.core.jdbc.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to dynamically build SQL WHERE clauses with safe parameter handling.
 * Supports AND, IN, NOT IN, and conditional appending.
 */
public class WhereBuilder {

    private final String baseSql;
    private final List<String> conditions = new ArrayList<>();
    private final List<Object> params = new ArrayList<>();

    private WhereBuilder(String baseSql) {
        this.baseSql = baseSql;
    }

    /**
     * Factory method to start building.
     * @param baseSql The initial SQL (e.g., "SELECT * FROM users")
     */
    public static WhereBuilder create(String baseSql) {
        return new WhereBuilder(baseSql);
    }

    // =========================================================================
    // BASIC CONDITIONS
    // =========================================================================

    /**
     * Add a condition WITHOUT parameters.
     * Example: .and("deleted_at IS NULL")
     */
    public WhereBuilder and(String condition) {
        if (condition != null && !condition.isBlank()) {
            this.conditions.add(condition);
        }
        return this;
    }

    /**
     * Add a condition WITH a single parameter.
     * Example: .and("status = ?", "ACTIVE")
     */
    public WhereBuilder and(String condition, Object value) {
        if (condition != null && !condition.isBlank()) {
            this.conditions.add(condition);
            this.params.add(value);
        }
        return this;
    }

    /**
     * Only add the condition if the value is NOT null (and not empty if String).
     * Useful for Search/Filter forms where fields are optional.
     */
    public WhereBuilder andIfPresent(String condition, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            this.conditions.add(condition);
            this.params.add(value);
        }
        return this;
    }

    // =========================================================================
    // IN / NOT IN CLAUSES
    // =========================================================================

    /**
     * Create an IN clause with a dynamic list of values.
     * Automatically generates the correct number of placeholders (e.g., "?, ?, ?").
     * Example: .andIn("category_id", List.of(1, 2, 3))
     */
    public WhereBuilder andIn(String column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            String placeholders = values.stream()
                    .map(v -> "?")
                    .collect(Collectors.joining(", "));

            this.conditions.add(column + " IN (" + placeholders + ")");
            this.params.addAll(values);
        }
        return this;
    }

    /**
     * Create a NOT IN clause with a dynamic list of values.
     * Example: .andNotIn("status", List.of("BANNED", "DELETED"))
     */
    public WhereBuilder andNotIn(String column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            String placeholders = values.stream()
                    .map(v -> "?")
                    .collect(Collectors.joining(", "));

            this.conditions.add(column + " NOT IN (" + placeholders + ")");
            this.params.addAll(values);
        }
        return this;
    }

    // =========================================================================
    // BUILD
    // =========================================================================

    /**
     * Constructs the final SQL string.
     * Automatically detects if 'WHERE' or 'AND' is needed.
     */
    public String getSql() {
        if (conditions.isEmpty()) {
            return baseSql;
        }

        // rudimentary check to see if baseSql already contains a WHERE clause
        String upperSql = baseSql.toUpperCase();
        String keyword = upperSql.contains(" WHERE ") ? " AND " : " WHERE ";

        return baseSql + keyword + String.join(" AND ", conditions);
    }

    /**
     * Returns the array of parameters matching the generated SQL placeholders.
     */
    public Object[] getParams() {
        return params.toArray();
    }
}