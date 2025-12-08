package com.nlu.store.core.web.bind;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

/**
 * A composite parser that delegates to a list of specific parsers.
 * <p>
 * It iterates through the registered parsers and uses the first one
 * that supports the request's Content-Type.
 */
public class CompositeBodyParser implements BodyParser {

    private final List<BodyParser> parsers;

    public CompositeBodyParser(List<BodyParser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public boolean supports(String contentType) {
        for (BodyParser parser : parsers) {
            if (parser.supports(contentType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T parse(HttpServletRequest request, Class<T> targetType) throws IOException {
        String contentType = request.getContentType();

        for (BodyParser parser : parsers) {
            if (parser.supports(contentType)) {
                return parser.parse(request, targetType);
            }
        }

        throw new IllegalArgumentException("Unsupported Content-Type: " + contentType);
    }
}
