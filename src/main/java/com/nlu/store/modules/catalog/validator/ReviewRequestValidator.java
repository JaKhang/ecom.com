package com.nlu.store.modules.catalog.validator;

import com.nlu.store.core.validation.ValidateHelper;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.catalog.dto.ReviewRequest;

public class ReviewRequestValidator implements Validator<ReviewRequest> {
    @Override
    public ValidateResult validate(ReviewRequest reviewRequest) {
        return ValidateHelper.of(reviewRequest)
                .isInRange("rating", 0, 5, "validation.review.rating.range")
                .notNull("rating", "validation.review.rating.notnull")
                .notBlank("comment", "validation.review.commnet.notblank")
                .validate();
    }
}
