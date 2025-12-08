package com.nlu.store.core.web.bind;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BodyParser {

    /**
     * Checks if this parser supports the given media type.
     *
     * @param contentType The Content-Type header from the request.
     * @return true if supported.
     */
    boolean supports(String contentType); // Renamed from 'canParse'

    /**
     * Parses the request body into the target type.
     *
     * @param request    The HTTP request.
     * @param targetType The class of the target object.
     * @return The parsed object.
     */
    <T> T parse(HttpServletRequest request, Class<T> targetType) throws IOException;
}
