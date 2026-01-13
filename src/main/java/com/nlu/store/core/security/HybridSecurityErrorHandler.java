package com.nlu.store.core.security;

import com.nlu.store.core.security.SecurityErrorHandler;
import com.nlu.store.core.web.WebInfrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@ApplicationScoped
@Named("hybridErrorHandler")
public class HybridSecurityErrorHandler implements SecurityErrorHandler {

    @Inject private WebInfrastructure infrastructure;

    @Override
    public void onUnauthenticated(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (isApi(req)) sendJson(res, 401, "Unauthorized");
        else {
            req.getSession().setAttribute("REDIRECT_TO", req.getRequestURI());
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    public void onAccessDenied(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (isApi(req)) sendJson(res, 403, "Forbidden");
        else {
            try {
                req.getRequestDispatcher("/WEB-INF/views/errors/403.jsp").forward(req, res);
            } catch (ServletException e) { throw new IOException(e); }
        }
    }

    private boolean isApi(HttpServletRequest req) {
        String path = req.getServletPath();
        String accept = req.getHeader("Accept");
        return path.startsWith("/api/") || (accept != null && accept.contains("application/json"));
    }

    private void sendJson(HttpServletResponse res, int status, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        infrastructure.objectMapper().writeValue(res.getWriter(), Map.of("error", true, "message", msg));
    }
}
