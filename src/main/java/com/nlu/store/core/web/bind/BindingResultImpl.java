package com.nlu.store.core.web.bind;

import java.util.Collections;
import java.util.Map;

public record BindingResultImpl<T>(T data, boolean hasError,
                                   Map<String, String> details) implements BindingResult<T> {
    public BindingResultImpl(T data, boolean hasError, Map<String, String> details) {
        this.data = data;
        this.hasError = hasError;
        this.details = details == null ? Collections.emptyMap() : details;
    }

    @Override
    public String toString() {
        return "ExtractedDataResultImpl{" +
                "body=" + data +
                ", hasError=" + hasError +
                ", details=" + details +
                '}';
    }
}



