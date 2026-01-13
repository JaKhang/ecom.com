package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.jdbc.sql.SqlBuilder;
import com.nlu.store.modules.catalog.dao.mappers.ReviewMapper;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.model.review.ReviewStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReviewDaoImpl implements ReviewDAO {
    private final JdbcOperations jdbc;
    private final ReviewMapper mapper = new ReviewMapper("rv_");



    @Inject
    public ReviewDaoImpl(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Page<Review> findVisibleByProductId(ULID productId, Pageable pageable) {
        String sql = SELECT_SQL + " WHERE pr.status = ? AND pr.is_verified_purchase = true AND pr.product_id = ?";
        String countSql = "SELECT COUNT(*) FROM product_reviews pr WHERE pr.status = ? AND pr.is_verified_purchase = true AND pr.product_id = ?";
        return jdbc.queryForPage(sql, countSql, pageable, mapper, ReviewStatus.APPROVED, productId);
    }

    @Override
    public List<Review> findUserReview(ULID productId, ULID userId) {
        String sql = SELECT_SQL + " WHERE u.id = ? AND pr.product_id = ?";
        return jdbc.queryForList(sql, mapper, userId, productId);
    }

    @Override
    public ULID create(Review review) {
        ULID id = review.getId() == null ? ULID.fast() : review.getId();
        var builder = SqlBuilder.insert("product_reviews")
                .map("id", id)
                .map("product_id", review.getProductId())
                .map("comment", review.getComment())
                .map("rating", review.getRating())
                .map("user_id", review.getUserId())
                .map("status", review.getStatus())
                .map("is_verified_purchase", review.isVerified());
        jdbc.update(builder.getSql(), builder.getParams());
        return id;
    }

    private static final String SELECT_SQL = "SELECT u.id AS rv_user_id, u.full_name AS rv_user_full_name, u.avatar AS rv_user_avatar, pr.id AS rv_id, pr.comment AS rv_comment, pr.status AS rv_status, pr.is_verified_purchase AS rv_is_verified_purchase, pr.created_at AS rv_created_at, pr.updated_at AS rv_updated_at, pr.rating AS rv_rating FROM product_reviews pr LEFT JOIN users u ON pr.user_id = u.id";
}
