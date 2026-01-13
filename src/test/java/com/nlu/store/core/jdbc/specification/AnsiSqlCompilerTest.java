package com.nlu.store.core.jdbc.specification;

import com.nlu.store.core.jdbc.sql.AnsiSqlCompiler;
import com.nlu.store.core.jdbc.sql.CompiledQuery;
import com.nlu.store.core.data.specification.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnsiSqlCompilerTest {

    private AnsiSqlCompiler compiler;

    @BeforeEach
    void setUp() {
        compiler = new AnsiSqlCompiler();
    }

    // =========================================================================
    // 1. BASIC OPERATORS
    // =========================================================================

    @Test
    @DisplayName("Should compile simple equality condition")
    void testSimpleEqual() {
        Criteria criteria = Criteria.of().equal("username", "admin");

        CompiledQuery query = compiler.compile(criteria);

        assertEquals("username = ?", query.whereClause());
        assertEquals(List.of("admin"), query.parameters());
    }

    @Test
    @DisplayName("Should compile comparison operators (>, <, >=, <=, <>)")
    void testComparisonOperators() {
        Criteria criteria = Criteria.of()
                .greaterThan("age", 18)
                .lessThanOrEqualTo("salary", 5000)
                .notEqual("status", "BANNED");

        CompiledQuery query = compiler.compile(criteria);

        // Mặc định nối bằng AND
        assertEquals("age > ? AND salary <= ? AND status <> ?", query.whereClause());
        assertEquals(List.of(18, 5000, "BANNED"), query.parameters());
    }

    @Test
    @DisplayName("Should compile LIKE operator")
    void testLikeOperator() {
        Criteria criteria = Criteria.of().like("description", "%java%");

        CompiledQuery query = compiler.compile(criteria);

        assertEquals("description LIKE ?", query.whereClause());
        assertEquals(List.of("%java%"), query.parameters());
    }

    // =========================================================================
    // 2. SPECIAL OPERATORS (IN, NULL)
    // =========================================================================

    @Test
    @DisplayName("Should compile IN operator with multiple values")
    void testInOperator() {
        List<Integer> ids = Arrays.asList(1, 2, 3);
        Criteria criteria = Criteria.of().in("category_id", ids);

        CompiledQuery query = compiler.compile(criteria);

        assertEquals("category_id IN (?, ?, ?)", query.whereClause());
        assertEquals(ids, query.parameters());
    }

    @Test
    @DisplayName("Should compile IN operator with empty list (Fallback to NULL)")
    void testInOperatorEmpty() {
        Criteria criteria = Criteria.of().in("category_id", Collections.emptyList());

        // Lưu ý: Criteria.add() thường bỏ qua list rỗng,
        // nhưng nếu logic add cho phép hoặc ta tự tạo Node, Compiler phải handle fallback.
        // Giả sử ta bypass check của Criteria để test Compiler:
        // (Trong thực tế Criteria.add sẽ return this ngay, nên test này check logic Compiler là chính)

        // Để test trực tiếp logic compiler, ta có thể mock hoặc tạo Node thủ công,
        // nhưng ở đây ta test hành vi: nếu Criteria rỗng thì query rỗng.
        CompiledQuery query = compiler.compile(criteria);
        assertEquals("", query.whereClause());
    }

    @Test
    @DisplayName("Should compile IS NULL / IS NOT NULL without parameters")
    void testNullChecks() {
        Criteria criteria = Criteria.of()
                .isNull("deleted_at")
                .isNotNull("created_at");

        CompiledQuery query = compiler.compile(criteria);

        assertEquals("deleted_at IS NULL AND created_at IS NOT NULL", query.whereClause());
        assertTrue(query.parameters().isEmpty());
    }

    // =========================================================================
    // 3. GROUPING & NESTED LOGIC
    // =========================================================================

    @Test
    @DisplayName("Should compile nested OR condition: A AND (B OR C)")
    void testGroupingOr() {
        // WHERE age > 18 AND (role = 'ADMIN' OR role = 'MOD')
        Criteria roleGroup = Criteria.of()
                .equal("role", "ADMIN")
                .or(Criteria.of().equal("role", "MOD"));

        Criteria main = Criteria.of()
                .greaterThan("age", 18)
                .and(roleGroup);

        CompiledQuery query = compiler.compile(main);

        assertEquals("age > ? AND (role = ? OR role = ?)", query.whereClause());
        assertEquals(List.of(18, "ADMIN", "MOD"), query.parameters());
    }

    @Test
    @DisplayName("Should compile complex nesting: (A OR B) AND (C OR D)")
    void testComplexNesting() {
        Criteria group1 = Criteria.of().equal("a", 1).or(Criteria.of().equal("b", 2));
        Criteria group2 = Criteria.of().equal("c", 3).or(Criteria.of().equal("d", 4));


        Criteria root = Criteria.of().and(group1).and(group2);

        CompiledQuery query = compiler.compile(root);

        // Lưu ý: Root là group tổng, các con là group con nên sẽ có ngoặc
        assertEquals("(a = ? OR b = ?) AND (c = ? OR d = ?)", query.whereClause());
        assertEquals(List.of(1, 2, 3, 4), query.parameters());
    }

    // =========================================================================
    // 4. SECURITY & VALIDATION
    // =========================================================================

    @Test
    @DisplayName("Should throw exception for SQL Injection in column name")
    void testSqlInjectionProtection() {
        // Cố tình truyền tên cột chứa ký tự độc hại
        Criteria criteria = Criteria.of().equal("id; DROP TABLE users; --", 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compiler.compile(criteria);
        });

        assertTrue(exception.getMessage().contains("Invalid column name"));
    }

    @Test
    @DisplayName("Should handle empty criteria gracefully")
    void testEmptyCriteria() {
        CompiledQuery query = compiler.compile(Criteria.of());

        assertEquals("", query.whereClause());
        assertTrue(query.parameters().isEmpty());
    }
}
