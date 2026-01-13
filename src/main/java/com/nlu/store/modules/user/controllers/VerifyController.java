package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.user.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.Optional;
@WebServlet("/verify")
public class VerifyController extends AbstractController {
    @Inject
    private AuthService authService;

    @Override
    protected void doGet(HttpContext ctx) {
        // 1. Lấy tham số từ URL
        Optional<String> emailOpt = ctx.getParam("email");
        Optional<String> tokenOpt = ctx.getParam("token");

        // 2. Validate tham số đầu vào
        if (emailOpt.isEmpty() || tokenOpt.isEmpty()) {
            // Sử dụng Alert Object + Static Factory Method
            throw new ResourceNotFoundException("");
        }
        try {
            authService.verify(emailOpt.get(), tokenOpt.get());
            ctx.alertSuccess("auth.verify.success");
            ctx.redirect("/");

        } catch (Exception e) {
            ctx.alertError("error.auth.verify.failed");
            if (ctx.isAuthenticated())
                ctx.redirect("/verify-pending");
            else
                ctx.redirect("/login");
        }
    }
}

