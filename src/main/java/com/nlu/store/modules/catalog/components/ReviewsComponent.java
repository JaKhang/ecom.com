package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jawire.JawireUpdateException;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.core.jawire.Pagination;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.services.ReviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
public class ReviewsComponent extends Pagination<Review> {

    @Inject
    private ReviewService service;

    @Model
    @Getter
    @Setter
    private ULID productId;

    @Override
    public Page<Review> getPage(Pageable pageable) {
        return service.findVisibleByProductId(productId, pageable);
    }

    @Override
    public void mount() {
        productId = context.getAttribute("productId", ULID.class);
        System.out.println(productId);
        super.mount();

    }

    @Override
    public String view() {
        return "shop/review";
    }
}
