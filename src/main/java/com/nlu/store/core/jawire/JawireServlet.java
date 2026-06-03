package com.nlu.store.core.jawire;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nlu.store.core.utils.ReflectionUtils;
import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.HttpContext;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <strong>Jawire Request Handler</strong>
 * <p>
 * Handles AJAX requests from the client-side Jawire JavaScript.
 * Optimized for security and performance.
 * </p>
 */
@WebServlet(name = "JawireServlet", urlPatterns = "/jawire/update")
public class JawireServlet extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(JawireServlet.class);

    // Cache for Reflection to improve performance
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Method> SETTER_CACHE = new ConcurrentHashMap<>();

    @Inject
    private JawireViewResolver jawireViewResolver;

    @Inject
    private ObjectMapper mapper;

    @Override
    protected void doPost(HttpContext ctx) {
        ctx.getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            // 1. Parse Request Payload
            JsonNode rootNode = mapper.readTree(ctx.getRequest().getReader());
            JsonNode snapshotNode = rootNode.get("snapshot");

            if (snapshotNode == null) {
                throw new IllegalArgumentException("Invalid payload: 'snapshot' is missing.");
            }

            // 2. Instantiate Component (Securely)
            Component component = createComponent(snapshotNode);

            // Sync ID from Client (Crucial for Nested Components)
            if (rootNode.hasNonNull("componentId")) {
                component.setId(rootNode.get("componentId").asText());
            }

            component.setHttpContext(ctx);

            // Lifecycle: Boot
            component.authorize(ctx.authentication());
            component.boot();


            // Restore state (Hydrate)
            String data = snapshotNode.path("data").asText();
            String checksum = snapshotNode.path("checksum").asText();
            component.hydrate(data, checksum);

            // 3. Process Property Updates
            processUpdates(component, rootNode.get("updates"));

            // 4. Invoke Action
            processAction(component, rootNode.get("action"));

            // 5. Render View & Send Response
            sendResponse(ctx.getRequest(), ctx.getResponse(), component);

        } catch (SecurityException e) {
            logger.warn("Security Alert: {}", e.getMessage());
            e.printStackTrace();
            ctx.sendError(HttpServletResponse.SC_FORBIDDEN, "Security Violation");
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            logger.error("Bad Request: {}", e.getMessage());
            e.printStackTrace();
            ctx.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request");
        } catch (JawireUpdateException e) {
            ctx.sendRestResponse(new ExceptionResponse(
                    e.getError(),
                    e.getMessage(),
                    e.getStatus()
            ));
        } catch (Exception e) {
            logger.error("Internal Server Error processing Jawire request", e);
            System.out.println(e.getClass().getName());
            if (e.getCause() instanceof JawireUpdateException je){
                ctx.setStatus(je.getStatus());
                ctx.sendRestResponse(new ExceptionResponse(
                        je.getError(),
                        ctx.getMessage(je.getLocalizedMessage()),
                        je.getStatus()
                ));
            } else {
                ctx.setStatus(500);
                e.printStackTrace();
                ctx.sendRestResponse(new ExceptionResponse(
                        "Unknown",
                        ctx.getMessage("server.error"),
                        500
                ));
            }



        }
    }

    // --- CORE LOGIC METHODS ---

    private Component createComponent(JsonNode snapshotNode) throws Exception {
        String className = snapshotNode.path("component").asText();

        // SECURITY CHECK: Prevent arbitrary class instantiation
        Class<?> clazz = Class.forName(className);
        if (!Component.class.isAssignableFrom(clazz)) {
            throw new SecurityException("Attempt to instantiate unauthorized class: " + className);
        }

        // CDI handles dependency injection (@Inject) automatically here
        return (Component) CDI.current().select(clazz).get();
    }

    private void processUpdates(Component component, JsonNode updatesNode) throws Exception {
        if (updatesNode == null || !updatesNode.isObject()) return;


        ObjectMapper updateMapper = mapper.copy();
        updateMapper.setAnnotationIntrospector(new ModelOnlyIntrospector());

        updateMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Iterator<Map.Entry<String, JsonNode>> fields = updatesNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String propertyName = entry.getKey();
            JsonNode valueNode = entry.getValue();

            // 2. SECURITY CHECK: Chỉ cho phép update field có @Model
            if (!JawireUtils.isModelField(component.getClass(), propertyName)) {
                logger.warn("Security: Blocked update on non-model field '{}'", propertyName);
                continue;
            }

            try {
                // 3. Lifecycle: Updating
                // Lưu ý: Lúc này chưa có giá trị mới dạng Object, chỉ có JsonNode
                // Nếu muốn lấy giá trị cũ để so sánh, có thể dùng ReflectionUtils.get(component, propertyName)
                component.updating(propertyName, valueNode);

                // 4. CORE: Dùng Jackson để update trường này vào Component
                // Tạo một ObjectNode nhỏ chứa đúng 1 field đang xét để đưa cho Jackson
                ObjectNode singleFieldUpdate = mapper.createObjectNode();
                singleFieldUpdate.set(propertyName, valueNode);

                // Jackson sẽ tự tìm setter hoặc field tương ứng để điền dữ liệu
                // Nó tự động convert String -> ULID, String -> LocalDateTime...
                updateMapper.readerForUpdating(component).readValue(singleFieldUpdate);

                // 5. Lifecycle: Updated
                // Lấy giá trị thực tế đã được set vào component để pass vào hook
                Object newValue = ReflectionUtils.get(component, propertyName);
                component.updated(propertyName, newValue);

            } catch (Exception e) {
                logger.error("JWire: Error updating property '{}'", propertyName, e);
                e.printStackTrace();
            }
        }
    }

    private void processAction(Component component, JsonNode actionNode) throws Exception {
        if (actionNode == null || actionNode.isNull()) return;

        String methodName = actionNode.path("method").asText();
        if ("$refresh".equals(methodName) || methodName.isEmpty()) return;

        JsonNode paramsNode = actionNode.get("params");
        invokeMethod(component, methodName, paramsNode);
    }

    private void sendResponse(HttpServletRequest req, HttpServletResponse resp, Component component) throws Exception {
        // Lifecycle: Rendering
        component.rendering();

        // Make component available in JSP
        req.setAttribute("component", component);

        // Render JSP to String
        String viewPath = jawireViewResolver.resolve(component.view());
        String htmlContent = renderJspToString(viewPath, req, resp);

        // Dehydrate (Create new snapshot)
        Component.JawireResponse responsePayload = component.dehydrate();

        // Escape JSON for HTML Attribute to prevent XSS/Breaking HTML
        // If commons-text is not available, use a simple helper, but StringEscapeUtils is best.
        // String jsonResponse = StringEscapeUtils.escapeHtml4(mapper.writeValueAsString(responsePayload));

        // Fallback simple escaping if library not present (matches original logic but cleaner)
        String rawJson = mapper.writeValueAsString(responsePayload);
        String jsonResponse = rawJson.replace("\"", "&quot;");

        String finalHtml = new StringBuilder()
                .append("<div id=\"").append(component.getId()).append("\"")
                .append(" jw-snapshot='").append(jsonResponse).append("'>")
                .append(htmlContent)
                .append("</div>")
                .toString();

        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(finalHtml);
    }

    // --- UTILITIES & CACHING ---

    /**
     * Finds a setter method for a property using Introspector and Caching.
     */
    private Method findSetter(Class<?> clazz, String propertyName) {
        String cacheKey = clazz.getName() + "." + propertyName;
        return SETTER_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if (pd.getName().equals(propertyName)) {
                        return pd.getWriteMethod();
                    }
                }
            } catch (Exception e) {
                logger.error("Error inspecting class {}", clazz.getName(), e);
            }
            return null;
        });
    }

    /**
     * Finds and invokes a method matching name and parameter count.
     */
    private void invokeMethod(Object component, String methodName, JsonNode paramsNode) throws Exception {
        int paramCount = (paramsNode != null && paramsNode.isArray()) ? paramsNode.size() : 0;
        String cacheKey = component.getClass().getName() + "." + methodName + "#" + paramCount;

        Method method = METHOD_CACHE.computeIfAbsent(cacheKey, k -> {
            for (Method m : component.getClass().getMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == paramCount) {
                    return m;
                }
            }
            return null;
        });

        if (method == null) {
            throw new NoSuchMethodException("Method '" + methodName + "' with " + paramCount + " params not found in " + component.getClass().getName());
        }

        Object[] args = new Object[paramCount];
        if (paramCount > 0) {
            Class<?>[] types = method.getParameterTypes();
            for (int i = 0; i < paramCount; i++) {
                args[i] = mapper.convertValue(paramsNode.get(i), types[i]);
            }
        }

        method.invoke(component, args);
    }

    private String renderJspToString(String viewPath, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        StringWriter stringWriter = new StringWriter();
        CharResponseWrapper wrapper = new CharResponseWrapper(resp, stringWriter);
        req.getRequestDispatcher(viewPath).include(req, wrapper);
        return stringWriter.toString().trim();
    }

    /**
     * Static wrapper class is better for memory and GC than anonymous inner class.
     */
    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final StringWriter writer;

        public CharResponseWrapper(HttpServletResponse response, StringWriter writer) {
            super(response);
            this.writer = writer;
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(writer);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener w) {
                }

                @Override
                public void write(int b) {
                    writer.write(b);
                }
            };
        }
    }
}
