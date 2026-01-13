package com.nlu.store.modules.order.admin.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/test")
public class AdminOrderController extends AbstractController {
    @Override
    protected void doGet(HttpContext ctx) {
        ctx.view("admin/dashboard");
    }
}
