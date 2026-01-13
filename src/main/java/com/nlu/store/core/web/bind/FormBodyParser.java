package com.nlu.store.core.web.bind;

import com.nlu.store.core.utils.ReflectionUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.*;
import java.util.*;

public class FormBodyParser implements BodyParser {

    private final ConversionService conversionService;

    public FormBodyParser(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supports(String contentType) {
        return contentType == null || contentType.startsWith("application/x-www-form-urlencoded");
    }

    @Override
    public <T> T parse(HttpServletRequest request, Class<T> targetType) {
        try {
            // 1. Tạo instance
            T instance = targetType.getDeclaredConstructor().newInstance();

            // 2. Duyệt qua các field để map dữ liệu
            for (Field field : getAllFields(targetType)) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                String paramName = field.getName();
                Class<?> fieldType = field.getType();

                // Phân loại xử lý
                if (Collection.class.isAssignableFrom(fieldType)) {
                    bindCollection(request, instance, field, paramName);
                } else if (fieldType.isArray()) {
                    bindArray(request, instance, field, paramName);
                } else {
                    bindSingleValue(request, instance, field, paramName);
                }
            }
            return instance;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to bind form data to " + targetType.getSimpleName(), e);
        }
    }

    // --- Helper Methods using ReflectionUtils ---

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    private void bindSingleValue(HttpServletRequest request, Object instance, Field field, String paramName) {
        String value = request.getParameter(paramName);
        if (value != null) {
            Object converted = conversionService.convert(value, field.getType());

            // UPDATE: Dùng ReflectionUtils để set (tự động tìm Setter hoặc Field)
            ReflectionUtils.set(instance, paramName, converted);
        }
    }

    private void bindCollection(HttpServletRequest request, Object instance, Field field, String paramName) {
        String[] values = request.getParameterValues(paramName);
        if (values == null || values.length == 0) return;

        Class<?> itemType = getGenericType(field);
        Collection<Object> collection = createCollectionInstance(field.getType());

        for (String val : values) {
            Object converted = conversionService.convert(val, itemType);
            collection.add(converted);
        }

        // UPDATE: Dùng ReflectionUtils
        ReflectionUtils.set(instance, paramName, collection);
    }

    private void bindArray(HttpServletRequest request, Object instance, Field field, String paramName) {
        String[] values = request.getParameterValues(paramName);
        if (values == null) return;

        Class<?> componentType = field.getType().getComponentType();
        Object array = Array.newInstance(componentType, values.length);

        for (int i = 0; i < values.length; i++) {
            Object converted = conversionService.convert(values[i], componentType);
            Array.set(array, i, converted);
        }

        // UPDATE: Dùng ReflectionUtils
        ReflectionUtils.set(instance, paramName, array);
    }

    // --- Utilities giữ nguyên ---
    private Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return String.class;
    }

    private Collection<Object> createCollectionInstance(Class<?> fieldType) {
        if (Set.class.isAssignableFrom(fieldType)) return new HashSet<>();
        return new ArrayList<>();
    }
}
