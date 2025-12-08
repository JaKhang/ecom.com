package com.nlu.store.core.web;

import com.nlu.store.core.data.MultipartFile;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.bind.BindingResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HttpContext {

    String AUTHENTICATION_KEY = "USER_PRINCIPAL";
    // ==================================================================
    // 1. CORE SERVLET ACCESS
    // ==================================================================
    HttpServletRequest getRequest();
    HttpServletResponse getResponse();

    // ==================================================================
    // 2. REQUEST PARAMETERS (QUERY & FORM DATA)
    // ==================================================================

    // --- Single Value ---
    Optional<String> getParam(String name);
    String getParam(String name, String defaultValue, boolean required);
    Integer getParam(String name, Integer defaultValue);
    Integer getParam(String name, Integer defaultValue, boolean required);
    Double getParam(String name, Double defaultValue, boolean required);
    <T> T getParam(String name, Class<T> type, T defaultValue, boolean required);

    // --- Multi Value (List - VD: ?ids=1&ids=2) ---
    List<String> getParams(String name);
    <T> List<T> getParams(String name, Class<T> type);

    // --- Utilities ---
    Map<String, String> getAllParams();
    Pageable getPageable();

    // --- Object Binding (Query String -> Object) ---
    /** Map các tham số trên URL vào một Object (Dùng cho Search/Filter) */
    <T> T bindQueryParams(Class<T> type);


    // ==================================================================
    // 3. PATH VARIABLES (URL PARAMETERS)
    // ==================================================================
    String getPathVariable(String template, String name);
    Integer getIntPathVariable(String template, String name);
    <T> T getPathVariable(String template, String name, Class<T> type);


    // ==================================================================
    // 4. REQUEST BODY & FILE UPLOAD
    // ==================================================================

    // --- Body Binding ---
    <T> T getBody(Class<T> tClass);
    <T> BindingResult<T> getBody(Class<T> tClass, Validator<T> validator);

    // --- File Upload ---
    boolean isMultipart();
    MultipartFile getMultipartFile(String name);
    List<MultipartFile> getMultipartFiles(String name);


    // ==================================================================
    // 5. HEADERS & COOKIES
    // ==================================================================
    boolean hasHeader(String name);
    String getHeader(String name);
    HttpContext addHeader(String name, String value); // Fluent

    Optional<Cookie> getCookie(String name);
    HttpContext addCookie(Cookie cookie); // Fluent
    HttpContext removeCookie(String name); // Fluent


    // ==================================================================
    // 6. ATTRIBUTES & SCOPE
    // ==================================================================
    Object getAttribute(String name);
    <T> T getAttribute(String name, Class<T> type);
    HttpContext setAttribute(String name, Object value); // Fluent
    void removeAttribute(String name);

    // --- Flash Attributes ---
    HttpContext setFlashAttribute(String name, Object value); // Fluent
    void removeFlashAttribute(String name);


    // ==================================================================
    // 7. SECURITY
    // ==================================================================
    boolean isAuthenticated();
    Authentication authentication();
    void setAuthentication(Authentication authentication);


    // ==================================================================
    // 8. RESPONSE & NAVIGATION
    // ==================================================================

    // --- Configuration ---
    HttpContext setStatus(int statusCode); // Fluent
    HttpContext setResponseHeader(String name, String value); // Fluent

    // --- Sending Response ---
    void sendResponse(String body);
    void sendRestResponse(Object data);
    void sendError(int statusCode, String message);

    // --- Navigation ---
    void forward(String path);
    void includes(String path);
    void redirect(String location);
    void redirect(String location, Map<String, Object> attributes);

    // --- Views ---
    void view(String viewName);
    void view(String viewName, Map<String, Object> model);
}
