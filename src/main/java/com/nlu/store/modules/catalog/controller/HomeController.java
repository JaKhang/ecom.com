package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.data.PageRequest;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.catalog.model.Brand;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.services.BrandService;
import com.nlu.store.modules.catalog.services.CategoryService;
import com.nlu.store.modules.catalog.services.ProductService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.List;

@WebServlet(urlPatterns = {"/home", "/trang-chu"})
public class HomeController extends AbstractController {
    @Inject
    private ProductService productService;

    @Inject
    private CategoryService categoryService;
    @Inject
    private BrandService brandService;

    @Override
    protected void doGet(HttpContext ctx) {
        Pageable pageable = new PageRequest(1, 5, Sort.by(Sort.Direction.DESC, "p.is_featured", "p.release_date"));
        List<SimpleProduct> news = productService.findAll(pageable).getContent();

        List<SimpleCategory> categories = categoryService.findAll(Sort.by("c.sort_order"));
        List<Brand> brands = brandService.findAll(Sort.by("b.sort_order"));

        ctx.setAttribute("news", news);
        ctx.setAttribute("categories", categories);
        ctx.setAttribute("brands", brands);
        ctx.view("index");
    }
}
