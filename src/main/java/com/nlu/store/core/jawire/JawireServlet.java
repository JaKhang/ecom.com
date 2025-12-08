package com.nlu.store.core.jawire;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
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
public class JawireServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(JawireServlet.class);

    // Cache for Reflection to improve performance
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Method> SETTER_CACHE = new ConcurrentHashMap<>();

    @Inject
    private JawireViewResolver jawireViewResolver;

    @Inject
    private ObjectMapper mapper;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            // 1. Parse Request Payload
            JsonNode rootNode = mapper.readTree(req.getReader());
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

            // Lifecycle: Boot
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
            sendResponse(req, resp, component);

        } catch (SecurityException e) {
            logger.warn("Security Alert: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Security Violation");
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            logger.error("Bad Request: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request");
        } catch (Exception e) {
            logger.error("Internal Server Error processing Jawire request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
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

        Iterator<Map.Entry<String, JsonNode>> fields = updatesNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String propertyName = entry.getKey();
            JsonNode valueNode = entry.getValue();

            // Find Setter using Java Beans Introspector (More robust than string concatenation)
            Method setter = findSetter(component.getClass(), propertyName);

            if (setter != null) {
                // Convert value to the correct type expected by the setter
                Object newValue = mapper.convertValue(valueNode, setter.getParameterTypes()[0]);

                // Lifecycle: Updating
                component.updating(propertyName, newValue);

                // Invoke Setter
                setter.invoke(component, newValue);

                // Lifecycle: Updated
                component.updated(propertyName, newValue);
            } else {
                logger.debug("No public setter found for property '{}' in {}", propertyName, component.getClass().getName());
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
                .append(" jw-snapshot=\"").append(jsonResponse).append("\">")
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
                @Override public boolean isReady() { return true; }
                @Override public void setWriteListener(WriteListener w) {}
                @Override public void write(int b) { writer.write(b); }
            };
        }
    }
}
