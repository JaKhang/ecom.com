package com.nlu.store.core.web;

/**
 * Strategy interface for extracting dynamic values from URI paths.
 * <p>
 * This component is responsible for parsing a URI template (containing placeholders)
 * against an actual request URI to extract specific variable values.
 * </p>
 * <p>
 * It is typically used by the {@link HttpContext} to resolve path variables
 * (e.g., extracting "123" from "/users/123" given the template "/users/{id}").
 * </p>
 *
 * @author Ja Khang
 */
public interface PathExtractor {

    /**
     * Extracts a single variable value from the actual path based on the template.
     *
     * @param template  the URI template pattern containing variables (e.g., "/products/{sku}/details").
     *             <i>Note: 'tmp' stands for 'template'.</i>
     * @param name the name of the variable to extract (e.g., "sku").
     * @param path the actual request URI to parse (e.g., "/products/IPHONE-15/details").
     * @return the extracted string value (e.g., "IPHONE-15"), or {@code null} if not found/mismatched.
     */
    String extractPathValue(String template, String name, String path);
}
