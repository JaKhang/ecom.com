package com.nlu.store.core.web;


/**
 * Resolves logical view names to actual physical resource paths.
 * <p>
 * Example: "home" -> "/WEB-INF/views/home.jsp"
 * </p>
 */
public interface ViewResolver {

    /**
     * Resolve the given view name into a physical resource path.
     *
     * @param viewName the logical name of the view (e.g., "auth/login")
     * @return the full path to the resource (e.g., "/WEB-INF/views/auth/login.jsp")
     */
    String resolveViewName(String viewName);
}
