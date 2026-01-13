package com.nlu.store.core.security;

import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ApplicationScoped()
@Named("sessionStrategy")
public class SessionAuthStrategy implements AuthenticationStrategy{
    @Override
    public Authentication authenticate(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object auth = session.getAttribute(HttpContext.AUTHENTICATION_KEY);
        if (auth instanceof Authentication) {
            return (Authentication) auth;
        }
        return null;
    }
}
