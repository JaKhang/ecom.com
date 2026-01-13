package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.user.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/verify-pending")
public class VerifyPendingController extends AbstractController {
    @Inject
    private AuthService authService;

    @Override
    protected void doGet(HttpContext ctx) {
        if (ctx.isAuthenticated()){
            if (!ctx.authentication().isVerified()){
                ctx.view("client/verify-pending");
            } else {
                ctx.redirect("/");
            }
        }

    }


}
