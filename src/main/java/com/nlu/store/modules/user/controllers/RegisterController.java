package com.nlu.store.modules.user.controllers;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.modules.user.UserAlreadyExistsException;
import com.nlu.store.modules.user.dto.LoginRequest;
import com.nlu.store.modules.user.dto.RegisterRequest;
import com.nlu.store.modules.user.services.AuthService;
import com.nlu.store.modules.user.validator.LoginRequestValidator;
import com.nlu.store.modules.user.validator.RegisterRequestValidator;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/register",
        "/dang-ky"
})
public class RegisterController extends AbstractController {

    @Inject
    private AuthService authService;
    @Override
    protected void doGet(HttpContext ctx) {
        if (ctx.isAuthenticated()) {
            ctx.redirect("/");
        } else {
            ctx.view("client/register");
        }
    }


    @Override
    protected void doPost(HttpContext ctx) {
        BindingResult<RegisterRequest> result = ctx.getBody(RegisterRequest.class, new RegisterRequestValidator());
        if (result.hasError()) {
            ctx.view("client/register");
            return;
        }

        try {
            RegisterRequest request = result.data();
            authService.register(result.data());
            ctx.setFlashAttribute("alertMessage", "auth.register.success");
            ctx.redirect("/login");
        } catch (UserAlreadyExistsException e){
            Map<String, String> errors = new HashMap<>();
            errors.put("email", e.getMessage());
            ctx.setAttribute("form", result.data());
            ctx.setAttribute("errors", errors);
            ctx.view("client/register");
        }
    }
}
