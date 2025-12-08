package com.nlu.store.core.web.bind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultConversionServiceTest {

    private DefaultConversionService service;

    // Dummy Enum for testing
    enum UserRole { ADMIN, USER, GUEST }

    @BeforeEach
    void setUp() {
        service = new DefaultConversionService();
    }

    @Test
    @DisplayName("Should convert Primitives correctly")
    void testConvertPrimitives() {
        assertEquals(123, service.convert("123", int.class));
        assertEquals(100L, service.convert("100", long.class));
        assertEquals(99.9, service.convert("99.9", double.class));

        // Boolean variations
        assertTrue(service.convert("true", boolean.class));
        assertTrue(service.convert("TRUE", boolean.class));
        assertTrue(service.convert("1", boolean.class));
        assertTrue(service.convert("on", boolean.class));
        assertFalse(service.convert("false", boolean.class));
        assertFalse(service.convert("anything_else", boolean.class));
    }

    @Test
    @DisplayName("Should convert Wrapper classes correctly")
    void testConvertWrappers() {
        assertEquals(Integer.valueOf(123), service.convert("123", Integer.class));
        assertEquals(Long.valueOf(100L), service.convert("100", Long.class));
        assertEquals(Double.valueOf(99.9), service.convert("99.9", Double.class));
        assertEquals(Boolean.TRUE, service.convert("true", Boolean.class));
    }

    @Test
    @DisplayName("Should handle Null or Empty input safely")
    void testNullAndEmptyHandling() {
        // Primitives should return default values (0, false)
        assertEquals(0, service.convert(null, int.class));
        assertEquals(0, service.convert("", int.class));
        assertEquals(0.0, service.convert(null, double.class));
        assertFalse(service.convert(null, boolean.class));

        // Wrappers/Objects should return null
        assertNull(service.convert(null, Integer.class));
        assertNull(service.convert("", String.class));
        assertNull(service.convert("   ", LocalDate.class));
    }

    @Test
    @DisplayName("Should convert Enums dynamically without registration")
    void testEnumConversion() {
        assertEquals(UserRole.ADMIN, service.convert("ADMIN", UserRole.class));
        assertEquals(UserRole.GUEST, service.convert("GUEST", UserRole.class));
    }

    @Test
    @DisplayName("Should convert Date Time (ISO-8601)")
    void testDateConversion() {
        LocalDate date = service.convert("2023-12-25", LocalDate.class);
        assertEquals(LocalDate.of(2023, 12, 25), date);

        LocalDateTime dateTime = service.convert("2023-12-25T10:30:00", LocalDateTime.class);
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 0), dateTime);
    }

    @Test
    @DisplayName("Should allow registering Custom Converters")
    void testCustomConverter() {
        // Register a converter for StringBuilder
        service.addConverter(StringBuilder.class, source -> new StringBuilder(source).reverse());

        StringBuilder result = service.convert("abc", StringBuilder.class);

        assertNotNull(result);
        assertEquals("cba", result.toString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException on invalid format")
    void testErrorHandling() {
        // Test invalid number
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.convert("not-a-number", int.class);
        });

        assertTrue(exception.getMessage().contains("Failed to convert"));

        // Test invalid Enum
        assertThrows(IllegalArgumentException.class, () -> {
            service.convert("INVALID_ROLE", UserRole.class);
        });
    }
}
