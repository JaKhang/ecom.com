package com.nlu.store.core.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultPathExtractorTest {

    private DefaultPathExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new DefaultPathExtractor();
    }

    // --- 1. Basic Variable Extraction ---

    @Test
    @DisplayName("Should extract simple variable from path")
    void shouldExtractSimpleVariable() {
        String template = "/users/{id}";
        String path = "/users/100";

        String result = extractor.extractPathValue(template, "id", path);

        assertEquals("100", result);
    }

    @Test
    @DisplayName("Should extract variable from the middle of the path")
    void shouldExtractMiddleVariable() {
        String template = "/shop/{category}/items";
        String path = "/shop/electronics/items";

        String result = extractor.extractPathValue(template, "category", path);

        assertEquals("electronics", result);
    }

    @Test
    @DisplayName("Should extract multiple variables correctly")
    void shouldExtractMultipleVariables() {
        String template = "/api/{version}/users/{userId}";
        String path = "/api/v2/users/999";

        assertEquals("v2", extractor.extractPathValue(template, "version", path));
        assertEquals("999", extractor.extractPathValue(template, "userId", path));
    }

    // --- 2. Wildcard (*) Support ---

    @Test
    @DisplayName("Should ignore segments matched by wildcard (*)")
    void shouldHandleWildcard() {
        // Template: /files/*/{filename} -> means the segment after 'files' can be anything
        String template = "/files/*/{filename}";

        // Case A: Subfolder 'images'
        assertEquals("logo.png", extractor.extractPathValue(template, "filename", "/files/images/logo.png"));

        // Case B: Subfolder 'docs'
        assertEquals("report.pdf", extractor.extractPathValue(template, "filename", "/files/docs/report.pdf"));
    }

    // --- 3. OR Operator (|) Support ---

    // --- 3. OR Operator (|) Support (No ParameterizedTest needed) ---

    @Test
    @DisplayName("Should handle OR operator in path segments")
    void shouldHandleOrOperator() {
        String template = "/admin|danh-muc/{page}";

        // Case 1: /admin (Should match)
        String result1 = extractor.extractPathValue(template, "page", "/admin/dashboard");
        assertEquals("dashboard", result1, "Should match 'admin'");

        // Case 2: /root (Should match)
        String result2 = extractor.extractPathValue(template, "page", "/danh-muc/dashboard");
        assertEquals("dashboard", result2, "Should match 'root'");

        // Case 3: /user (Should NOT match)
        String result3 = extractor.extractPathValue(template, "page", "/user/dashboard");
        assertNull(result3, "Should not match 'user'");
    }


    // --- 4. Edge Cases & Errors ---

    @Test
    @DisplayName("Should return null if path does not match template structure")
    void shouldReturnNullForMismatch() {
        String template = "/users/{id}";
        String path = "/products/100"; // Different prefix

        assertNull(extractor.extractPathValue(template, "id", path));
    }

    @Test
    @DisplayName("Should return null if path is shorter than template")
    void shouldReturnNullForShortPath() {
        String template = "/users/{id}/details";
        String path = "/users/100";

        assertNull(extractor.extractPathValue(template, "id", path));
    }

    @Test
    @DisplayName("Should return null if variable name requested does not exist in template")
    void shouldReturnNullForUnknownVariable() {
        String template = "/users/{id}";
        String path = "/users/100";

        // Template has 'id', but we ask for 'name'
        assertNull(extractor.extractPathValue(template, "name", path));
    }

    @Test
    @DisplayName("Should handle null path input safely")
    void shouldHandleNullPath() {
        String template = "/";
        // Implementation sets path to "/" if null
        String result = extractor.extractPathValue(template, "any", null);

        // Should not throw exception, just return null as no variable matches
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle complex combination of Wildcard, OR and Variables")
    void shouldHandleComplexCombination() {
        // Either 'v1' or 'v2', followed by any segment, followed by resource ID
        String template = "/api/v1|v2/*/{resourceId}";

        String path1 = "/api/v1/users/10";
        String path2 = "/api/v2/products/50";
        String path3 = "/api/v3/fails/00"; // v3 not allowed

        assertEquals("10", extractor.extractPathValue(template, "resourceId", path1));
        assertEquals("50", extractor.extractPathValue(template, "resourceId", path2));
        assertNull(extractor.extractPathValue(template, "resourceId", path3));
    }
}
