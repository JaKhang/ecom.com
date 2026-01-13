package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.PageRequest;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.details.ProductSpecs;
import com.nlu.store.modules.catalog.model.review.Review;
import com.nlu.store.modules.catalog.services.ProductService;
import com.nlu.store.modules.catalog.services.ReviewService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.List;

@WebServlet(urlPatterns = {"/san-pham/*", "/products/*"})
public class ProductDetailsController extends AbstractController {
    @Inject
    private ProductService productService;

    @Inject
    private ReviewService reviewService;

    @Override
    protected void doGet(HttpContext ctx) {
        if ((ctx.countRequestPathSegments() != 2)) throw new ResourceNotFoundException("page.notfound");
        String slug = ctx.getPathVariable("/products|san-pham/{slug}", "slug");
        SimpleProduct simpleProduct = productService.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("product.notfound"));
        List<ProductSpecs> productSpecs = productService.findProductSpecs(simpleProduct.getId());

        if (ctx.isAuthenticated()){
            Authentication authentication = ctx.authentication();
            List<Review> reviews = reviewService.findUserReview(simpleProduct.getId(), authentication.id());
            ctx.setAttribute("currentUserReviews", reviews);
        }


        ctx.setAttribute("product", simpleProduct);
        ctx.setAttribute("productSpecs", productSpecs);
        ctx.view("client/shop/details");
    }
}
