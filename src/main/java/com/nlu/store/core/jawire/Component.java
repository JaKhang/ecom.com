package com.nlu.store.core.jawire;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
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
 *     <li><strong>State Management:</strong> Serializes component state to JSON for the client and restores it on subsequent requests.</li>
 *     <li><strong>Security:</strong> Implements HMAC-SHA256 signing to ensure client-side state is not tampered with.</li>
 *     <li><strong>Lifecycle:</strong> Provides a rich set of hooks (mount, boot, hydrated, etc.) for executing logic at specific points.</li>
 *     <li><strong>Validation:</strong> Integrated error handling and validation support.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * public class Counter extends Component {
 *     public int count = 0;
 *
 *     public void increment() {
 *         this.count++;
 *     }
 *
 *     &#64;Override
 *     public String render() {
 *         return "/components/counter.jsp";
 *     }
 * }
 * </pre>
 *
 * @author Bamboo Team
 * @version 1.3.0
 */
public abstract class Component implements Serializable {

    /**
     * Recommended for Serializable classes to maintain compatibility during deserialization.
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Component.class);

    private static final String HMAC_ALGO = "HmacSHA256";

    /**
     * The secret key used for HMAC signing. Loaded once at class initialization.
     */
    private static final byte[] SECRET_KEY_BYTES = loadSecretKey();

    /**
     * Unique identifier for this component instance.
     * Usually generated via {@link ULID} during the {@link #mount()} phase.
     */
    private String id;

    /**
     * Stores validation errors for the current request cycle.
     * <p>
     * Key: The field name (e.g., "email").<br>
     * Value: The error message (e.g., "Invalid email format").
     * </p>
     */
    protected Map<String, String> errors = new HashMap<>();

    /**
     * Jackson ObjectMapper for JSON processing.
     * <p>
     * <strong>Note:</strong> This field is {@code transient} and annotated with {@code @JsonIgnore}
     * because it should not be serialized to the client or Java streams.
     * It must be injected by the CDI container (e.g., Weld, Spring) or manually set.
     * </p>
     */
    @JsonIgnore
    @Inject
    protected transient ObjectMapper mapper;

    // --- GETTERS / SETTERS ---

    /**
     * Gets the unique ID of this component instance.
     * @return The component ID string.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique ID for this component.
     * @param id The new ID string.
     */
    public void setId(String id) {
        this.id = id;
    }

    // --- LIFECYCLE HOOKS (Template Method Pattern) ---

    /**
     * <strong>Lifecycle Hook: Mount</strong>
     * <p>
     * Called <em>only once</em> when the component is first instantiated (initial page load).
     * Use this method to initialize properties, load data from the database, or set up defaults.
     * </p>
     */
    public void mount() {
        this.id = "jw" + ULID.fast();
        this.clear();
    }

    /**
     * Clears the validation errors and resets transient state.
     * Can be overridden to clear custom transient fields.
     */
    protected void clear() {
        this.errors.clear();
    }

    /**
     * <strong>Lifecycle Hook: Boot</strong>
     * <p>
     * Called at the beginning of <em>every</em> request cycle (both initial load and subsequent updates).
     * Use this for dependency injection verification or resetting transient state that shouldn't persist.
     * </p>
     */
    public void boot() {}

    /**
     * <strong>Lifecycle Hook: Hydrated</strong>
     * <p>
     * Called after the component's state has been fully restored (hydrated) from the client payload.
     * At this point, all public properties reflect the values sent from the client.
     * </p>
     */
    public void hydrated() {}

    /**
     * <strong>Lifecycle Hook: Updating</strong>
     * <p>
     * Called <strong>before</strong> a specific property is updated by the client.
     * </p>
     *
     * @param field The name of the property being updated.
     * @param value The new value that is about to be assigned.
     */
    public void updating(String field, Object value) {}

    /**
     * <strong>Lifecycle Hook: Updated</strong>
     * <p>
     * Called <strong>after</strong> a specific property has been updated by the client.
     * Use this to trigger side effects based on property changes (e.g., validation).
     * </p>
     *
     * @param field The name of the property that was updated.
     * @param value The new value that was assigned.
     */
    public void updated(String field, Object value) {}

    /**
     * <strong>Lifecycle Hook: Rendering</strong>
     * <p>
     * Called immediately before the {@link #view()} method.
     * Use this to prepare data specifically for the view (e.g., calculating totals, formatting dates)
     * without modifying the persistent state.
     * </p>
     */
    public void rendering() {}

    // --- CORE FUNCTIONS ---

    /**
     * Defines the path to the View (JSP/Template) for this component.
     *
     * @return The relative path to the view file (e.g., {@code "/WEB-INF/components/TodoList.jsp"}).
     */
    public abstract String view();

    /**
     * <strong>Server to Client:</strong> Serializes the component state.
     * <p>
     * Converts the current instance to a JSON string and generates a cryptographic signature (HMAC)
     * to ensure integrity when the data returns from the client.
     * </p>
     *
     * @return A {@link JawireResponse} record containing the JSON state, checksum, and component class name.
     * @throws IllegalStateException If the {@code ObjectMapper} is missing or serialization fails.
     */
    @JsonIgnore
    public JawireResponse dehydrate() {
        ensureMapper();
        try {
            String jsonState = mapper.writeValueAsString(this);
            String checksum = sign(jsonState);
            return new JawireResponse(jsonState, checksum, this.getClass().getName());
        } catch (JsonProcessingException e) {
            logger.error("Jawire: Serialization failed for component {}", this.getClass().getName(), e);
            throw new IllegalStateException("Jawire: Failed to serialize component state.", e);
        }
    }

