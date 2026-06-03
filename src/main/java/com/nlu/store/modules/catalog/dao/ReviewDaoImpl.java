package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.jdbc.sql.SqlBuilder;
import com.nlu.store.core.jdbc.sql.WhereBuilder;
import com.nlu.store.modules.catalog.dao.mappers.ReviewMapper;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.model.review.ReviewStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
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
    public Optional<Review> findById(ULID reviewId) {
        String sql = SELECT_SQL + " WHERE pr.id = ?";
        return jdbc.queryForObject(sql, mapper, reviewId);
    }

    @Override
    public Page<Review> findVisibleByProductId(ULID productId, Pageable pageable) {
        String sql = SELECT_SQL + " WHERE pr.status = ? AND pr.product_id = ?";
        String countSql = "SELECT COUNT(*) FROM product_reviews pr WHERE pr.status = ?  AND pr.product_id = ?";
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

    @Override
    public void update(Review review) {
        var builder = SqlBuilder.update("product_reviews")
                .set("product_id", review.getProductId())
                .set("comment", review.getComment())
                .set("rating", review.getRating())
                .set("user_id", review.getUserId())
                .set("status", review.getStatus())
                .set("is_verified_purchase", review.isVerified())
                .where("id", review.getId());
        jdbc.update(builder.getSql(), builder.getParams());
    }

    @Override
    public void recalcRating(ULID productId) {
        jdbc.update("CALL sp_recalc_product_rating(?)", productId);
    }

    @Override
    public Page<Review> find(Pageable page, String keyword, ReviewStatus status) {
        WhereBuilder sql = WhereBuilder.create(SELECT_SQL);
        WhereBuilder count = WhereBuilder.create(COUNT_SQL);
        if (status != null) {
            sql.and("pr.status = ?", status);
            count.and("pr.status = ?", status);
        }
        if (keyword != null) {
        }


        return jdbc.queryForPage(sql.getSql(), count.getSql(), page, mapper, sql.getParams());
    }

    private static final String SELECT_SQL = "SELECT u.id AS rv_user_id, u.full_name AS rv_user_full_name, u.avatar AS rv_user_avatar, pr.id AS rv_id, pr.comment AS rv_comment, pr.status AS rv_status, pr.is_verified_purchase AS rv_is_verified_purchase, p.name AS rv_product_name, pr.created_at AS rv_created_at, pr.updated_at AS rv_updated_at, pr.rating AS rv_rating, pr.product_id AS rv_product_id FROM product_reviews pr LEFT JOIN users u ON pr.user_id = u.id LEFT JOIN products p ON p.id = product_id";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM product_reviews pr LEFT JOIN users u ON pr.user_id = u.id LEFT JOIN products p ON p.id = product_id";
}
