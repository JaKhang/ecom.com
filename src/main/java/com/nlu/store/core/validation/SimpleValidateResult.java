package com.nlu.store.core.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleValidateResult implements ValidateResult {
    private final Map<String, String> errors;
    private final Class<?> validatedClass;

    public SimpleValidateResult(Map<String, String> errors, Class<?> validatedClass) {
        this.errors = errors;
        this.validatedClass = validatedClass;
    }

    public SimpleValidateResult(Class<?> validatedClass) {
        this(new HashMap<>(), validatedClass);
    }

    public void addError(String field, String messageCode) {
        this.errors.put(field, messageCode);
    }


    @Override
    public boolean hasError() {
        return !errors.isEmpty();
    }

    @Override
    public Map<String, String> details() {
        return Collections.unmodifiableMap(errors);
    }

    @Override
    public Class<?> getValidatedClass() {
        return validatedClass;
    }


    public ValidateResult merge(ValidateResult other) {
        if (other == null || !other.hasError()) {
            return this;
        }
        if (!this.hasError()) {
            return other;
        }
        // Cả 2 đều có lỗi -> Gộp Map
        Map<String, String> combined = new HashMap<>(this.errors);
        combined.putAll(other.details());
        return new SimpleValidateResult(combined, this.validatedClass);
    }


}
