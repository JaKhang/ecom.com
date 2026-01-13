package com.nlu.store.core.jawire;

import com.nlu.store.core.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWire Framework Utilities.
 * <p>
 * Handles domain-specific logic including:
 * 1. Filtering fields marked with @Model.
 * 2. Validating methods marked with @Action.
 * 3. Extracting component state for snapshots.
 * </p>
 */
public class JawireUtils {

    private static final Logger logger = LoggerFactory.getLogger(JawireUtils.class);

    // Cache: List of field names marked with @Model for each Component class
    private static final Map<Class<?>, List<String>> MODEL_FIELD_NAMES_CACHE = new ConcurrentHashMap<>();

    private JawireUtils() {
        // Prevent instantiation
    }

    /**
     * Extracts the public state of a component.
     * Only fields annotated with @Model are included.
     *
     * @param component The component instance.
     * @return A map of field names and their values.
     */
    public static Map<String, Object> extractState(Object component) {
        Map<String, Object> state = new HashMap<>();
        List<String> modelFields = getModelFieldNames(component.getClass());

        for (String fieldName : modelFields) {
            // Use ReflectionUtils for high-performance retrieval
            Object value = ReflectionUtils.get(component, fieldName);
            state.put(fieldName, value);
        }
        return state;
    }

    /**
     * Checks if a property is a valid Model field.
     * Used for Security Checks during client-side updates.
     *
     * @param clazz     The component class.
     * @param fieldName The property name.
     * @return true if the field has @Model, false otherwise.
     */
    public static boolean isModelField(Class<?> clazz, String fieldName) {
        return getModelFieldNames(clazz).contains(fieldName);
    }

    /**
     * Checks if a method is exposed as a valid Action.
     *
     * @param method The method to check.
     * @return true if the method has @Action, false otherwise.
     */
    public static boolean isActionMethod(Method method) {
        return method.isAnnotationPresent(Action.class);
    }

    /**
     * Retrieves the list of field names marked with @Model (Cached).
     * Traverses up the class hierarchy until Component or Object is reached.
     */
    private static List<String> getModelFieldNames(Class<?> clazz) {
        return MODEL_FIELD_NAMES_CACHE.computeIfAbsent(clazz, k -> {
            List<String> names = new ArrayList<>();
            Class<?> current = k;

            // Traverse hierarchy
            while (current != null && current != Object.class && Component.class.isAssignableFrom(current)) {
                for (Field field : current.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Model.class)) {
                        names.add(field.getName());
                    }
                }
                current = current.getSuperclass();
            }
            return names;
        });
    }
}
