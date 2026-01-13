package com.nlu.store.core.security;

import com.nlu.store.core.web.Authentication;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationStrategy {
    Authentication authenticate(HttpServletRequest req);
}
