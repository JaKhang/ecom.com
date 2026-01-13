package com.nlu.store.core.jdbc.sql;

import com.nlu.store.core.data.specification.Criteria;
import com.nlu.store.core.data.specification.Predicate;
import com.nlu.store.core.data.specification.Predicate.Operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class AnsiSqlCompiler implements CriteriaCompiler {

    // Regex để kiểm tra tên cột an toàn (chỉ cho phép chữ cái, số, gạch dưới và dấu chấm)
    private static final Pattern VALID_COLUMN_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    @Override
    public CompiledQuery compile(Criteria criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return new CompiledQuery("", "", Collections.emptyList());
        }

        StringBuilder whereSb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Bắt đầu duyệt cây, coi root là một group để tránh ngoặc bao ngoài cùng không cần thiết
        visitNodes(criteria.getNodes(), whereSb, params, true);

        return new CompiledQuery("", whereSb.toString(), params);
    }

    private void visitNodes(List<Criteria.Node> nodes, StringBuilder sb, List<Object> params, boolean isGroupRoot) {
        if (nodes == null || nodes.isEmpty()) return;

        // Only add parentheses if this is a sub-group AND it has multiple elements
        if (!isGroupRoot) sb.append("(");

        boolean isFirst = true;
        for (Criteria.Node node : nodes) {

            // 1. Handle Logic (AND/OR)
            if (!isFirst) {
                sb.append(" ").append(node.logic.name()).append(" ");
            }

            // 2. Handle Node Content
            if (node.predicate != null) {
                // Case A: Leaf Node (Predicate)
                compilePredicate(node.predicate, sb, params);
            } else if (node.group != null) {
                // Case B: Branch Node (Group)
                List<Criteria.Node> childNodes = node.group.getNodes();

                // [FIX]: Optimization to remove redundant parentheses.
                // If a group has only 1 item, e.g., "... OR (b=1)", we simplify to "... OR b=1".
                // We treat it as a "root" so the recursive call won't add surrounding '()'.
                boolean treatAsRoot = childNodes.size() <= 1;

                visitNodes(childNodes, sb, params, treatAsRoot);
            }

            isFirst = false;
        }

        if (!isGroupRoot) sb.append(")");
    }


    private void compilePredicate(Predicate p, StringBuilder sb, List<Object> params) {
        // [Security] Validate tên cột để tránh SQL Injection
        if (!isValidColumn(p.attribute)) {
            throw new IllegalArgumentException("Invalid column name: " + p.attribute);
        }

        sb.append(p.attribute).append(" ");

        // Xử lý các toán tử đặc biệt không cần tham số hoặc cấu trúc khác lạ
        if (p.operator == Operator.IS_NULL || p.operator == Operator.IS_NOT_NULL) {
            sb.append(getOperatorSymbol(p.operator));
        }
        else if (p.operator == Operator.IN) {
            sb.append(getOperatorSymbol(p.operator));
            handleInOperator(sb, params, p.value);
        }
        else {
            // Các toán tử so sánh thông thường (=, >, <, LIKE...)
            sb.append(getOperatorSymbol(p.operator));

            if (p.value != null) {
                sb.append(" ?");
                params.add(p.value);
            } else {
                // Defensive coding: Tránh sinh ra SQL lỗi kiểu "age ="
                throw new IllegalArgumentException("Value cannot be null for operator " + p.operator);
            }
        }
    }

    private void handleInOperator(StringBuilder sb, List<Object> params, Object value) {
        if (value instanceof Collection<?> col && !col.isEmpty()) {
            sb.append(" (");
            // Dùng String.join hoặc loop đơn giản để xử lý dấu phẩy
            int size = col.size();
            for (int i = 0; i < size; i++) {
                sb.append("?");
                if (i < size - 1) sb.append(", ");
            }
            sb.append(")");
            params.addAll(col);
        } else {
            // Fallback: 1=0 an toàn hơn (NULL) để đảm bảo logic sai mà không lỗi cú pháp DB
            sb.append(" (NULL)");
        }
    }

    protected String getOperatorSymbol(Operator op) {
        return switch (op) {
            case EQUAL -> "=";
            case NOT_EQUAL -> "<>";
            case GREATER_THAN -> ">";
            case GREATER_THAN_OR_EQUAL -> ">=";
            case LESS_THAN -> "<";
            case LESS_THAN_OR_EQUAL -> "<=";
            case LIKE -> "LIKE";
            case IN -> "IN";
            case IS_NULL -> "IS NULL";
            case IS_NOT_NULL -> "IS NOT NULL";
        };
    }

    private boolean isValidColumn(String column) {
        return column != null && VALID_COLUMN_PATTERN.matcher(column).matches();
    }
}
