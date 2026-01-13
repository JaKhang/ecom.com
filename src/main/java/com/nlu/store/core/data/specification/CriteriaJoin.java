package com.nlu.store.core.data.specification;

public class CriteriaJoin {

    public enum Type {
        INNER,
        LEFT,
        RIGHT;


    }

    public final Type type;
    public final String table;
    public final String alias;
    public final String condition;

    public CriteriaJoin(Type type, String table, String alias, String condition) {
        this.type = type;
        this.table = table;
        this.alias = alias;
        this.condition = condition;
    }
}
