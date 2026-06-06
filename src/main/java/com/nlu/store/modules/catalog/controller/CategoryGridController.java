package com.nlu.store.modules.catalog.controller;


import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import com.nlu.store.modules.catalog.services.CategoryService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/danh-muc/*", "/category/*"})
public class CategoryGridController extends AbstractController {

    @Inject
    private CategoryService categoryService;

    @Override
    protected void doGet(HttpContext ctx) {
        if (ctx.countRequestPathSegments() != 2) throw new ResourceNotFoundException("page.notfound");
        String slug = ctx.getPathVariable("/category|danh-muc/{slug}", "slug");
        SimpleCategory category = categoryService.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("category.notfound"));
        System.out.println(category);
        ctx.setAttribute("category", category);
        ctx.view("client/shop/grid");
    }
}
