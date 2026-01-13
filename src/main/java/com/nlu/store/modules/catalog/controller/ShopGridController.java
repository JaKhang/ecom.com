package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.catalog.dto.SearchRequest;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import com.nlu.store.modules.catalog.services.CategoryService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/shop", "/cua-hang"})
public class ShopGridController extends AbstractController {

    @Inject
    private CategoryService categoryService;


    @Override
    protected void doGet(HttpContext ctx) {
        System.out.println("/shop");

        ctx.view("client/shop/grid");
    }


    @Override
    protected void doPost(HttpContext ctx) {
        SearchRequest searchRequest = ctx.getBody(SearchRequest.class);
        System.out.println(searchRequest);
        if (searchRequest.getCategoryId() != null) {
            SimpleCategory simpleCategory = categoryService.findById(searchRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category.notfound"));
            ctx.redirect(String.format("/%s/%s?keyword=%s", ctx.getMessage("routes.category", "danh-muc"), simpleCategory.getSlug(), searchRequest.getKeyword()));
        } else {
            ctx.redirect(String.format("/%s?keyword=%s", ctx.getMessage("routes.shop", "shop"), searchRequest.getKeyword()));

        }

    }
}
