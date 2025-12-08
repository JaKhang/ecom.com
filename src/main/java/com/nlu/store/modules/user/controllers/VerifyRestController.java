package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.modules.user.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

public class VerifyRestController extends AbstractController {

    @Inject
    private AuthService authService;

    @Override
    protected void doPost(HttpContext ctx) {
        try {
            String email = ctx.getParam("email", String.class, "", true);

            // 1. Gọi Service
            authService.requestVerify(email);


        } catch (Exception e) {

        }
    }
}

