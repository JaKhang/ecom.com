package com.nlu.store.core.web.bind;

import com.nlu.store.core.data.MultipartFile;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MultipartBodyParserCollectionTest {

    private MultipartBodyParser parser;
    private ConversionService conversionService;
    private HttpServletRequest request;

    // DTO chứa List
    static class ProductForm {
        private String name;
        private List<String> categories;      // Text Collection
        private List<MultipartFile> images;   // File Collection

        public String getName() { return name; }
        public List<String> getCategories() { return categories; }
        public List<MultipartFile> getImages() { return images; }
    }

    @BeforeEach
    void setUp() {
        conversionService = mock(ConversionService.class);
        request = mock(HttpServletRequest.class);
        parser = new MultipartBodyParser(conversionService);
    }

    @Test
    @DisplayName("Should bind List<MultipartFile> and List<String>")
    void testBindCollections() throws IOException, ServletException {
        // --- 1. Setup Text Data ---
        when(request.getParameter("name")).thenReturn("iPhone 15");
        when(conversionService.convert("iPhone 15", String.class)).thenReturn("iPhone 15");

        // Setup Text List (categories)
        when(request.getParameterValues("categories")).thenReturn(new String[]{"Mobile", "Apple"});
        when(conversionService.convert("Mobile", String.class)).thenReturn("Mobile");
        when(conversionService.convert("Apple", String.class)).thenReturn("Apple");

        // --- 2. Setup File List (images) ---
        // Tạo 2 part giả có cùng tên "images"
        Part part1 = mock(Part.class);
        when(part1.getName()).thenReturn("images");
        when(part1.getSize()).thenReturn(100L);
        when(part1.getSubmittedFileName()).thenReturn("front.png");

        Part part2 = mock(Part.class);
        when(part2.getName()).thenReturn("images");
        when(part2.getSize()).thenReturn(200L);
        when(part2.getSubmittedFileName()).thenReturn("back.png");

        // Part khác tên (không được bind vào images)
        Part partOther = mock(Part.class);
        when(partOther.getName()).thenReturn("avatar");

        // Mock request.getParts() trả về danh sách
        when(request.getParts()).thenReturn(Arrays.asList(part1, part2, partOther));

        // --- 3. Execute ---
        ProductForm result = parser.parse(request, ProductForm.class);

        // --- 4. Assert ---
        // Check Name
        assertEquals("iPhone 15", result.getName());

        // Check Text List
        assertNotNull(result.getCategories());
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("Mobile"));

        // Check File List
        assertNotNull(result.getImages());
        assertEquals(2, result.getImages().size());
        assertEquals("front.png", result.getImages().get(0).getOriginalFilename());
        assertEquals("back.png", result.getImages().get(1).getOriginalFilename());
    }
}
