package com.nlu.store.core.web;

import com.nlu.store.core.data.MultipartFile;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.bind.BindingResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central interface for HTTP request/response handling.
 * <p>
 * This interface acts as a high-level wrapper around the standard Servlet API.
 * It provides helper methods for parameter extraction, type conversion,
 * session management, and common web patterns like Flash Attributes and Alert Systems.
 * </p>
 * <p>
 * It is designed to simplify controller logic by abstracting away the raw
 * {@link HttpServletRequest} and {@link HttpServletResponse} manipulations.
 * </p>
 *
 * @author Ja Khang
 */
public interface HttpContext {

    /**
     * Key used to store the {@link Authentication} object in the HTTP session.
     */
    String AUTHENTICATION_KEY = "com.nlu.store.core.web.AUTHENTICATION";

    /**
     * Key used to store temporary flash alerts in the session (survives one redirect).
     */
    String ALERT_KEY = "com.nlu.store.core.web.ALERT";

    /**
     * Attribute name used to store validation error maps in the request scope.
     */
    String ATTR_ERRORS = "errors";

    /**
     * Attribute name used to preserve previous form data in the request scope
     * when validation fails.
     */
    String ATTR_FORM = "form";

    // ==================================================================
    // 1. CORE SERVLET ACCESS
    // ==================================================================

    /**
     * Gets the underlying raw Servlet request.
     *
     * @return the {@link HttpServletRequest} instance.
     */
    HttpServletRequest getRequest();

    /**
     * Gets the underlying raw Servlet response.
     *
     * @return the {@link HttpServletResponse} instance.
     */
    HttpServletResponse getResponse();


    // ==================================================================
    // 2. REQUEST PARAMETERS (QUERY & FORM DATA)
    // ==================================================================

    /**
     * Retrieves a request parameter value wrapped in an {@link Optional}.
     *
     * @param name the name of the parameter.
     * @return an {@link Optional} containing the value, or empty if not found/null.
     */
    Optional<String> getParam(String name);

    /**
     * Retrieves a request parameter with a fallback default value and validation control.
     *
     * @param name         the name of the parameter.
     * @param defaultValue the value to return if the parameter is missing or empty.
     * @param required     if {@code true}, the implementation may throw an exception or
     *                     register a validation error if the parameter is missing.
     * @return the parameter value or the default value.
     */
    String getParam(String name, String defaultValue, boolean required);

    /**
     * Retrieves a parameter and attempts to convert it to an {@link Integer}.
     *
     * @param name         the name of the parameter.
     * @param defaultValue the default value to return if conversion fails or param is missing.
     * @return the converted {@link Integer} or the default value.
     */
    Integer getParam(String name, Integer defaultValue);

    /**
     * Retrieves a parameter as an {@link Integer} with strict requirement checking.
     *
     * @param name         the name of the parameter.
     * @param defaultValue the default value.
     * @param required     whether the parameter is mandatory.
     * @return the converted {@link Integer}.
     */
    Integer getParam(String name, Integer defaultValue, boolean required);

    /**
     * Retrieves a parameter as a {@link Double} with strict requirement checking.
     *
     * @param name         the name of the parameter.
     * @param defaultValue the default value.
     * @param required     whether the parameter is mandatory.
     * @return the converted {@link Double}.
     */
    Double getParam(String name, Double defaultValue, boolean required);

    /**
     * Generic method to retrieve and convert a parameter to a specific target type.
     *
     * @param <T>          the target type (e.g., Long, Boolean, Enum).
     * @param name         the name of the parameter.
     * @param type         the class of the target type.
     * @param defaultValue the default value.
     * @param required     whether the parameter is mandatory.
     * @return the converted value of type T.
     */
    <T> T getParam(String name, Class<T> type, T defaultValue, boolean required);

    /**
     * Retrieves all values of a specific parameter name as a List of Strings.
     * Useful for checkbox groups or multi-select inputs.
     *
     * @param name the name of the parameter.
     * @return a {@link List} of values, or an empty list if none found.
     */
    List<String> getParams(String name);

    /**
     * Retrieves all values of a parameter and converts them to the specified type.
     *
     * @param <T>  the target element type.
     * @param name the name of the parameter.
     * @param type the class of the target type.
     * @return a {@link List} of converted values.
     */
    <T> List<T> getParams(String name, Class<T> type);

    /**
     * Retrieves all request parameters as a Map.
     *
     * @return a map where keys are parameter names and values are the first string value.
     */
    Map<String, String> getAllParams();

    /**
     * Extracts pagination information (page number, page size, sort) from the request parameters.
     *
     * @return a {@link Pageable} object populated from the request.
     */
    Pageable getPageable();

