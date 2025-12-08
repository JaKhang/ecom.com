package com.nlu.store.core.web.bind;

import com.nlu.store.core.data.MultipartFile;
import com.nlu.store.core.data.StandardMultipartFile;
import com.nlu.store.core.utils.ReflectionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

public class MultipartBodyParser implements BodyParser {

    private final ConversionService conversionService;

    public MultipartBodyParser(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.startsWith("multipart/form-data");
    }

    @Override
    public <T> T parse(HttpServletRequest request, Class<T> targetType) throws IOException {
        try {
            T instance = targetType.getDeclaredConstructor().newInstance();

            for (Field field : targetType.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                String fieldName = field.getName();
                Class<?> fieldType = field.getType();

                // 1. Xử lý Collection (List<MultipartFile> hoặc List<String>)
                if (Collection.class.isAssignableFrom(fieldType)) {
                    bindCollection(request, instance, fieldName, field);
                }
                // 2. Xử lý Single File (MultipartFile)
                else if (fieldType == MultipartFile.class) {
                    bindSingleFile(request, instance, fieldName);
                }
                // 3. Xử lý Single Text (String, int...)
                else {
                    bindSingleText(request, instance, fieldName, fieldType);
                }
            }
            return instance;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to bind multipart data to " + targetType.getSimpleName(), e);
        }
    }

    // --- Helper Methods ---

    private void bindCollection(HttpServletRequest request, Object instance, String fieldName, Field field) throws IOException, ServletException {
        Class<?> itemType = getGenericType(field);
        Collection<Object> collection = createCollectionInstance(field.getType());

        // A. Nếu là Collection File (List<MultipartFile>)
        if (itemType == MultipartFile.class) {
            for (Part part : request.getParts()) {
                if (fieldName.equals(part.getName()) && part.getSize() > 0) {
                    collection.add(new StandardMultipartFile(part));
                }
            }
        }
        // B. Nếu là Collection Text (List<String>, List<Integer>...)
        else {
            String[] values = request.getParameterValues(fieldName);
            if (values != null) {
                for (String val : values) {
                    Object converted = conversionService.convert(val, itemType);
                    collection.add(converted);
                }
            }
        }

        // Chỉ set nếu collection không rỗng (hoặc tùy logic business của bạn)
        if (!collection.isEmpty()) {
            ReflectionUtils.set(instance, fieldName, collection);
        }
    }

    private void bindSingleFile(HttpServletRequest request, Object instance, String fieldName) throws IOException {
        Part part = getPartSafe(request, fieldName);
        if (part != null && part.getSize() > 0) {
            ReflectionUtils.set(instance, fieldName, new StandardMultipartFile(part));
        }
    }

    private void bindSingleText(HttpServletRequest request, Object instance, String fieldName, Class<?> fieldType) {
        String paramValue = request.getParameter(fieldName);
        if (paramValue != null) {
            Object convertedValue = conversionService.convert(paramValue, fieldType);
            ReflectionUtils.set(instance, fieldName, convertedValue);
        }
    }

    // --- Utilities ---

    private Part getPartSafe(HttpServletRequest request, String name) throws IOException {
        try {
            return request.getPart(name);
        } catch (ServletException e) {
            throw new IOException("Error retrieving part '" + name + "'", e);
        }
    }

    private Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return String.class;
    }

    private Collection<Object> createCollectionInstance(Class<?> fieldType) {
        if (Set.class.isAssignableFrom(fieldType)) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }
}
