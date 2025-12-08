package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/logout")
public class LogoutController extends AbstractController {
    @Override
    protected void doGet(HttpContext ctx) {
        ctx.setAuthentication(null);
        ctx.redirect("/login");
    }


}
