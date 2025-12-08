package com.nlu.store.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Utility class hỗ trợ Reflection với hiệu năng cao nhờ Caching.
 * Hỗ trợ:
 * 1. Getter: getField(), isField(), field() (Record), direct field access.
 * 2. Setter: setField(), direct field access.
 */
public class ReflectionUtils {

    // Cache cho Getter: Key = "ClassName.fieldName", Value = Function để lấy giá trị
    private static final Map<String, Function<Object, Object>> GETTER_CACHE = new ConcurrentHashMap<>();

    // Cache cho Setter: Key = "ClassName.fieldName", Value = BiConsumer để set giá trị
    private static final Map<String, BiConsumer<Object, Object>> SETTER_CACHE = new ConcurrentHashMap<>();

    private ReflectionUtils() {
        // Prevent instantiation
    }

    /**
     * Lấy giá trị của một field từ object (High Performance).
     *
     * @param target    Object chứa dữ liệu
     * @param fieldName Tên field (ví dụ: "username", "email")
     * @return Giá trị của field
     */
    public static Object get(Object target, String fieldName) {
        if (target == null) return null;
        String key = target.getClass().getName() + "." + fieldName;

        // Lấy từ cache hoặc tạo mới nếu chưa có (computeIfAbsent đảm bảo thread-safe)
        Function<Object, Object> getter = GETTER_CACHE.computeIfAbsent(key, k -> createGetter(target.getClass(), fieldName));

        return getter.apply(target);
    }

    /**
     * Gán giá trị cho một field của object (High Performance).
     *
     * @param target    Object cần gán
     * @param fieldName Tên field
     * @param value     Giá trị cần gán
     */
    public static void set(Object target, String fieldName, Object value) {
        if (target == null) return;
        String key = target.getClass().getName() + "." + fieldName;

        BiConsumer<Object, Object> setter = SETTER_CACHE.computeIfAbsent(key, k -> createSetter(target.getClass(), fieldName));

        setter.accept(target, value);
    }

    // --- Internal Logic tạo Getter ---

    private static Function<Object, Object> createGetter(Class<?> clazz, String fieldName) {
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // 1. Thử tìm method getXxx() (Java Bean chuẩn)
        try {
            Method method = clazz.getMethod("get" + capitalized);
            return obj -> invokeMethod(method, obj);
        } catch (NoSuchMethodException e) { /* ignore */ }

        // 2. Thử tìm method isXxx() (Boolean)
        try {
            Method method = clazz.getMethod("is" + capitalized);
            return obj -> invokeMethod(method, obj);
        } catch (NoSuchMethodException e) { /* ignore */ }

        // 3. Thử tìm method xxx() (Java Record / Fluent)
        try {
            Method method = clazz.getMethod(fieldName);
            return obj -> invokeMethod(method, obj);
        } catch (NoSuchMethodException e) { /* ignore */ }

        // 4. Fallback: Truy cập trực tiếp Field
        try {
            Field field = getFieldRecursively(clazz, fieldName);
            field.setAccessible(true);
            return obj -> getFieldValue(field, obj);
        } catch (NoSuchFieldException e) {
            // Nếu không tìm thấy gì cả, trả về null thay vì throw lỗi để tránh crash flow validate
            System.err.println("[ReflectionUtils] Getter not found for: " + clazz.getName() + "." + fieldName);
            return obj -> null;
        }
    }

    // --- Internal Logic tạo Setter ---

    private static BiConsumer<Object, Object> createSetter(Class<?> clazz, String fieldName) {
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String setterName = "set" + capitalized;

        // 1. Thử tìm method setXxx(Arg)
        // Lưu ý: Setter khó hơn vì cần biết kiểu tham số.
        // Ở đây ta quét tất cả method, tìm method có tên setXxx và 1 tham số.
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                return (obj, val) -> invokeMethod(method, obj, val);
            }
        }

        // 2. Fallback: Truy cập trực tiếp Field
        try {
            Field field = getFieldRecursively(clazz, fieldName);
            field.setAccessible(true);
            return (obj, val) -> setFieldValue(field, obj, val);
        } catch (NoSuchFieldException e) {
            System.err.println("[ReflectionUtils] Setter not found for: " + clazz.getName() + "." + fieldName);
            return (obj, val) -> {}; // No-op consumer
        }
    }

    // --- Helper Methods ---

    private static Field getFieldRecursively(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method " + method.getName(), e);
        }
    }

    private static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get field " + field.getName(), e);
        }
    }

    private static void setFieldValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set field " + field.getName(), e);
        }
    }
}
