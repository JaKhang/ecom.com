package com.nlu.store.modules.catalog.dao.mappers;

import com.nlu.store.core.jdbc.ResultSetReader;
import com.nlu.store.core.jdbc.RowMapper;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.model.review.ReviewStatus;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public record ReviewMapper(String prefix) implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSetReader reader, int row) throws SQLException {



        return Review.builder()
                // Mapping thông tin Review (pr.*)
                .id(reader.getULID(column("id"))) // rv_id
                .rating(reader.getInt(column("rating"))) // rv_rating

                // Lưu ý: SQL alias của bạn đang viết là "rv_comment" (dư chữ n)
                // Nên ở đây mình map theo đúng alias trong SQL
                .comment(reader.getString(column("comment")))
                .productId(reader.getULIDIfPresent(column("productId")))
                .status(reader.getEnum(column("status"), ReviewStatus.class)) // rv_status -> Enum
                .isVerified(reader.getBoolean(column("is_verified_purchase"))) // rv_is_verified_purchase
                .createdAt(reader.getLocalDateTime(column("created_at"))) // rv_created_at
                .updatedAt(reader.getLocalDateTime(column("updated_at"))) // rv_updated_at

                // Mapping thông tin User (u.*)
                .productName(reader.getString(column("product_name")))
                .productId(reader.getULID(column("product_id")))
                .userId(reader.getULID(column("user_id"))) // rv_user_id -> ULID Type
                .username(reader.getString(column("user_full_name"))) // rv_user_full_name
                .avatar(reader.getString(column("user_avatar"))) // rv_user_avatar
                .build();
    }
}
