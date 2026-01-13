package com.nlu.store.core.jawire;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.utils.ReflectionUtils;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.HttpContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * <strong>Base abstract class for all Jawire UI components.</strong>
 * <p>
 * This class serves as the foundation for building reactive, stateful UI components in a
 * server-side Java environment. It bridges the gap between the server and client by
 * managing state synchronization, security, and the component lifecycle.
 * </p>
 *
 * <h2>Key Responsibilities:</h2>
 * <ul>
 *     <li><strong>State Management:</strong> Securely extracts only {@code @Model} fields for the client.</li>
 *     <li><strong>Security:</strong> Implements HMAC-SHA256 signing to ensure client-side state is not tampered with.</li>
 *     <li><strong>Lifecycle:</strong> Provides a rich set of hooks (mount, boot, hydrated, etc.).</li>
 *     <li><strong>Validation:</strong> Integrated error handling and validation support.</li>
 * </ul>
 *
 * @author Bamboo Team
 * @version 2.0.0 (Refactored for JWire Core)
 */
public abstract class Component implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Component.class);
    private static final String HMAC_ALGO = "HmacSHA256";

    /**
     * Unique identifier for this component instance.
     * Usually generated via {@link ULID} during the {@link #mount()} phase.
     */
    @Model
    private String id;

    /**
     * Stores validation errors for the current request cycle.
     */
    protected Map<String, String> errors = new HashMap<>();

    /**
     * Jackson ObjectMapper for JSON processing.
     * Transient to avoid serialization.
     */
    @Inject
    protected transient ObjectMapper mapper;

    @Inject
    protected transient PropertySource config;


    @JsonIgnore
    protected HttpContext context;

    // --- GETTERS / SETTERS ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.context = httpContext;
    }

    @JsonIgnore
    public HttpContext getHttpContext() {
        return context;
    }

    // --- LIFECYCLE HOOKS ---

    /**
     * <strong>Lifecycle Hook: Mount</strong>
     * Called <em>only once</em> when the component is first instantiated.
     */
    public void mount() {
    }

    /**
     * Clears the validation errors.
     */
    protected void clear() {
        this.errors.clear();
    }

    /**
     * <strong>Lifecycle Hook: Boot</strong>
     * Called at the beginning of <em>every</em> request cycle.
     */
    public void boot() {}

    /**
     * <strong>Lifecycle Hook: Hydrated</strong>
     * Called after the component's state has been fully restored from the client.
     */
    public void hydrated() {}

    /**
     * <strong>Lifecycle Hook: Updating</strong>
     * Called <strong>before</strong> a specific property is updated by the client.
     */
    public void updating(String field, Object value) {}

    /**
     * <strong>Lifecycle Hook: Updated</strong>
     * Called <strong>after</strong> a specific property has been updated by the client.
     */
    public void updated(String field, Object value) {}

    /**
     * <strong>Lifecycle Hook: Rendering</strong>
     * Called immediately before the view is rendered.
     */
    public void rendering() {}

    // --- CORE FUNCTIONS ---

    /**
     * Defines the path to the View (JSP/Template) for this component.
     * @return The relative path to the view file.
     */
    public abstract String view();

    /**
     * <strong>Server to Client:</strong> Serializes the component state.
     * <p>
     * 1. Uses {@link JawireUtils#extractState(Object)} to get only fields annotated with {@code @Model}.
     * 2. Serializes that map to JSON.
     * 3. Signs the JSON with HMAC.
     * </p>
     *
     * @return A {@link JawireResponse} record containing the JSON state and checksum.
     */
    @JsonIgnore
    public JawireResponse dehydrate() {
        ensureMapper();
        try {
            // 1. Tạo bản sao mapper và áp dụng bộ lọc ModelOnlyIntrospector

            // 2. Serialize trực tiếp 'this'.
            // Jackson sẽ tự động bỏ qua các field không có @Model.
            ObjectMapper mapper = this.mapper.copy();
            mapper.setAnnotationIntrospector(new ModelOnlyIntrospector());
            String jsonState = mapper.writeValueAsString(this);

            // 3. Sign
            String checksum = sign(jsonState);

            return new JawireResponse(jsonState, checksum, this.getClass().getName());
        } catch (JsonProcessingException e) {
            logger.error("JWire: Serialization failed for component {}", this.getClass().getName(), e);
            throw new IllegalStateException("JWire: Failed to serialize component state.", e);
        }
    }

    /**
     * <strong>Client to Server:</strong> Restores the component state.
     * <p>
     * 1. Verifies the checksum.
     * 2. Parses JSON to a Map.
     * 3. Iterates and securely updates fields using {@link JawireUtils} and {@link ReflectionUtils}.
     * </p>
     *
     * @param jsonState      The JSON string representing the component's state.
     * @param clientChecksum The HMAC signature provided by the client.
     */
    public void hydrate(String jsonState, String clientChecksum) {
        // 1. Verify Integrity
        if (!verify(jsonState, clientChecksum)) {
            logger.warn("JWire Security: Checksum mismatch for component {}", this.getClass().getName());
            throw new SecurityException("JWire Security Alert: Data tampering detected.");
        }

        ensureMapper();
        try {
            // 2. Parse JSON to Map
            ObjectMapper mapper = this.mapper.copy();
            mapper.setAnnotationIntrospector(new ModelOnlyIntrospector());
            mapper.readerForUpdating(this).readValue(jsonState);

            // 4. Lifecycle Hook
            this.hydrated();

        } catch (JsonProcessingException e) {
            logger.error("JWire: Hydration failed for component {}", this.getClass().getName(), e);
            throw new IllegalArgumentException("JWire: Invalid JSON state provided.", e);
        }
    }

    // --- VALIDATION LOGIC ---

    /**
     * Validates the component using the provided {@link Validator}.
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> boolean validate(Validator<T> validator) {
        Objects.requireNonNull(validator, "Validator must not be null");

        this.errors.clear();

        ValidateResult result = validator.validate((T) this);

        if (result.hasError()) {
            this.errors.putAll(result.details());
            return false;
        }
        return true;
    }

    public boolean hasError(String field) {
        return errors.containsKey(field);
    }

    public String getError(String field) {
        return errors.get(field);
    }

    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    // --- SECURITY UTILS ---

    private String sign(String data) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGO);
            hmac.init(new SecretKeySpec(loadSecretKey(), HMAC_ALGO));
            byte[] signature = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("JWire: Cryptography initialization failed.", e);
        }
    }

    private boolean verify(String jsonState, String clientChecksum) {
        if (jsonState == null || clientChecksum == null) return false;
        String serverChecksum = sign(jsonState);
        return MessageDigest.isEqual(
                serverChecksum.getBytes(StandardCharsets.UTF_8),
                clientChecksum.getBytes(StandardCharsets.UTF_8)
        );
    }

    private byte[] loadSecretKey() {
        String key = config.getProperty("jawire.dev.key", "2A51838556726CDED45FC952B5892");
        return key.getBytes(StandardCharsets.UTF_8);
    }

    private void ensureMapper() {
        if (this.mapper == null) {
            throw new IllegalStateException("JWire: ObjectMapper is null. " +
                    "Ensure the component is managed by CDI or mapper is manually injected.");
        }
    }

    // --- DTO ---

    /**
     * Immutable Data Transfer Object for sending component state to the client.
     */
    public record JawireResponse(String data, String checksum, String component) implements Serializable {
    }
}
