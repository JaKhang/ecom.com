package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.review.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDAO {

    Page<Review> findVisibleByProductId(ULID productId, Pageable pageable);

    List<Review> findUserReview(ULID productId, ULID userId);

    ULID create(Review review);
}
