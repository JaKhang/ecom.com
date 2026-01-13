package com.nlu.store.core.jawire;

public class ModelOnlyIntrospector extends com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean hasIgnoreMarker(final com.fasterxml.jackson.databind.introspect.AnnotatedMember m) {
        // Chỉ can thiệp vào Field
        if (m instanceof com.fasterxml.jackson.databind.introspect.AnnotatedField) {
            // Lấy class chứa field này
            Class<?> declaringClass = m.getDeclaringClass();

            // LOGIC QUAN TRỌNG:
            // Chỉ áp dụng luật "phải có @Model" nếu class khai báo là một Component (hoặc con của Component).
            if (Component.class.isAssignableFrom(declaringClass)) {
                // Nếu là Component mà không có @Model -> IGNORE (return true)
                return !m.hasAnnotation(Model.class);
            }

        }

        return super.hasIgnoreMarker(m);
    }
}
