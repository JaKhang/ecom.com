package com.nlu.store.core.data.specification;

public class Predicate {
    public final String attribute;
    public final Operator operator;
    public final Object value;

    public Predicate(String attribute, Operator operation, Object value) {
        this.attribute = attribute;
        this.operator = operation;
        this.value = value;
    }

    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL("<="),
        LIKE("LIKE"),
        IN("IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL");

        public final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }
    }
}

