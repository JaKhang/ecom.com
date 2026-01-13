package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jdbc.ResultSetExtractor;
import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.modules.catalog.model.details.ProductImage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductImagesExtractor implements ResultSetExtractor<List<ProductImage>> {

    @Override
    public List<ProductImage> extractData(ResultSetReader reader) throws SQLException {
        Map<ULID, ProductImage> imagesMap = new LinkedHashMap<>();

        while (reader.next()) {
            ULID imageId = reader.getULID("img_id");

            ProductImage image = imagesMap.get(imageId);
            if (image == null) {
                image = ProductImage.builder()
                        .id(imageId)
                        .url(reader.getString("img_url"))
                        .altText(reader.getString("img_alt_text"))
                        .attributeValueId(new ArrayList<>()) // Khởi tạo danh sách variantIds
                        .build();

                imagesMap.put(imageId, image);
            }

            ULID variantId = reader.getULID("attribute_value_id");
            if (variantId != null) {
                image.getAttributeValueId().add(variantId);
            }
        }

        return new ArrayList<>(imagesMap.values());
    }
}
