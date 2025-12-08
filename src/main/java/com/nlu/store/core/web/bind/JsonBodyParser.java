package com.nlu.store.core.web.bind;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JsonBodyParser implements BodyParser {

    private final ObjectMapper objectMapper;

    public JsonBodyParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.startsWith("application/json");
    }

    @Override
    public <T> T parse(HttpServletRequest request, Class<T> targetType) throws IOException {
        return objectMapper.readValue(request.getReader(), targetType);
    }
}
