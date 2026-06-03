package com.nlu.store.modules.catalog.admin;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/admin/reviews")
public class AdminReviewController extends AbstractController {
    @Override
    protected void doGet(HttpContext ctx) {
        ctx.view("admin/review/index");
    }
}
