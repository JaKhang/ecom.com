package com.nlu.store.modules.catalog.dao.mappers;


import com.nlu.store.core.jdbc.ResultSetExtractor;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.details.AttributeOption;
import com.nlu.store.modules.catalog.model.details.ProductAttribute;

import java.sql.SQLException;
import java.util.*;

public class ProductAttributesExtractor implements ResultSetExtractor<List<ProductAttribute>> {

    @Override
    public List<ProductAttribute> extractData(ResultSetReader reader) throws SQLException {
        Map<ULID, ProductAttribute> attributesMap = new LinkedHashMap<>();

        Map<ULID, Set<ULID>> processedOptionsMap = new HashMap<>();

        while (reader.next()) {
            ULID attId = reader.getULID("att_id");
            ProductAttribute attribute = attributesMap.get(attId);
            if (attribute == null) {
                attribute = ProductAttribute.builder()
                        .id(attId)
                        .code(reader.getString("att_code"))
                        .label(reader.getString("att_label"))
                        .position(reader.getInt("att_position"))
                        .visualType(reader.getString("att_visual_type"))
                        .options(new ArrayList<>())
                        .build();

                attributesMap.put(attId, attribute);
                processedOptionsMap.put(attId, new HashSet<>());
            }

            ULID optId = reader.getULID("opt_id");

            if (optId == null) {
                continue;
            }

            Set<ULID> processedOptions = processedOptionsMap.get(attId);

            if (!processedOptions.contains(optId)) {
                AttributeOption option = new AttributeOption(
                        optId,
                        reader.getString("opt_label"),
                        reader.getString("opt_visual_value"),
                        reader.getString("opt_visual_type")
                );

                attribute.getOptions().add(option);
                processedOptions.add(optId);
            }
        }

        return new ArrayList<>(attributesMap.values());
    }
}
