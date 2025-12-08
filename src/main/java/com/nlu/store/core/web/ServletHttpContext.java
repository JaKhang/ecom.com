package com.nlu.store.core.web;

import com.nlu.store.core.data.*;
import com.nlu.store.core.utils.ReflectionUtils;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.core.web.flash.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ServletHttpContext implements HttpContext {

    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final WebInfrastructure infrastructure;
    private Authentication authentication; // Cache

    public ServletHttpContext(HttpServletRequest req, HttpServletResponse resp, WebInfrastructure infrastructure) {
        this.req = req;
        this.resp = resp;
        this.infrastructure = infrastructure;
    }

    @Override
    public HttpServletRequest getRequest() {
        return req;
    }

    @Override
    public HttpServletResponse getResponse() {
        return resp;
    }

    // ==================================================================
    // PARAMETERS IMPLEMENTATION
    // ==================================================================

    @Override
    public Optional<String> getParam(String name) {
        return Optional.ofNullable(req.getParameter(name));
    }

    @Override
    public String getParam(String name, String defaultValue, boolean required) {
        String val = req.getParameter(name);
        if (val == null && required) {
            throw new BadRequestException("Missing required parameter: " + name);
        }
        return val != null ? val : defaultValue;
    }

    @Override
    public Integer getParam(String name, Integer defaultValue) {
        return getParam(name, Integer.class, defaultValue, false);
    }

    @Override
    public Integer getParam(String name, Integer defaultValue, boolean required) {
        return getParam(name, Integer.class, defaultValue, required);
    }

    @Override
    public Double getParam(String name, Double defaultValue, boolean required) {
        return getParam(name, Double.class, defaultValue, required);
    }

    @Override
    public <T> T getParam(String name, Class<T> type, T defaultValue, boolean required) {
        String val = req.getParameter(name);
        if (val == null) {
            if (required) throw new BadRequestException("Missing required parameter: " + name);
            return defaultValue;
        }
        try {
            return infrastructure.conversionService().convert(val, type);
        } catch (Exception e) {
            throw new BadRequestException("Invalid format for parameter: " + name);
        }
    }

    @Override
    public List<String> getParams(String name) {
        String[] values = req.getParameterValues(name);
        return values == null ? Collections.emptyList() : Arrays.asList(values);
    }

    @Override
    public <T> List<T> getParams(String name, Class<T> type) {
        String[] values = req.getParameterValues(name);
        if (values == null || values.length == 0) return Collections.emptyList();

        return Arrays.stream(values)
                .map(val -> infrastructure.conversionService().convert(val, type))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getAllParams() {
        Map<String, String> params = new HashMap<>();
        req.getParameterMap().forEach((k, v) -> {
            if (v.length > 0) params.put(k, v[0]);
        });
        return params;
    }

    @Override
    public <T> T bindQueryParams(Class<T> type) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();

            for (Field field : fields) {
                String paramName = field.getName();

                // Xử lý List
                if (List.class.isAssignableFrom(field.getType())) {
                    Class<?> genericType = String.class;
                    Type pt = field.getGenericType();
                    if (pt instanceof ParameterizedType) {
                        Type[] args = ((ParameterizedType) pt).getActualTypeArguments();
                        if (args.length > 0) genericType = (Class<?>) args[0];
                    }
                    List<?> listValues = getParams(paramName, genericType);
                    ReflectionUtils.set(instance, paramName, listValues);
                } else {
                    // Xử lý Single Value
                    String val = req.getParameter(paramName);
                    if (val != null) {
                        Object converted = infrastructure.conversionService().convert(val, field.getType());
                        ReflectionUtils.set(instance, paramName, converted);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind query params", e);
        }
    }

    @Override
    public Pageable getPageable() {
        // 1. Parse Page (URL thường là 1-based, PageRequest là 0-based)
        int page = getParam("page", Integer.class, 1, false);
        int pageIndex = (page > 0) ? page - 1 : 0;

        // 2. Parse Limit
        int limit = getParam("limit", Integer.class, 10, false);

        // 3. Parse Sort (Format: ?sort=name,asc&sort=price,desc)
        List<String> sortParams = getParams("sort");
        Sort sort = Sort.UNSORTED;

        if (!sortParams.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String param : sortParams) {
                // Tách "property,direction"
                String[] parts = param.split(",");
                String property = parts[0].trim();

                Sort.Direction direction = Sort.DEFAULT_DIRECTION;
                if (parts.length > 1) {
                    try {
                        direction = Sort.Direction.fromString(parts[1]);
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid direction, fallback to ASC
                    }
                }
                orders.add(new Sort.Order(property, direction));
            }
            sort = Sort.by(orders);
        }

        return new PageRequest(pageIndex, limit, sort);
    }

    // ==================================================================
    // PATH VARIABLES
    // ==================================================================

    @Override
    public String getPathVariable(String template, String name) {
        return getPathVariable(template, name, String.class);
    }

    @Override
    public Integer getIntPathVariable(String template, String name) {
        return getPathVariable(template, name, Integer.class);
    }

    @Override
    public <T> T getPathVariable(String template, String name, Class<T> type) {
        @SuppressWarnings("unchecked")
        Map<String, String> vars = (Map<String, String>) req.getAttribute("URI_TEMPLATE_VARIABLES");
        if (vars == null || !vars.containsKey(name)) return null;
        return infrastructure.conversionService().convert(vars.get(name), type);
    }

    // ==================================================================
    // BODY & FILE UPLOAD
    // ==================================================================

    @Override
    public <T> T getBody(Class<T> tClass) {
        return infrastructure.dataBinder().bind(req, tClass).data();
    }

    @Override
    public <T> BindingResult<T> getBody(Class<T> tClass, Validator<T> validator) {
       return infrastructure.dataBinder().bind(req, tClass, validator);
    }


    @Override
    public boolean isMultipart() {
        String type = req.getContentType();
        return type != null && type.toLowerCase().startsWith("multipart/");
    }

    @Override
    public MultipartFile getMultipartFile(String name) {
        if (!isMultipart()) return null;
        try {
            Part part = req.getPart(name);
            return (part != null && part.getSize() > 0) ? new StandardMultipartFile(part) : null;
        } catch (ServletException | IOException e) {
            return null;
        }
    }

    @Override
    public List<MultipartFile> getMultipartFiles(String name) {
        if (!isMultipart()) return Collections.emptyList();
        try {
            return req.getParts().stream()
                    .filter(p -> name.equals(p.getName()) && p.getSize() > 0)
                    .map(StandardMultipartFile::new)
                    .collect(Collectors.toList());
        } catch (ServletException | IOException e) {
            throw new RuntimeException("Failed to get multipart files", e);
        }
    }

    // ==================================================================
    // HEADERS & COOKIES
    // ==================================================================

    @Override
    public boolean hasHeader(String name) {
        return req.getHeader(name) != null;
    }

    @Override
    public String getHeader(String name) {
        return req.getHeader(name);
    }

    @Override
    public HttpContext addHeader(String name, String value) {
        resp.addHeader(name, value);
        return this;
    }

    @Override
    public Optional<Cookie> getCookie(String name) {
        if (req.getCookies() == null) return Optional.empty();
        return Arrays.stream(req.getCookies()).filter(c -> c.getName().equals(name)).findFirst();
    }

    @Override
    public HttpContext addCookie(Cookie cookie) {
        resp.addCookie(cookie);
        return this;
    }

    @Override
    public HttpContext removeCookie(String name) {
        Cookie c = new Cookie(name, "");
        c.setMaxAge(0);
        c.setPath("/");
        resp.addCookie(c);
        return this;
    }

    // ==================================================================
    // ATTRIBUTES & FLASH
    // ==================================================================

    @Override
    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> type) {
        Object attr = req.getAttribute(name);
        return type.isInstance(attr) ? type.cast(attr) : null;
    }

    @Override
    public HttpContext setAttribute(String name, Object value) {
        req.setAttribute(name, value);
        return this;
    }

    @Override
    public void removeAttribute(String name) {
        req.removeAttribute(name);
    }

    @Override
    public HttpContext setFlashAttribute(String name, Object value) {
        Flash.put(req, name, value);
        return this;
    }

    @Override
    public void removeFlashAttribute(String name) {
        Flash.remove(req, name);
    }

    // ==================================================================
    // SECURITY
    // ==================================================================

    @Override
    public boolean isAuthenticated() {
        return authentication() != null;
    }

    @Override
    public Authentication authentication() {
        if (this.authentication != null) return this.authentication;
        this.authentication = (Authentication) req.getSession().getAttribute(AUTHENTICATION_KEY);
        return this.authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        req.getSession().setAttribute(AUTHENTICATION_KEY, authentication);
    }

    // ==================================================================
    // RESPONSE & NAVIGATION
    // ==================================================================

    @Override
    public HttpContext setStatus(int statusCode) {
        resp.setStatus(statusCode);
        return this;
    }

    @Override
    public HttpContext setResponseHeader(String name, String value) {
        resp.setHeader(name, value);
        return this;
    }

    @Override
    public void sendResponse(String body) {
        try {
            resp.getWriter().write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRestResponse(Object data) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            infrastructure.objectMapper().writeValue(resp.getWriter(), data);
        } catch (IOException e) {
            throw new RuntimeException("JSON Write Error", e);
        }
    }

    @Override
    public void sendError(int statusCode, String message) {
        try {
            resp.sendError(statusCode, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forward(String path) {
        try {
            req.getRequestDispatcher(path).forward(req, resp);
        } catch (ServletException | IOException e) {
            throw new RuntimeException("Forward Error", e);
        }
    }

    @Override
    public void includes(String path) {
        try {
            req.getRequestDispatcher(path).include(req, resp);
        } catch (ServletException | IOException e) {
            throw new RuntimeException("Include Error", e);
        }
    }

    @Override
    public void redirect(String location) {
        try {
            String targetUrl = location;

            // 1. Nếu không phải URL tuyệt đối (http/https) và bắt đầu bằng dấu "/"
            // Thì tự động thêm Context Path vào trước (nếu chưa có)
            if (!location.startsWith("http") && location.startsWith("/")) {
                String contextPath = req.getContextPath(); // Cần có HttpServletRequest req

                if (!location.startsWith(contextPath)) {
                    targetUrl = contextPath + location;
                }
            }

            // 2. Thực hiện redirect
            resp.sendRedirect(targetUrl);

        } catch (IOException e) {
            // 3. Sử dụng UncheckedIOException để giữ nguyên ngữ nghĩa lỗi IO
            throw new UncheckedIOException("Cannot redirect to " + location, e);
        }
    }

    @Override
    public void redirect(String location, Map<String, Object> attributes) {
        if (attributes != null) attributes.forEach(this::setFlashAttribute);
        redirect(location);
    }

    @Override
    public void view(String viewName) {
        // Logic resolve view, ví dụ thêm prefix/suffix
        String path = infrastructure.viewResolver().resolveViewName(viewName);
        forward(path);
    }

    @Override
    public void view(String viewName, Map<String, Object> model) {
        if (model != null) model.forEach(req::setAttribute);
        view(viewName);
    }
}
