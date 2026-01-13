package com.nlu.store.core.jdbc.query;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.specification.Specification;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Interface defining the query execution pipeline.
 * <p>
 * This interface follows the Fluent Builder pattern.
 * Methods are divided into Intermediate (Configuration) and Terminal (Execution).
 *
 * @param <T> The type of the entity being queried.
 */
public interface ExecuteQueryStep<T> {

    // ========================================================================
    // 1. RELATIONSHIPS (Intermediate Operations)
    // ========================================================================

    // Using default methods prevents the implementation class from becoming cluttered
    // with simple overload logic.

    default <R> OneRelationStep<T, R> hasOne(String table) {
        return hasOne(table, FetchMode.SELECT, null);
    }

    default <R> OneRelationStep<T, R> hasOne(String table, FetchMode mode) {
        return hasOne(table, mode, null);
    }

    default <R> OneRelationStep<T, R> hasOne(String table, String alias) {
        return hasOne(table, FetchMode.SELECT, alias);
    }

    <R> OneRelationStep<T, R> hasOne(String table, FetchMode mode, String alias);

    default <R> ManyRelationStep<T, R> hasMany(String table) {
        return hasMany(table, FetchMode.SELECT, null);
    }

    default <R> ManyRelationStep<T, R> hasMany(String table, FetchMode mode) {
        return hasMany(table, mode, null);
    }

    default <R> ManyRelationStep<T, R> hasMany(String table, String alias) {
        return hasMany(table, FetchMode.SELECT, alias);
    }

    <R> ManyRelationStep<T, R> hasMany(String table, FetchMode mode, String alias);


    // ========================================================================
    // MANY-TO-MANY RELATIONSHIPS
    // ========================================================================

    /**
     * Many-to-Many cơ bản: Tự động đoán khóa ngoại dựa trên tên bảng.
     * VD: users -> roles (thông qua user_roles)
     */
    default <R> ManyRelationStep<T, R> hasManyToMany(String targetTable, String pivotTable) {
        return hasManyToMany(targetTable, pivotTable, FetchMode.SELECT, null);
    }

    default <R> ManyRelationStep<T, R> hasManyToMany(String targetTable, String pivotTable, String alias) {
        return hasManyToMany(targetTable, pivotTable, FetchMode.SELECT, alias);
    }

    /**
     * Many-to-Many đầy đủ: Cho phép chỉ định bảng đích, bảng trung gian, mode và alias.
     * Logic khóa ngoại sẽ được xử lý mặc định hoặc custom trong Implementation.
     */
    <R> ManyRelationStep<T, R> hasManyToMany(String targetTable, String pivotTable, FetchMode mode, String alias);

    /**
     * Many-to-Many nâng cao: Cho phép chỉ định rõ tên các cột khóa ngoại (Foreign Keys).
     * Dùng khi tên cột trong DB không tuân theo chuẩn (VD: 'u_id' thay vì 'user_id').
     *
     * @param sourceFk: Khóa ngoại trong bảng Pivot trỏ về bảng Gốc (VD: user_id)
     * @param targetFk: Khóa ngoại trong bảng Pivot trỏ về bảng Đích (VD: role_id)
     */
    <R> ManyRelationStep<T, R> hasManyToMany(String targetTable, String pivotTable,
                                             String sourceFk, String targetFk,
                                             FetchMode mode, String alias);
    // ========================================================================
    // 2. FILTERING (Intermediate Operations)
    // ========================================================================

    /**
     * Returns a WhereBuilder to manually construct conditions.
     * Note: This might break the fluent chain unless WhereBuilder returns to ExecuteQueryStep.
     */
    WhereBuilder<T> where();

    /**
     * Functional style filtering.
     * Allows usage like: .where(w -> w.eq("id", 1).like("name", "A%"))
     * This keeps the chain intact returning ExecuteQueryStep.
     */
    default ExecuteQueryStep<T> where(Consumer<WhereBuilder<T>> consumer) {
        consumer.accept(where());
        return this;
    }

    ExecuteQueryStep<T> where(Specification<T> spec);

    ExecuteQueryStep<T> whereRaw(String sql, Object... args);

    // ========================================================================
    // 3. SORTING & PAGING (Intermediate Operations)
    // ========================================================================

    ExecuteQueryStep<T> orderByRaw(String sql);

    ExecuteQueryStep<T> sort(Sort sort);

    ExecuteQueryStep<T> limit(int limit);

    ExecuteQueryStep<T> offset(int offset);

    /**
     * Applies pagination settings (limit/offset) from a Pageable object
     * without executing the query immediately.
     */
    ExecuteQueryStep<T> applyPageable(Pageable pageable);

    // ========================================================================
    // 4. EXECUTION (Terminal Operations)
    // ========================================================================

    /**
     * Executes query and returns a list.
     */
    List<T> list();

    /**
     * Executes query and returns a single optional result.
     * Throws exception if more than one result is found (strict) or returns first (lenient), depending on impl.
     */
    Optional<T> first();

    /**
     * Executes a count query based on current conditions.
     */
    long count();

    /**
     * Executes query and wraps result in a Page object.
     * Usually triggers a count query internally.
     */
    Page<T> paging(Pageable pageable);

    /**
     * Returns a lazy stream of results (remember to close the stream/connection).
     */
    Stream<T> stream();
}
