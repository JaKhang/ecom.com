package com.nlu.store.core.dao;

import com.nlu.store.core.data.Sort;

import java.util.StringJoiner;

public class SortToSqlConvertor implements Convertor<Sort, String> {

    @Override
    public String convert(Sort sort) {
        if (sort.isUnsorted())
            return "";
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (var order : sort) {
            stringJoiner.add(order.getProperty() + " " + order.getDirection());
        }

        return "ORDER BY " + stringJoiner;
    }
}
