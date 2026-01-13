package com.nlu.store.core.security;

import com.nlu.store.core.web.flash.Flash;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@ApplicationScoped
@Named("mvcErrorHandler")
public class MvcSecurityErrorHandler implements SecurityErrorHandler {

    @Override
    public void onUnauthenticated(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Flash.put(req,"REDIRECT_TO" ,req.getRequestURI());
        res.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    public void onAccessDenied(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Flash.put(req,"REDIRECT_TO" ,req.getRequestURI());
        res.sendRedirect(req.getContextPath() + "/login");    }
}
