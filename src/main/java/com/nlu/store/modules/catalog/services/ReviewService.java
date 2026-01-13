package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dto.ReviewRequest;
import com.nlu.store.modules.catalog.model.review.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Page<Review> findVisibleByProductId(ULID productId, Pageable pageable);

    List<Review> findUserReview(ULID productID, ULID userId);

    ULID create(ULID userId, ReviewRequest request);
}
