package com.nlu.store.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface SecurityErrorHandler {
    void onUnauthenticated(HttpServletRequest req, HttpServletResponse res) throws IOException;

    void onAccessDenied(HttpServletRequest req, HttpServletResponse res) throws IOException;
}
