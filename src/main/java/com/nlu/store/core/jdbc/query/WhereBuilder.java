package com.nlu.store.core.jdbc.query;

import java.util.Collection;

public interface WhereBuilder<T> {
    WhereBuilder<T> eq(String column, Object value);  // Equal (=)

    WhereBuilder<T> ne(String column, Object value);  // Not Equal (!=)

    WhereBuilder<T> gt(String column, Object value);  // Greater Than (>)

    WhereBuilder<T> lt(String column, Object value);  // Less Than (<)

    WhereBuilder<T> gte(String column, Object value); // Greater Than or Equal (>=)

    WhereBuilder<T> lte(String column, Object value); // Less Than or Equal (<=)

    WhereBuilder<T> like(String column, String value);       // LIKE 'value'

    WhereBuilder<T> notLike(String column, String value);    // NOT LIKE 'value'

    WhereBuilder<T> contains(String column, String value);   // LIKE '%value%'

    WhereBuilder<T> startsWith(String column, String value); // LIKE 'value%'

    WhereBuilder<T> endsWith(String column, String value);   // LIKE '%value'

    WhereBuilder<T> in(String column, Collection<?> values);

    WhereBuilder<T> notIn(String column, Collection<?> values);

    WhereBuilder<T> between(String column, Object min, Object max);

    WhereBuilder<T> isNull(String column);

    WhereBuilder<T> isNotNull(String column);

    WhereBuilder<T> eqIfPresent(String column, Object value);

}
