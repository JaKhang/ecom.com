package com.nlu.store.core.web.bind;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositeBodyParserTest {

    private CompositeBodyParser compositeParser;
    private BodyParser jsonParser;
    private BodyParser formParser;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Mock các parser con
        jsonParser = mock(BodyParser.class);
        formParser = mock(BodyParser.class);
        request = mock(HttpServletRequest.class);

        // Cấu hình behavior cho supports()
        when(jsonParser.supports(startsWith("application/json"))).thenReturn(true);
        when(formParser.supports(startsWith("application/x-www-form-urlencoded"))).thenReturn(true);

        // Khởi tạo Composite
        compositeParser = new CompositeBodyParser(Arrays.asList(jsonParser, formParser));
    }

    @Test
    @DisplayName("Should delegate to JSON parser when Content-Type is application/json")
    void testDelegateToJson() throws IOException {
        // Setup
        String contentType = "application/json";
        when(request.getContentType()).thenReturn(contentType);

        // Giả lập jsonParser trả về kết quả
        Object expectedResult = new Object();
        when(jsonParser.parse(request, Object.class)).thenReturn(expectedResult);

        // Execute
        Object result = compositeParser.parse(request, Object.class);

        // Assert
        assertSame(expectedResult, result);
        verify(jsonParser).parse(request, Object.class); // Verify JSON parser was called
        verify(formParser, never()).parse(any(), any()); // Verify Form parser was NOT called
    }

    @Test
    @DisplayName("Should delegate to Form parser when Content-Type is form-urlencoded")
    void testDelegateToForm() throws IOException {
        // Setup
        String contentType = "application/x-www-form-urlencoded";
        when(request.getContentType()).thenReturn(contentType);

        Object expectedResult = new Object();
        when(formParser.parse(request, Object.class)).thenReturn(expectedResult);

        // Execute
        Object result = compositeParser.parse(request, Object.class);

        // Assert
        assertSame(expectedResult, result);
        verify(formParser).parse(request, Object.class);
        verify(jsonParser, never()).parse(any(), any());
    }

    @Test
    @DisplayName("Should throw exception if Content-Type is unsupported")
    void testUnsupportedContentType() {
        // Setup
        String contentType = "image/png"; // Không parser nào support
        when(request.getContentType()).thenReturn(contentType);

        // Execute & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            compositeParser.parse(request, Object.class);
        });

        assertTrue(ex.getMessage().contains("Unsupported Content-Type"));
    }
}
