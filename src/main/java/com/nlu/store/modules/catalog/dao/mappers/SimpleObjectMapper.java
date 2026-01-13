package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.SimpleObject;

import java.sql.SQLException;

public class SimpleObjectMapper implements RowMapper<SimpleObject> {

    private final String prefix;
    private final String imageColumnName;

    public SimpleObjectMapper(String prefix) {
        this(prefix, "");
    }


    public SimpleObjectMapper(String prefix, String imageColumnName) {
        this.prefix = (prefix == null) ? "" : prefix;
        this.imageColumnName = imageColumnName;
    }

    @Override
    public String prefix() {
        return this.prefix;
    }

    @Override
    public SimpleObject mapRow(ResultSetReader reader, int row) throws SQLException {
        // 1. Sử dụng hàm column() từ Interface.
        // column("id") sẽ tự động gọi getPrefix() + "id" -> VD: "brand_id"
        String idCol = column("id");

        if (reader.getULID(idCol) == null) {
            return null;
        }

        return SimpleObject.builder()
                .id(reader.getULID(idCol))
                .name(reader.getString(column("name")))
                .slug(reader.getStringIfPresent(column("slug")))
                .image(reader.getStringIfPresent(column(imageColumnName)))
                .build();
    }
}
