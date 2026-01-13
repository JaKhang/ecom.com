package com.nlu.store.core.security;

import com.nlu.store.core.security.AuthenticationStrategy;
import com.nlu.store.core.web.Authentication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
@Named("tokenStrategy")
public class TokenAuthenticationStrategy implements AuthenticationStrategy {
    @Override
    public Authentication authenticate(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);

        // TODO: Implement JWT verification here
        return null;
    }
}
