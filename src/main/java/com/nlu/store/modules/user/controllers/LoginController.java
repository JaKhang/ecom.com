package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.core.exceptions.AuthenticationException;
import com.nlu.store.modules.user.dto.LoginRequest;
import com.nlu.store.modules.user.services.AuthService;
import com.nlu.store.modules.user.validator.LoginRequestValidator;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/login",
        "/dang-nhap"
})
public class LoginController extends AbstractController {
    @Inject
    private AuthService authService;

    @Override
    protected void doGet(HttpContext ctx) {

        if (ctx.isAuthenticated()) {
            ctx.redirect("/");
        } else {

            String redirectUrl = ctx.getParam("redirectUrl").orElse(ctx.getAttribute("REDIRECT_TO", String.class));
            ctx.setAttribute("redirectUrl", redirectUrl);
            ctx.view("client/login");
        }
    }

    @Override
    protected void doPost(HttpContext ctx) {
        BindingResult<LoginRequest> result = ctx.getBody(LoginRequest.class, new LoginRequestValidator());
        if (result.hasError()) {
            ctx.view("client/login");
            return;
        }
        Map<String, String> errors = new HashMap<>();
        try {
            Authentication authentication = authService.login(result.data());
            ctx.setAuthentication(authentication);

            if (authentication.isVerified()) {
                String redirectUrl = ctx.getParam("redirectUrl").orElse(null);
                if (redirectUrl != null) {
                    ctx.redirect(redirectUrl);
                } else {
                    ctx.alertSuccess("hello");
                    ctx.redirect("");

                }
            } else {
                ctx.redirect("/verify-pending");

            }

            return;

        } catch (AuthenticationException e) {
            errors.put("global", e.getMessage());
        }
        ctx.setAttribute("errors", errors);
        ctx.view("client/login");
    }
}
