package com.nlu.store.core.web.bind;

import com.nlu.store.core.data.MultipartFile;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MultipartBodyParserTest {

    private MultipartBodyParser parser;
    private ConversionService conversionService;
    private HttpServletRequest request;

    // --- Dummy DTO for Testing ---
    static class UserProfileForm {
        private String username;
        private Integer age;
        private MultipartFile avatar;

        // Getters for assertion
        public String getUsername() { return username; }
        public Integer getAge() { return age; }
        public MultipartFile getAvatar() { return avatar; }
    }
    // -----------------------------

    @BeforeEach
    void setUp() {
        // 1. Mock Dependencies
        conversionService = mock(ConversionService.class);
        request = mock(HttpServletRequest.class);

        // 2. Initialize Parser
        parser = new MultipartBodyParser(conversionService);
    }

    @Test
    @DisplayName("Should return true for multipart/form-data content type")
    void testSupports() {
        assertTrue(parser.supports("multipart/form-data"));
        assertTrue(parser.supports("multipart/form-data; boundary=---123"));

        assertFalse(parser.supports("application/json"));
        assertFalse(parser.supports("application/x-www-form-urlencoded"));
        assertFalse(parser.supports(null));
    }

    @Test
    @DisplayName("Should bind Text fields and File fields correctly")
    void testParseMixedContent() throws IOException, ServletException {
        // --- 1. Setup Data ---
        // Mock Text Parameters
        when(request.getParameter("username")).thenReturn("john_doe");
        when(request.getParameter("age")).thenReturn("25");

        // Mock Conversion Service behavior
        when(conversionService.convert("john_doe", String.class)).thenReturn("john_doe");
        when(conversionService.convert("25", Integer.class)).thenReturn(25);

        // Mock File Part
        Part mockPart = mock(Part.class);
        when(mockPart.getName()).thenReturn("avatar");
        when(mockPart.getSize()).thenReturn(1024L); // File has content
        when(mockPart.getSubmittedFileName()).thenReturn("profile.png");
        when(mockPart.getContentType()).thenReturn("image/png");

        // Mock Request getting Part
        when(request.getPart("avatar")).thenReturn(mockPart);

        // --- 2. Execute ---
        UserProfileForm result = parser.parse(request, UserProfileForm.class);

        // --- 3. Assert ---
        assertNotNull(result);

        // Check Text Fields
        assertEquals("john_doe", result.getUsername());
        assertEquals(25, result.getAge());

        // Check File Field
        assertNotNull(result.getAvatar());
        assertEquals("profile.png", result.getAvatar().getOriginalFilename());
        assertEquals(1024L, result.getAvatar().getSize());
        assertEquals("image/png", result.getAvatar().getContentType());
    }

    @Test
    @DisplayName("Should ignore empty file parts (size == 0)")
    void testParseEmptyFile() throws IOException, ServletException {
        // Mock empty part
        Part mockPart = mock(Part.class);
        when(mockPart.getSize()).thenReturn(0L); // Empty file

        when(request.getPart("avatar")).thenReturn(mockPart);

        // Execute
        UserProfileForm result = parser.parse(request, UserProfileForm.class);

        // Assert: Avatar should be null because size was 0
        assertNull(result.getAvatar());
    }

    @Test
    @DisplayName("Should handle ServletException when getting Part")
    void testHandleServletException() throws IOException, ServletException {
        // Simulate ServletException (e.g., config error in Tomcat)
        when(request.getPart("avatar")).thenThrow(new ServletException("Multipart config missing"));

        // Expect IllegalArgumentException (wrapped by the catch block in parse method)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(request, UserProfileForm.class);
        });

        assertTrue(exception.getMessage().contains("Failed to bind multipart data"));
        assertTrue(exception.getCause() instanceof IOException); // getPartSafe wraps ServletException in IOException
    }


}