    /**
     * Maps URL query parameters to a Java Object using reflection.
     * <p>
     * For example, {@code ?name=John&age=20} will populate fields {@code name} and {@code age}
     * in the target class.
     * </p>
     *
     * @param <T>  the target object type.
     * @param type the class of the object to bind to.
     * @return the populated object instance.
     */
    <T> T bindQueryParams(Class<T> type);


    // ==================================================================
    // 3. PATH VARIABLES (URL PARAMETERS)
    // ==================================================================

    /**
     * Extracts a path variable from the URI template.
     * <p>
     * Example: If template is {@code /users/{id}} and URI is {@code /users/5},
     * calling {@code getPathVariable("/users/{id}", "id")} returns "5".
     * </p>
     *
     * @param template the URI template pattern containing placeholders.
     * @param name     the name of the variable to extract.
     * @return the string value of the path variable.
     */
    String getPathVariable(String template, String name);

    /**
     * Extracts a path variable and converts it to an {@link Integer}.
     *
     * @param template the URI template pattern.
     * @param name     the name of the variable.
     * @return the converted Integer value.
     */
    Integer getIntPathVariable(String template, String name);

    /**
     * Extracts a path variable and converts it to the specified target type.
     *
     * @param <T>      the target type.
     * @param template the URI template pattern.
     * @param name     the name of the variable.
     * @param type     the target class for conversion.
     * @return the converted value.
     */
    <T> T getPathVariable(String template, String name, Class<T> type);


    // ==================================================================
    // 4. REQUEST BODY & FILE UPLOAD
    // ==================================================================

    /**
     * Deserializes the JSON request body into a Java Object.
     *
     * @param <T>    the target type.
     * @param tClass the target class.
     * @return the deserialized object.
     */
    <T> T getBody(Class<T> tClass);

    /**
     * Deserializes the JSON body and applies validation logic.
     *
     * @param <T>       the target type.
     * @param tClass    the target class.
     * @param validator the validator instance to validate the deserialized object.
     * @return a {@link BindingResult} containing the object and any validation errors.
     */
    <T> BindingResult<T> getBody(Class<T> tClass, Validator<T> validator);

    /**
     * Checks if the current request is a Multipart request (i.e., contains file uploads).
     *
     * @return {@code true} if the content-type is multipart/form-data.
     */
    boolean isMultipart();

    /**
     * Retrieves a single uploaded file by its form field name.
     *
     * @param name the form field name.
     * @return the {@link MultipartFile} object, or null if empty.
     */
    MultipartFile getMultipartFile(String name);

    /**
     * Retrieves multiple uploaded files sharing the same form field name.
     *
     * @param name the form field name.
     * @return a list of {@link MultipartFile} objects.
     */
    List<MultipartFile> getMultipartFiles(String name);


    // ==================================================================
    // 5. HEADERS & COOKIES
    // ==================================================================

    /**
     * Checks if a specific header exists in the request.
     *
     * @param name the header name.
     * @return {@code true} if the header is present.
     */
    boolean hasHeader(String name);

    /**
     * Retrieves a request header value.
     *
     * @param name the header name.
     * @return the header value, or null if not found.
     */
    String getHeader(String name);

    /**
     * Adds a header to the response.
     *
     * @param name  the header name.
     * @param value the header value.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext addHeader(String name, String value);

    /**
     * Retrieves a cookie by name from the request.
     *
     * @param name the cookie name.
     * @return an {@link Optional} containing the cookie, or empty if not found.
     */
    Optional<Cookie> getCookie(String name);

    /**
     * Adds a cookie to the response.
     *
     * @param cookie the {@link Cookie} object to add.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext addCookie(Cookie cookie);

    /**
     * Removes a cookie by setting its max-age to 0 and adding it to the response.
     *
     * @param name the name of the cookie to remove.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext removeCookie(String name);


    // ==================================================================
    // 6. ATTRIBUTES & SCOPE
    // ==================================================================

    /**
     * Retrieves an attribute from the request scope.
     *
     * @param name the attribute name.
     * @return the attribute value.
     */
    Object getAttribute(String name);

    /**
     * Retrieves a request attribute and casts it to the specified type.
     *
     * @param <T>  the target type.
     * @param name the attribute name.
     * @param type the target class.
     * @return the cast attribute value.
     */
    <T> T getAttribute(String name, Class<T> type);

    /**
     * Sets an attribute in the request scope.
     *
     * @param name  the attribute name.
     * @param value the attribute value.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext setAttribute(String name, Object value);

    /**
     * Removes an attribute from the request scope.
     *
     * @param name the attribute name.
     */
    void removeAttribute(String name);

