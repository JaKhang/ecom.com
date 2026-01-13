package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/account", "/tai-khoan"})
public class AccountController extends AbstractController {
    @Override
    protected void doGet(HttpContext ctx) {
        ctx.view("client/account");
    }
}
