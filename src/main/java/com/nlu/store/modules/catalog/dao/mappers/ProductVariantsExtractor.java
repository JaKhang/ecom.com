package com.nlu.store.modules.catalog.dao.mappers;


import com.nlu.store.core.jdbc.ResultSetExtractor;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.details.ProductVariant;
import com.nlu.store.modules.catalog.model.details.VariantValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductVariantsExtractor implements ResultSetExtractor<List<ProductVariant>> {

    @Override
    public List<ProductVariant> extractData(ResultSetReader reader) throws SQLException {
        // 1. Dùng LinkedHashMap để giữ thứ tự variant (ORDER BY pv.id)
        Map<ULID, ProductVariant> variantsMap = new LinkedHashMap<>();

        while (reader.next()) {
            ULID varId = reader.getULID("var_id");

            // 2. Lấy hoặc tạo mới ProductVariant
            ProductVariant variant = variantsMap.get(varId);
            if (variant == null) {
                variant = ProductVariant.builder()
                        .id(varId)
                        // ✅ CẬP NHẬT: Map thêm 2 trường thời gian
                        .createdAt(reader.getLocalDateTime("var_created_at"))
                        .updatedAt(reader.getLocalDateTime("var_updated_at"))

                        .sku(reader.getString("var_sku"))
                        .name(reader.getString("var_name"))
                        .price(reader.getBigDecimal("var_price"))
                        .originalPrice(reader.getBigDecimal("var_original_price"))
                        .stocks(reader.getInt("var_stock_quantity"))
                        .weight(reader.getInt("var_weight_g"))
                        .isDefault(reader.getBoolean("var_is_default"))
                        .values(new ArrayList<>())
                        .build();

                variantsMap.put(varId, variant);
            }

            ULID attId = reader.getULID("att_id");
            ULID optId = reader.getULID("opt_id");

            if (attId != null && optId != null) {
                VariantValue val = VariantValue.builder()
                        .attributeId(attId)
                        .optionId(optId)
                        // ✅ CẬP NHẬT: Đổi tên cột từ "att_name" thành "att_label" theo SQL mới
                        .attributeLabel(reader.getString("att_label"))
                        .optionValue(reader.getString("opt_value"))
                        .build();

                variant.getValues().add(val);
            }
        }

        return new ArrayList<>(variantsMap.values());
    }
}