    /**
     * Returns the current session associated with this request, or if the request does
     * not have a session, creates one.
     *
     * @param create {@code true} to create a new session if necessary;
     *               {@code false} to return null if no session exists.
     * @return the {@link HttpSession}.
     */
    default HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }

    /**
     * Returns the current session associated with this request, or if the request does
     * not have a session, creates one.
     *
     * @return the {@link HttpSession}.
     */
    default HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * Sets a "Flash Attribute" in the session.
     * <p>
     * Flash attributes are stored temporarily in the session and are automatically
     * removed after being retrieved once (usually after a redirect).
     * </p>
     *
     * @param name  the attribute name.
     * @param value the attribute value.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext setFlashAttribute(String name, Object value);

    /**
     * Manually removes a flash attribute from the session.
     *
     * @param name the attribute name.
     */
    void removeFlashAttribute(String name);


    // ==================================================================
    // 7. SECURITY
    // ==================================================================

    /**
     * Checks if the current user is authenticated (logged in).
     *
     * @return {@code true} if the session contains a valid authentication object.
     */
    boolean isAuthenticated();

    /**
     * Retrieves the current authenticated user principal.
     *
     * @return the {@link Authentication} object, or null if not logged in.
     */
    Authentication authentication();

    /**
     * Sets the authenticated user principal into the session (Login).
     *
     * @param authentication the authentication object to store.
     */
    void setAuthentication(Authentication authentication);


    // ==================================================================
    // 8. RESPONSE & NAVIGATION
    // ==================================================================

    /**
     * Sets the HTTP response status code.
     *
     * @param statusCode the status code (e.g., 200, 404).
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext setStatus(int statusCode);

    /**
     * Sets a header on the response. Alias for {@link #addHeader(String, String)}.
     *
     * @param name  the header name.
     * @param value the header value.
     * @return this {@link HttpContext} instance for fluent chaining.
     */
    HttpContext setResponseHeader(String name, String value);

    /**
     * Writes a raw string to the response body and closes the stream.
     *
     * @param body the string content to send.
     */
    void sendResponse(String body);

    /**
     * Serializes an object to JSON and sends it as the response body.
     * Automatically sets Content-Type to application/json.
     *
     * @param data the object to serialize.
     */
    void sendRestResponse(Object data);

    /**
     * Sends an error response with a specific status code and message.
     *
     * @param statusCode the HTTP error code.
     * @param message    the error message.
     */
    void sendError(int statusCode, String message);

    /**
     * Forwards the request to another internal resource (Server-side forward).
     *
     * @param path the internal path to forward to.
     */
    void forward(String path);

    /**
     * Includes the content of another resource in the current response.
     *
     * @param path the internal path to include.
     */
    void include(String path);

    /**
     * Redirects the client to a new URL (Client-side redirect).
     *
     * @param location the URL to redirect to.
     */
    void redirect(String location);

    /**
     * Redirects the client to a new URL and adds temporary flash attributes.
     *
     * @param location   the URL to redirect to.
     * @param attributes a map of attributes to add to the flash scope.
     */
    void redirect(String location, Map<String, Object> attributes);

    /**
     * Renders a server-side view template (e.g., JSP, Thymeleaf).
     *
     * @param viewName the logical name of the view.
     */
    void view(String viewName);

    /**
     * Renders a server-side view template with a provided model map.
     *
     * @param viewName the logical name of the view.
     * @param model    the data to pass to the view.
     */
    void view(String viewName, Map<String, Object> model);


    // ==================================================================
    // 9. ALERT SYSTEM
    // ==================================================================

    /**
     * Sends a flash message to be displayed in the UI after a redirect.
     *
     * @param type       the {@link AlertType} (SUCCESS, ERROR, WARNING, INFO).
     * @param messageKey the translation key or raw message text.
     */
    void alert(AlertType type, String messageKey);

    /**
     * Helper method to send a SUCCESS alert.
     *
     * @param messageKey the translation key or message.
     */
    default void alertSuccess(String messageKey) {
        alert(AlertType.SUCCESS, messageKey);
    }

    /**
     * Helper method to send an ERROR alert.
     *
     * @param messageKey the translation key or message.
     */
    default void alertError(String messageKey) {
        alert(AlertType.ERROR, messageKey);
    }

    /**
     * Gets the current request URI path relative to the context root.
     *
     * @return the request path string.
     */
    String getRequestPath();

    /**
     * Counts the number of segments in the request path.
     * <p>
     * Example: For path {@code /api/users/list}, returns 3.
     * </p>
     *
     * @return the number of path segments.
     */
    default int countRequestPathSegments() {
        String[] pathParts = getRequestPath().split("/");
        return (int) java.util.Arrays.stream(pathParts)
                .filter(part -> !part.isEmpty())
                .count();
    }

    /**
     * Translates a message key using the configured MessageSource/ResourceBundle.
     *
     * @param key the message key.
     * @return the translated string, or the key itself if not found.
     */
    String getMessage(String key);

    /**
     * Translates a message key with a fallback default value.
     *
     * @param key          the message key.
     * @param defaultValue the value to return if the key is not found.
     * @return the translated string or default value.
     */
    String getMessage(String key, String defaultValue);
}
