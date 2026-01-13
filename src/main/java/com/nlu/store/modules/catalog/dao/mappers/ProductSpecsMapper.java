package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.details.ProductSpecs;

import java.sql.SQLException;

public class ProductSpecsMapper implements RowMapper<ProductSpecs> {
    private final String prefix;

    public ProductSpecsMapper() {
        this.prefix = "";
    }

    public ProductSpecsMapper(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public ProductSpecs mapRow(ResultSetReader reader, int row) throws SQLException {
        return ProductSpecs.builder()
                .id(reader.getULID(column("id")))
                .attributeLabel(reader.getString(column("attribute_label")))
                .value(reader.getString(column("value")))
                .unit(reader.getString(column("unit")))
                .isHighlight(reader.getBoolean(column("is_highlight")))
                .build();
    }
}
