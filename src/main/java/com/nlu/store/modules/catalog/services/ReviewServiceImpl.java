package com.nlu.store.modules.catalog.services;


import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dao.ReviewDAO;
import com.nlu.store.modules.catalog.dto.ReviewRequest;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.model.review.ReviewStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReviewServiceImpl implements ReviewService{
    private final ReviewDAO reviewDAO;

    @Inject
    public ReviewServiceImpl(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @Override
    public Page<Review> findVisibleByProductId(ULID productId, Pageable pageable){
        return reviewDAO.findVisibleByProductId(productId, pageable);
    }

    @Override
    public List<Review> findUserReview(ULID productId, ULID userId) {
        return reviewDAO.findUserReview(productId, userId);
    }

    @Override
    public ULID create(ULID userId, ReviewRequest request) {
        Review review = Review.builder()
                .id(ULID.fast())
                .rating(request.getRating())
                .comment(request.getComment())
                .isVerified(false)
                .userId(userId)
                .productId(request.getProductId())
                .status(ReviewStatus.PENDING)
                .build();
        return reviewDAO.create(review);
    }
}
