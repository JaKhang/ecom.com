package com.nlu.store.modules.catalog.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/home", "/trang-chu"})
public class HomeController extends AbstractController {
    @Override
    protected void doGet(HttpContext ctx) {

        ctx.view("index");
    }
}
