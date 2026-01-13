package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.modules.catalog.dto.ReviewRequest;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.services.ReviewService;
import com.nlu.store.modules.catalog.validator.ReviewRequestValidator;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/reviews")
public class ReviewController extends AbstractController {

    @Inject
    private ReviewService service;

    @Override
    protected void doPost(HttpContext ctx) {
        String redirect = ctx.getParam("redirectUrl").orElse("/");
        BindingResult<ReviewRequest> rs = ctx.getBody(ReviewRequest.class, new ReviewRequestValidator());
        if (rs.hasError()){
            ctx.alertError("review.invalid");
            ctx.redirect(redirect);
            return;
        }
        Authentication authentication = ctx.authentication();;
        service.create(authentication.id(), rs.data());
        ctx.alertSuccess("review.success");
        ctx.redirect(redirect);

    }
}
