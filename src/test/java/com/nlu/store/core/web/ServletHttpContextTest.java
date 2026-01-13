package com.nlu.store.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.core.web.bind.BodyParser;
import com.nlu.store.core.web.bind.ConversionService;
import com.nlu.store.core.web.bind.DataBinder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletHttpContextTest {

    // --- Mocks for Servlet API ---
    @Mock private HttpServletRequest req;
    @Mock private HttpServletResponse resp;
    @Mock private HttpSession session;

    // --- Mocks for Infrastructure ---
    @Mock private ViewResolver viewResolver;
    @Mock private ObjectMapper objectMapper;
    @Mock private DataBinder dataBinder;
    @Mock private ConversionService conversionService;
    @Mock private BodyParser bodyParser;

    // --- System Under Test ---
    private ServletHttpContext context;

    @BeforeEach
    void setUp() {
        // 1. Setup Infrastructure behavior
        // Vì WebInfrastructure là Record, ta có thể tạo instance thật với các mock component
        // Tuy nhiên, code của bạn gọi: infrastructure.conversionService()
        // Nếu WebInfrastructure là Record như các bước trước, nó sẽ gọi dataBinder.getConversionService()
        // Ta cần mock hành vi đó:


        WebInfrastructure infrastructure = new WebInfrastructure(viewResolver, objectMapper, dataBinder, conversionService, bodyParser, null);

        // 2. Init Context
        context = new ServletHttpContext(req, resp, infrastructure);
    }

    // ==================================================================
    // 1. TEST PARAMETERS & CONVERSION
    // ==================================================================

    @Test
    @DisplayName("getParam: Should return converted integer value")
    void testGetParam_Integer() {
        // Given
        when(req.getParameter("age")).thenReturn("30");
        when(conversionService.convert("30", Integer.class)).thenReturn(30);

        // When
        Integer result = context.getParam("age", Integer.class, 0, false);

        // Then
        assertEquals(30, result);
    }

    @Test
    @DisplayName("getParam: Should throw BadRequestException if required param is missing")
    void testGetParam_RequiredMissing() {
        when(req.getParameter("id")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> {
            context.getParam("id", Integer.class, null, true);
        });
    }

    @Test
    @DisplayName("getParams: Should return list of converted values")
    void testGetParams_List() {
        // Given ?ids=10&ids=20
        when(req.getParameterValues("ids")).thenReturn(new String[]{"10", "20"});
        when(conversionService.convert("10", Integer.class)).thenReturn(10);
        when(conversionService.convert("20", Integer.class)).thenReturn(20);

        // When
        List<Integer> result = context.getParams("ids", Integer.class);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(10));
        assertTrue(result.contains(20));
    }

    // ==================================================================
    // 2. TEST QUERY BINDING (bindQueryParams)
    // ==================================================================

    @Test
    @DisplayName("bindQueryParams: Should map URL params to DTO object")
    void testBindQueryParams() {
        // Given DTO fields: keyword (String), tags (List)
        when(req.getParameter("keyword")).thenReturn("java");
        when(req.getParameterValues("tags")).thenReturn(new String[]{"coding", "test"});

        // Mock Conversion
        when(conversionService.convert("java", String.class)).thenReturn("java");
        when(conversionService.convert("coding", String.class)).thenReturn("coding");
        when(conversionService.convert("test", String.class)).thenReturn("test");

        // When
        SearchDto dto = context.bindQueryParams(SearchDto.class);

        // Then
        assertNotNull(dto);
        assertEquals("java", dto.keyword);
        assertNotNull(dto.tags);
        assertEquals(2, dto.tags.size());
    }

    // ==================================================================
    // 3. TEST PAGINATION (Pageable)
    // ==================================================================

    @Test
    @DisplayName("getPageable: Should convert 1-based page to 0-based index")
    void testGetPageable_PageCalculation() {
        // Given URL: ?page=1&limit=10 (Trang 1)
        when(req.getParameter("page")).thenReturn("1");
        when(req.getParameter("limit")).thenReturn("10");
        when(req.getParameterValues("sort")).thenReturn(null); // No sort

        when(conversionService.convert("1", Integer.class)).thenReturn(1);
        when(conversionService.convert("10", Integer.class)).thenReturn(10);

        // When
        Pageable pageable = context.getPageable();

        // Then
        // Logic: pageIndex = (page > 0) ? page - 1 : 0;
        // Input 1 -> Output 0
        assertEquals(0, pageable.getPage());
        assertEquals(10, pageable.getLimit());
    }

    @Test
    @DisplayName("getPageable: Should parse complex sort string")
    void testGetPageable_SortParsing() {
        // Given URL: ?sort=price,desc
        when(req.getParameter("page")).thenReturn(null); // default 1
        when(req.getParameter("limit")).thenReturn(null); // default 10
        when(req.getParameterValues("sort")).thenReturn(new String[]{"price,desc"});

        // When
        Pageable pageable = context.getPageable();

        // Then
        Sort sort = pageable.getSort();
        assertFalse(sort.isUnsorted());

        sort.forEach(order -> {
            assertEquals("price", order.getProperty());
            assertEquals(Sort.Direction.DESC, order.getDirection());
        });
    }

    // ==================================================================
    // 4. TEST BODY & VALIDATION
    // ==================================================================

    @Test
    @DisplayName("getBody: Should return BindingResult with data and errors")
    void testGetBody_Validation() throws IOException {
        // Given
        UserDto mockDto = new UserDto("bad-email");

        // 1. Mock Body Parsing
        when(bodyParser.parse(eq(req), eq(UserDto.class))).thenReturn(mockDto);

        // 2. Mock Validation Result
        Validator<UserDto> validator = mock(Validator.class);
        ValidateResult validateResult = mock(ValidateResult.class);

        when(validateResult.hasError()).thenReturn(true);
        when(validateResult.details()).thenReturn(Map.of("email", "Invalid format"));

        when(validator.validate(mockDto)).thenReturn(validateResult);

        // When
        BindingResult<UserDto> result = context.getBody(UserDto.class, validator);

        // Then
        assertNotNull(result);
        assertTrue(result.hasError());
        assertEquals("bad-email", result.data().email);
        assertEquals("Invalid format", result.details().get("email"));
    }

    // ==================================================================
    // 5. TEST MULTIPART
    // ==================================================================

    @Test
    @DisplayName("getMultipartFile: Should return file when request is multipart")
    void testGetMultipartFile() throws Exception {
        // Given
        when(req.getContentType()).thenReturn("multipart/form-data");
        Part mockPart = mock(Part.class);
        when(mockPart.getSize()).thenReturn(500L);
        when(req.getPart("avatar")).thenReturn(mockPart);

        // When
        var file = context.getMultipartFile("avatar");

        // Then
        assertNotNull(file);
        assertEquals(500L, file.getSize());
    }

    // ==================================================================
    // 6. TEST RESPONSE (JSON)
    // ==================================================================

    @Test
    @DisplayName("sendRestResponse: Should write JSON to response writer")
    void testSendRestResponse() throws Exception {
        // Given
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);

        Map<String, String> data = Map.of("status", "success");

        // When
        context.sendRestResponse(data);

        // Then
        verify(resp).setContentType("application/json");
        verify(objectMapper).writeValue(any(PrintWriter.class), eq(data));
    }

    // ==================================================================
    // 7. TEST FLASH ATTRIBUTES
    // ==================================================================

    // Lưu ý: Flash.put là static method.
    // Nếu Flash sử dụng req.getSession() bên trong, ta có thể verify session.
    // Nếu không dùng mockito-inline, ta chỉ có thể verify context gọi hàm mà không crash.


    // ==================================================================
    // HELPER DTOs
    // ==================================================================

    public static class SearchDto {
        private String keyword;
        private List<String> tags;
    }

    public static class UserDto {
        public String email;
        public UserDto(String email) { this.email = email; }
    }
}
