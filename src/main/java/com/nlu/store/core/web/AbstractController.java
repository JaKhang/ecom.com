package com.nlu.store.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractController extends HttpServlet {

    @Inject
   private WebInfrastructure webInfrastructure;

    ;

    private enum HttpMethod { GET, POST, PUT, DELETE }

    /**
     * Central processing method acting as a template.
     * Wraps execution in a try-catch block to ensure safety.
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod method) {
        HttpContext ctx = new ServletHttpContext(req, resp, webInfrastructure);

        try {
            switch (method) {
                case GET -> doGet(ctx);
                case POST -> doPost(ctx);
                case PUT -> doPut(ctx);
                case DELETE -> doDelete(ctx);
            }
        } catch (IllegalArgumentException e) {
            // Handle Bad Requests (400)
            ctx.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            // Log here (using SLF4J or similar)
            e.printStackTrace();
            // Handle Server Errors (500)
            ctx.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    // --- Servlet API Overrides (Bridge to HttpExchange) ---

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp, HttpMethod.GET);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp, HttpMethod.POST);
        }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp, HttpMethod.PUT);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp, HttpMethod.DELETE);
    }

    // --- Extension Points for Subclasses ---

    protected void doGet(HttpContext ctx) {
        ctx.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not supported");
    }

    protected void doPost(HttpContext ctx) {
        ctx.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported");
    }

    protected void doPut(HttpContext ctx) {
        ctx.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "PUT method not supported");
    }

    protected void doDelete(HttpContext ctx) {
        ctx.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "DELETE method not supported");
    }
}
