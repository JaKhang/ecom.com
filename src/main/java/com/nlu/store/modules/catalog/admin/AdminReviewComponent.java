package com.nlu.store.modules.catalog.admin;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jawire.Action;
import com.nlu.store.core.jawire.JawireUpdateException;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.core.jawire.Pagination;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.model.review.ReviewStatus;
import com.nlu.store.modules.catalog.services.ReviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
public class AdminReviewComponent extends Pagination<Review> {

    @Model
    @Getter
    @Setter
    private String keyword;
    @Model
    @Setter
    @Getter
    private ReviewStatus status;

    @Override
    public Page<Review> getPage(Pageable pageable) {
        return reviewService.find(pageable, keyword, status);
    }

    @Override
    public String view() {
        return "admin/review/table";
    }

    @Action
    public void updateStatus(ULID id, ReviewStatus status){
        reviewService.updateStatus(id, status);
    }

    @Action
    public void verify(ULID id){
        reviewService.verify(id);
    }

    @Override
    protected void authorize(Authentication authentication) {
        if (authentication == null) throw new JawireUpdateException("unauthorized",401, "unauthorized");
        if (!authentication.authorities().contains("ROLE_ADMIN")) throw new JawireUpdateException("forbidden",403, "forbidden");
    }
    @Inject
    private ReviewService reviewService;
}
