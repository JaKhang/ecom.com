package com.nlu.store.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * High-Performance Reflection Wrapper.
 * <p>
 * Purpose: Read/Write object properties efficiently using Caching and Functional Interfaces.
 * This utility is framework-agnostic.
 * </p>
 */
public class ReflectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    // --- CACHES ---
    // Cache for Getters: Key = "ClassName.fieldName", Value = Function to retrieve value
    private static final Map<String, Function<Object, Object>> GETTER_CACHE = new ConcurrentHashMap<>();

    // Cache for Setters: Key = "ClassName.fieldName", Value = BiConsumer to set value
    private static final Map<String, BiConsumer<Object, Object>> SETTER_CACHE = new ConcurrentHashMap<>();

    // Cache for Setter Methods: Used to determine parameter types for JSON conversion
    private static final Map<String, Method> SETTER_METHOD_CACHE = new ConcurrentHashMap<>();

    private ReflectionUtils() {
        // Prevent instantiation
    }

    /**
     * Retrieve a field value from an object (High Performance).
     *
     * @param target    The object instance.
     * @param fieldName The name of the field.
     * @return The value of the field.
     */
    public static Object get(Object target, String fieldName) {
        if (target == null) return null;
        String key = target.getClass().getName() + "." + fieldName;
        return GETTER_CACHE.computeIfAbsent(key, k -> createGetter(target.getClass(), fieldName))
                .apply(target);
    }

    /**
     * Set a field value on an object (High Performance).
     *
     * @param target    The object instance.
     * @param fieldName The name of the field.
     * @param value     The value to set.
     */
    public static void set(Object target, String fieldName, Object value) {
        if (target == null) return;
        String key = target.getClass().getName() + "." + fieldName;
        SETTER_CACHE.computeIfAbsent(key, k -> createSetter(target.getClass(), fieldName))
                .accept(target, value);
    }

    /**
     * Find the Setter Method for a given property.
     * Useful for determining the parameter type (e.g., for Jackson ObjectMapper).
     *
     * @param clazz        The class to search.
     * @param propertyName The property name.
     * @return The Method object, or null if not found.
     */
    public static Method findSetterMethod(Class<?> clazz, String propertyName) {
        String cacheKey = clazz.getName() + "." + propertyName;
        return SETTER_METHOD_CACHE.computeIfAbsent(cacheKey, k -> {
            String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            for (Method method : clazz.getMethods()) {
                // We look for a public method named setXxx with exactly 1 parameter
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    return method;
                }
            }
            return null;
        });
    }

    // ==================================================================================
    // INTERNAL FACTORIES (Getter/Setter Creation Logic)
    // ==================================================================================

    private static Function<Object, Object> createGetter(Class<?> clazz, String fieldName) {
        String cap = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // 1. Try standard Getter: getXxx()
        try { Method m = clazz.getMethod("get" + cap); return o -> invoke(m, o); } catch (Exception e) {}

        // 2. Try boolean Getter: isXxx()
        try { Method m = clazz.getMethod("is" + cap); return o -> invoke(m, o); } catch (Exception e) {}

        // 3. Try fluent/record style: xxx()
        try { Method m = clazz.getMethod(fieldName); return o -> invoke(m, o); } catch (Exception e) {}

        // 4. Fallback: Direct Field Access
        try {
            Field f = getFieldRecursively(clazz, fieldName);
            f.setAccessible(true);
            return o -> getField(f, o);
        } catch (Exception e) {
            // Return null if not found to prevent crashes during validation flows
            return o -> null;
        }
    }

    private static BiConsumer<Object, Object> createSetter(Class<?> clazz, String fieldName) {
        // 1. Try finding a Setter Method first
        Method m = findSetterMethod(clazz, fieldName);
        if (m != null) return (o, v) -> invoke(m, o, v);

        // 2. Fallback: Direct Field Access
        try {
            Field f = getFieldRecursively(clazz, fieldName);
            f.setAccessible(true);
            return (o, v) -> setField(f, o, v);
        } catch (Exception e) {
            return (o, v) -> {}; // No-op consumer
        }
    }

    private static Field getFieldRecursively(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> curr = clazz;
        while (curr != null) {
            try { return curr.getDeclaredField(name); } catch (Exception e) { curr = curr.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }

    // Wrappers to handle checked exceptions inside lambdas
    private static Object invoke(Method m, Object t, Object... args) { try { return m.invoke(t, args); } catch (Exception e) { throw new RuntimeException(e); } }
    private static Object getField(Field f, Object t) { try { return f.get(t); } catch (Exception e) { throw new RuntimeException(e); } }
    private static void setField(Field f, Object t, Object v) { try { f.set(t, v); } catch (Exception e) { throw new RuntimeException(e); } }
}
