package com.nlu.store.core.data.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Criteria {

    public enum Logic {
        AND, OR
    }


    // ... (Các phần cũ: Node, Logic, predicates...)

    // 1. Danh sách các bảng cần Join
    private final List<CriteriaJoin> criteriaJoins = new ArrayList<>();

    // 2. Các hàm Join (Fluent API)

    /**
     * Inner Join
     *
     * @param table     Tên bảng (vd: "categories")
     * @param alias     Tên giả (vd: "c")
     * @param condition Điều kiện ON (vd: "root.category_id = c.id")
     */
    public Criteria innerJoin(String table, String alias, String condition) {
        criteriaJoins.add(new CriteriaJoin(CriteriaJoin.Type.INNER, table, alias, condition));
        return this;
    }

    public Criteria leftJoin(String table, String alias, String condition) {
        criteriaJoins.add(new CriteriaJoin(CriteriaJoin.Type.LEFT, table, alias, condition));
        return this;
    }

    // Getter cho SqlGen dùng
    public List<CriteriaJoin> getJoins() {
        return criteriaJoins;
    }


    public static class Node {
        public final Logic logic;         // Nối với phần tử trước bằng AND hay OR?
        public final Predicate predicate; // Điều kiện đơn: age > 18
        public final Criteria group;      // Nhóm điều kiện: (A AND B)

        // Constructor cho Predicate đơn
        public Node(Logic logic, Predicate predicate) {
            this.logic = logic;
            this.predicate = predicate;
            this.group = null;
        }

        // Constructor cho Group (Criteria con)
        public Node(Logic logic, Criteria group) {
            this.logic = logic;
            this.predicate = null;
            this.group = group;
        }
    }

    private final List<Node> nodes = new ArrayList<>();

    private Criteria() {
    }

    public static Criteria of() {
        return new Criteria();
    }

    // =========================================================================
    // Standard JPA-style Methods
    // =========================================================================

    public Criteria equal(String attribute, Object value) {
        return add(Logic.AND, attribute, Predicate.Operator.EQUAL, value);
    }

    public Criteria notEqual(String attribute, Object value) {
        return add(Logic.AND, attribute, Predicate.Operator.NOT_EQUAL, value);
    }

    public Criteria greaterThan(String attribute, Number value) {
        return add(Logic.AND, attribute, Predicate.Operator.GREATER_THAN, value);
    }

    public Criteria greaterThanOrEqualTo(String attribute, Number value) {
        return add(Logic.AND, attribute, Predicate.Operator.GREATER_THAN_OR_EQUAL, value);
    }

    public Criteria lessThan(String attribute, Number value) {
        return add(Logic.AND, attribute, Predicate.Operator.LESS_THAN, value);
    }

    public Criteria lessThanOrEqualTo(String attribute, Number value) {
        return add(Logic.AND, attribute, Predicate.Operator.LESS_THAN_OR_EQUAL, value);
    }

    public Criteria like(String attribute, String pattern) {
        return add(Logic.AND, attribute, Predicate.Operator.LIKE, pattern);
    }

    public Criteria in(String attribute, Collection<?> values) {
        return add(Logic.AND, attribute, Predicate.Operator.IN, values);
    }

    public Criteria isNull(String attribute) {
        return add(Logic.AND, attribute, Predicate.Operator.IS_NULL, null);
    }

    public Criteria isNotNull(String attribute) {
        return add(Logic.AND, attribute, Predicate.Operator.IS_NOT_NULL, null);
    }

    // =========================================================================
    // New Methods: Support AND / OR Grouping
    // =========================================================================

    /**
     * Nối một Criteria khác vào hiện tại bằng AND
     * Ví dụ: ... AND ( ... )
     */
    public Criteria and(Criteria other) {
        if (other != null && !other.isEmpty()) {
            this.nodes.add(new Node(Logic.AND, other));
        }
        return this;
    }

    /**
     * Nối một Criteria khác vào hiện tại bằng OR
     * Ví dụ: ... OR ( ... )
     */
    public Criteria or(Criteria other) {
        if (other != null && !other.isEmpty()) {
            this.nodes.add(new Node(Logic.OR, other));
        }
        return this;
    }

    // =========================================================================
    // Core Logic (Updated)
    // =========================================================================

    private Criteria add(Logic logic, String key, Predicate.Operator op, Object value) {
        // Null-Safety Check
        if (value != null || op == Predicate.Operator.IS_NULL || op == Predicate.Operator.IS_NOT_NULL) {
            if (value instanceof String && ((String) value).isBlank()) return this;
            if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return this;

            // Thêm vào danh sách Node thay vì Predicate
            nodes.add(new Node(logic, new Predicate(key, op, value)));
        }
        return this;
    }

    // Getter mới trả về List<Node> để SqlGen xử lý đệ quy
    public List<Node> getNodes() {
        return nodes;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