    /**
     * <strong>Client to Server:</strong> Restores the component state.
     * <p>
     * 1. Verifies the checksum to prevent tampering.<br>
     * 2. Updates the current instance with data from the JSON payload.<br>
     * 3. Triggers the {@link #hydrated()} lifecycle hook.
     * </p>
     *
     * @param jsonState      The JSON string representing the component's state.
     * @param clientChecksum The HMAC signature provided by the client.
     * @throws SecurityException        If the checksum verification fails (potential hacking attempt).
     * @throws IllegalArgumentException If the JSON format is invalid.
     */
    public void hydrate(String jsonState, String clientChecksum) {
        if (!verify(jsonState, clientChecksum)) {
            logger.warn("Jawire Security: Checksum mismatch for component {}", this.getClass().getName());
            throw new SecurityException("Jawire Security Alert: Data tampering detected.");
        }

        ensureMapper();
        try {
            // Uses updateValue to efficiently merge JSON data into the existing instance
            mapper.updateValue(this, jsonState);
            this.hydrated();
        } catch (JsonProcessingException e) {
            logger.error("Jawire: Hydration failed for component {}", this.getClass().getName(), e);
            throw new IllegalArgumentException("Jawire: Invalid JSON state provided.", e);
        }
    }

    // --- VALIDATION LOGIC ---

    /**
     * Validates the component using the provided {@link Validator}.
     * <p>
     * If validation fails, errors are populated into the {@link #errors} map.
     * </p>
     *
     * @param validator The validator implementation to use. Must not be null.
     * @param <T>       The type of the component being validated.
     * @return {@code true} if validation passes, {@code false} if there are errors.
     * @throws NullPointerException if the validator is null.
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> boolean validate(Validator<T> validator) {
        Objects.requireNonNull(validator, "Validator must not be null");

        this.errors.clear();

        // Unchecked cast is safe assuming the caller provides a Validator matching the component type
        ValidateResult result = validator.validate((T) this);

        if (result.hasError()) {
            this.errors.putAll(result.details());
            return false;
        }
        return true;
    }

    /**
     * Checks if a specific field has a validation error.
     *
     * @param field The name of the field.
     * @return {@code true} if an error exists for the field.
     */
    public boolean hasError(String field) {
        return errors.containsKey(field);
    }

    /**
     * Retrieves the error message for a specific field.
     *
     * @param field The name of the field.
     * @return The error message, or {@code null} if no error exists.
     */
    public String getError(String field) {
        return errors.get(field);
    }

    /**
     * Retrieves a read-only view of all validation errors.
     *
     * @return An unmodifiable map of field names to error messages.
     */
    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    // --- SECURITY UTILS ---

    /**
     * Generates an HMAC-SHA256 signature for the given data string.
     *
     * @param data The string data to sign.
     * @return The Base64 encoded signature.
     * @throws IllegalStateException If the HMAC algorithm is not available.
     */
    private String sign(String data) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGO);
            hmac.init(new SecretKeySpec(SECRET_KEY_BYTES, HMAC_ALGO));
            byte[] signature = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Jawire: Cryptography initialization failed.", e);
        }
    }

    /**
     * Verifies the data integrity using a constant-time comparison.
     * <p>
     * Constant-time comparison is crucial to prevent timing attacks where an attacker
     * could guess the signature by measuring how long the comparison takes.
     * </p>
     *
     * @param jsonState      The data to verify.
     * @param clientChecksum The signature provided by the client.
     * @return {@code true} if the signature is valid, {@code false} otherwise.
     */
    private boolean verify(String jsonState, String clientChecksum) {
        if (jsonState == null || clientChecksum == null) return false;
        String serverChecksum = sign(jsonState);
        return MessageDigest.isEqual(
                serverChecksum.getBytes(StandardCharsets.UTF_8),
                clientChecksum.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Loads the secret key from the environment variable {@code JAWIRE_SECRET_KEY}.
     * <p>
     * <strong>Security Requirement:</strong> The environment variable must be set.
     * If missing, the application will fail to start to prevent insecure defaults.
     * </p>
     *
     * @return The bytes of the secret key.
     * @throws IllegalStateException If the environment variable is missing.
     */
    private static byte[] loadSecretKey() {
        String key = System.getenv("JAWIRE_SECRET_KEY");
        if (key == null || key.isBlank()) {
            // Allow a system property override for testing environments
            String devKey = System.getProperty("jawire.dev.key");
            if (devKey != null) {
                logger.warn("JAWIRE SECURITY WARNING: Using system property key. Do not use in production.");
                return devKey.getBytes(StandardCharsets.UTF_8);
            }

            throw new IllegalStateException("JAWIRE_SECRET_KEY environment variable is missing. " +
                    "Security cannot be guaranteed. Please set this variable.");
        }
        return key.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Checks if the ObjectMapper is properly injected.
     *
     * @throws IllegalStateException If the mapper is null.
     */
    private void ensureMapper() {
        if (this.mapper == null) {
            throw new IllegalStateException("Jawire: ObjectMapper is null. " +
                    "Ensure the component is managed by CDI or mapper is manually injected.");
        }
    }

    // --- DTO ---

    /**
     * Immutable Data Transfer Object for sending component state to the client.
     *
     * @param data      The JSON representation of the component's state.
     * @param checksum  The cryptographic signature of the data.
     * @param component The fully qualified class name of the component.
     */
    public record JawireResponse(String data, String checksum, String component) implements Serializable {
    }
}
