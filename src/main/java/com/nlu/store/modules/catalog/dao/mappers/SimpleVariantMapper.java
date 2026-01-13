package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.SimpleVariant;

import java.sql.SQLException;

public class SimpleVariantMapper implements RowMapper<SimpleVariant> {

    @Override
    public SimpleVariant mapRow(ResultSetReader reader, int row) throws SQLException {
        return SimpleVariant.builder()
                .id(reader.getULID(column("id")))
                .price(reader.getBigDecimal(column("price")))
                .originalPrice(reader.getBigDecimal(column("original_price")))
                .productId(reader.getULID(column("product_id")))
                .thumbnail(reader.getString(column("thumbnail")))
                .sku(reader.getString(column("sku")))
                .name(reader.getString(column("name")))
                .ratingAvg(reader.getDouble(column("rating_avg")))
                .reviewsCount(reader.getInt(column("reviews_count")))
                .slug(reader.getString(column("product_slug")))
                .stocks(reader.getInt(column("stocks")))
                .build();
    }

    @Override
    public String prefix() {
        return "v_";
    }
}
