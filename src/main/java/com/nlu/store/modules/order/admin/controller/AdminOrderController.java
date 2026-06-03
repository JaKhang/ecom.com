package com.nlu.store.modules.order.admin.controller;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.services.OrderService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/admin/orders/*"})
public class AdminOrderController extends AbstractController {
    @Inject
    private OrderService service;
    @Override
    protected void doGet(HttpContext ctx) {
        switch (ctx.countRequestPathSegments()){
            case 2 ->  index(ctx);
            case 3 -> details(ctx);
            default -> ctx.sendError(404, "page.notfound");
        }
    }

    private void details(HttpContext ctx) {
        System.out.println(ctx.getRequestPath());
        ULID id = ctx.getPathVariable("/admin/orders/{id}", "id", ULID.class);
        Order order = service.findById(id).orElseThrow(() -> new ResourceNotFoundException("order.notfount"));
        ctx.setAttribute("order", order);
        ctx.view("admin/order/details");
    }

    private void index(HttpContext ctx) {
        ctx.view("admin/order/index");
    }


}
