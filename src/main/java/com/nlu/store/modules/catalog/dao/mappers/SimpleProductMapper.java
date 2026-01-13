package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.ProductStatus;
import com.nlu.store.modules.catalog.model.SimpleObject;
import com.nlu.store.modules.catalog.model.SimpleProduct;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SimpleProductMapper implements RowMapper<SimpleProduct> {

    private final String prefix;
    private final SimpleObjectMapper brandMapper;

    public SimpleProductMapper() {
        this("", null);
    }


    public SimpleProductMapper(String productPrefix, String brandPrefix) {
        this.prefix = (productPrefix == null) ? "" : productPrefix;

        // Nếu có brandPrefix, khởi tạo mapper con. Nếu không, để null.
        if (brandPrefix != null) {
            this.brandMapper = new SimpleObjectMapper(brandPrefix, "logo");
        } else {
            this.brandMapper = null;
        }
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public SimpleProduct mapRow(ResultSetReader rs, int row) throws SQLException {
        // --- 1. Map Product Fields ---
        SimpleProduct.SimpleProductBuilder builder = SimpleProduct.builder()
                .id(rs.getULID(column("id")))
                .createdAt(rs.getLocalDateTime(column("created_at")))
                .updatedAt(rs.getLocalDateTime(column("updated_at")))
                .name(rs.getString(column("name")))
                .slug(rs.getString(column("slug")))
                .thumbnail(rs.getString(column("thumbnail")))
                .description(rs.getString(column("description")))
                .shortDescription(rs.getString(column("short_description")))
                .isFeatured(rs.getBoolean(column("is_featured")))
                .minPrice(rs.getBigDecimal(column("min_price")))
                .maxPrice(rs.getBigDecimal(column("max_price")))
                .status(rs.getEnum(column("status"), ProductStatus.class))
                .specsSnapshot(rs.getJson(column("specs_snapshot")));


        SimpleObject brand = null;

        if (brandMapper != null) {
            String brandIdCol = brandMapper.column("id");

            // Chỉ map nếu cột brand ID có dữ liệu
            if (rs.hasColumn(brandIdCol) && rs.getULID(brandIdCol) != null) {
                brand = brandMapper.mapRow(rs, row);
            }
        }

        if (brand == null) {
            ULID fkBrandId = rs.getULID(column("brand_id"));
            if (fkBrandId != null) {
                brand = SimpleObject.builder().id(fkBrandId).build();
            }
        }
        builder.brand(brand);
        Map<String, Object> extras = new HashMap<>();
        if (rs.hasColumn(column("rating_avg"))) {
            extras.put("ratingAvg", rs.getDouble(column("rating_avg")));
        }
        if (rs.hasColumn(column("reviews_count"))) {
            extras.put("reviewsCount", rs.getInt(column("reviews_count")));
        }

        builder.extras(extras);

        return builder.build();
    }
}
