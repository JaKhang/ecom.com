package com.nlu.store.core.web;

import com.nlu.store.core.data.*;
import com.nlu.store.core.utils.ReflectionUtils;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.core.web.flash.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.jsp.jstl.core.Config;

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
        // 1. Parse Page
        int page = getParam("page", Integer.class, 1, false);
        int pageIndex = (page > 0) ? page : 1;

        // 2. Parse Limit
        int limit = getParam("limit", Integer.class, 12, false);

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
        return infrastructure.pathExtractor().extractPathValue(template, name, getRequestPath());
    }

    @Override
    public Integer getIntPathVariable(String template, String name) {
        return getPathVariable(template, name, Integer.class);
    }

    @Override
    public <T> T getPathVariable(String template, String name, Class<T> type) {
        String value = getPathVariable(template, name);
        if (value == null) return null;
        return infrastructure.conversionService().convert(value, type);
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
        BindingResult<T> result = infrastructure.dataBinder().bind(req, tClass, validator);
        req.setAttribute(ATTR_ERRORS, result.details());
        req.setAttribute(ATTR_FORM, result.data());
        return result;
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
        // 1. Level 1: Local Cache (Nhanh nhất)
        if (this.authentication != null) {
            return this.authentication;
        }

        // 2. Level 2: Request Attribute (Được set bởi SecurityFilter)
        Object reqAuth = req.getAttribute(AUTHENTICATION_KEY);
        if (reqAuth instanceof Authentication) {
            this.authentication = (Authentication) reqAuth;
            return this.authentication;
        }

        // 3. Level 3: Session (Fallback cho Legacy hoặc Stateful App)
        // Lưu ý: getSession(false) để không tạo session rác nếu client là bot/api
        var session = req.getSession(false);
        if (session != null) {
            Object sessionAuth = session.getAttribute(AUTHENTICATION_KEY);
            if (sessionAuth instanceof Authentication) {
                this.authentication = (Authentication) sessionAuth;
                // Đồng bộ ngược lại vào Request để lần sau lấy nhanh hơn
                req.setAttribute(AUTHENTICATION_KEY, this.authentication);
                return this.authentication;
            }
        }

        return null;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        // 1. Cập nhật Cache cục bộ của instance này
        this.authentication = authentication;

        if (authentication != null) {
            // --- TRƯỜNG HỢP ĐĂNG NHẬP (LOGIN) ---

            // a. Lưu vào Request Attribute
            // Giúp các thành phần khác trong cùng Request (như View, Filter khác) truy cập được ngay
            // mà không cần đọc lại từ Session.
            req.setAttribute(AUTHENTICATION_KEY, authentication);

            // b. Lưu vào Session (Persistence)
            // Hành động gọi setAuthentication() từ Controller ngầm định là "Ghi nhớ đăng nhập".
            // - Nếu là Stateful (MVC): Ta cần tạo Session mới (true) để lưu trạng thái.
            // - Nếu là Stateless (JWT): Controller thường KHÔNG gọi hàm này (chỉ trả Token).
            //   (Nếu lỡ gọi thì sẽ tạo ra 1 session thừa, nhưng không ảnh hưởng logic xác thực).
            req.getSession(true).setAttribute(AUTHENTICATION_KEY, authentication);

        } else {
            // --- TRƯỜNG HỢP ĐĂNG XUẤT (LOGOUT) ---

            // a. Xóa khỏi Request hiện tại
            req.removeAttribute(AUTHENTICATION_KEY);

            // b. Xóa khỏi Session & Hủy Session
            // Chỉ lấy session nếu nó đang tồn tại (false), không tạo mới để logout.
            var session = req.getSession(false);
            if (session != null) {
                session.removeAttribute(AUTHENTICATION_KEY);

                // QUAN TRỌNG: Hủy toàn bộ Session để ngăn chặn Session Fixation Attack
                // Mọi dữ liệu khác trong session cũng sẽ bị xóa.
                session.invalidate();
            }
        }
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
    public void include(String path) {
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

    @Override
    public void alert(AlertType type, String messageKey) {
        this.setFlashAttribute(ALERT_KEY, new Alert(type, messageKey));
    }

    @Override
    public String getRequestPath() {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    // ==================================================================
    // I18N / MESSAGES IMPLEMENTATION
    // ==================================================================

    @Override
    public String getMessage(String key) {
        return getMessage(key, key);
    }

    @Override
    public String getMessage(String key, String defaultValue) {
        try {
            // 1. XÁC ĐỊNH LOCALE (Ưu tiên Session -> Browser)
            Locale locale = null;

            // Kiểm tra trong Session xem LocaleFilter đã set ngôn ngữ chưa
            // Key chuẩn của JSTL là: "javax.servlet.jsp.jstl.fmt.locale"
            HttpSession session = req.getSession(false);
            if (session != null) {
                Object sessionLocale = Config.get(session, Config.FMT_LOCALE);
                if (sessionLocale instanceof Locale) {
                    locale = (Locale) sessionLocale;
                }
            }

            // Nếu không có trong Session, dùng mặc định của trình duyệt
            if (locale == null) {
                locale = req.getLocale();
            }

            // 2. Lấy tên file Resource từ web.xml
            String baseName = req.getServletContext().getInitParameter("jakarta.servlet.jsp.jstl.fmt.localizationContext");
            if (baseName == null || baseName.trim().isEmpty()) {
                baseName = "messages"; // Tên file mặc định
            }

            // 3. Load ResourceBundle & Lấy text
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            return bundle.getString(key);

        } catch (MissingResourceException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
