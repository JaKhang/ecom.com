package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/cart", "/gio-hang"})
public class CartController extends AbstractController {

    @Override
    protected void doGet(HttpContext ctx) {
        ctx.view("client/shop/cart");
    }
}
