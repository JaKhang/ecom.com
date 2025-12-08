package com.nlu.store.core.web.bind;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JsonBodyParserTest {

    private JsonBodyParser parser;
    private HttpServletRequest request;

    // DTO giả để test binding
    static class UserDto {
        public String name;
        public int age;
        public boolean active;
    }

    @BeforeEach
    void setUp() {
        // Dùng ObjectMapper thật để test integration với Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        parser = new JsonBodyParser(objectMapper);

        // Mock Request
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("Should support application/json content type")
    void testSupports() {
        assertTrue(parser.supports("application/json"));
        assertTrue(parser.supports("application/json; charset=utf-8"));

        assertFalse(parser.supports("text/html"));
        assertFalse(parser.supports(null));
        assertFalse(parser.supports("application/x-www-form-urlencoded"));
    }

    @Test
    @DisplayName("Should parse valid JSON into Object")
    void testParseSuccess() throws IOException {
        // 1. Chuẩn bị JSON payload
        String jsonPayload = "{\"name\": \"John Doe\", \"age\": 30, \"active\": true}";

        // 2. Mock request.getReader() để trả về JSON trên
        // BufferedReader wrap lấy StringReader chứa payload
        BufferedReader reader = new BufferedReader(new StringReader(jsonPayload));
        when(request.getReader()).thenReturn(reader);

        // 3. Execute
        UserDto result = parser.parse(request, UserDto.class);

        // 4. Assert
        assertNotNull(result);
        assertEquals("John Doe", result.name);
        assertEquals(30, result.age);
        assertTrue(result.active);
    }

    @Test
    @DisplayName("Should throw IOException on malformed JSON")
    void testParseMalformedJson() throws IOException {
        // JSON lỗi (thiếu dấu đóng ngoặc)
        String brokenJson = "{\"name\": \"John\", \"age\": ";

        BufferedReader reader = new BufferedReader(new StringReader(brokenJson));
        when(request.getReader()).thenReturn(reader);

        // Mong đợi ném ra IOException (hoặc JsonProcessingException con của nó)
        assertThrows(IOException.class, () -> {
            parser.parse(request, UserDto.class);
        });
    }

    @Test
    @DisplayName("Should throw IOException when Reader fails")
    void testReaderFailure() throws IOException {
        // Giả lập lỗi IO từ phía Servlet Container
        when(request.getReader()).thenThrow(new IOException("Stream closed"));

        assertThrows(IOException.class, () -> {
            parser.parse(request, UserDto.class);
        });
    }
}
