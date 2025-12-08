package com.nlu.store.core.web.bind;

import com.nlu.store.core.data.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.Set;

// DTO cho Form Data




class FormBodyParserTest {

    private FormBodyParser parser;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Setup dependencies thực tế
        ConversionService conversionService = new DefaultConversionService();
        parser = new FormBodyParser(conversionService);

        // Mock Request
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("Should support correct Content-Type")
    void testSupports() {
        assertTrue(parser.supports("application/x-www-form-urlencoded"));
        assertTrue(parser.supports(null)); // Default
        assertFalse(parser.supports("application/json"));
    }

    @Test
    @DisplayName("Should bind Single Values and use Setter if available")
    void testBindSingleValues() {
        // Mock inputs
        when(request.getParameter("username")).thenReturn("john");
        when(request.getParameter("age")).thenReturn("25");

        // Execute
        SimpleForm result = parser.parse(request, SimpleForm.class);

        // Assert
        // Kiểm tra logic "SETTER_" trong SimpleForm để chắc chắn ReflectionUtils đã gọi Setter
        assertEquals("SETTER_john", result.getUsername());
        assertEquals(25, result.getAge());
    }

    @Test
    @DisplayName("Should bind Collections (List & Set)")
    void testBindCollections() {
        // Mock inputs for List and Set
        when(request.getParameterValues("ids")).thenReturn(new String[]{"10", "20"});
        when(request.getParameterValues("roles")).thenReturn(new String[]{"ADMIN", "USER"});

        SimpleForm result = parser.parse(request, SimpleForm.class);

        // Assert List
        assertNotNull(result.getIds());
        assertEquals(2, result.getIds().size());
        assertTrue(result.getIds().containsAll(List.of(10, 20)));

        // Assert Set
        assertNotNull(result.getRoles());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains("ADMIN"));
    }

    @Test
    @DisplayName("Should bind Arrays")
    void testBindArrays() {
        when(request.getParameterValues("tags")).thenReturn(new String[]{"hot", "new"});

        SimpleForm result = parser.parse(request, SimpleForm.class);

        assertNotNull(result.getTags());
        assertEquals(2, result.getTags().length);
        assertEquals("hot", result.getTags()[0]);
    }
    @Test
    @DisplayName("Should bind List of Enums correctly")
    void testBindListEnum() {


        // Mock Request: ?roles=ADMIN&roles=GUEST
        when(request.getParameterValues("roles")).thenReturn(new String[]{"ADMIN", "GUEST"});

        // Execute
        UserForm result = parser.parse(request, UserForm.class);

        // Assert
        assertNotNull(result.getRoles());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(Role.ADMIN));
        assertTrue(result.getRoles().contains(Role.GUEST));

        // Kiểm tra đúng kiểu dữ liệu
        assertInstanceOf(Role.class, result.getRoles().get(0));
    }

}
