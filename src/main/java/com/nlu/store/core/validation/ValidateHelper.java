package com.nlu.store.core.validation;

import com.nlu.store.core.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ValidateHelper<T> {

    private final T target;
    private final SimpleValidateResult result;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");

    private ValidateHelper(T target) {
        this.target = target;
        this.result = new SimpleValidateResult(target != null ? target.getClass() : Object.class);
    }

    public static <T> ValidateHelper<T> of(T target) {
        return new ValidateHelper<>(target);
    }

    /**
     * Helper: Dùng Reflection để lấy giá trị của field từ target object.
     * Hỗ trợ: Getter chuẩn (getEmail), Accessor kiểu Record (email), hoặc Field trực tiếp.
     */
    // --- THAY ĐỔI Ở ĐÂY ---
    private Object getFieldValue(String fieldName) {
        // Ủy quyền hoàn toàn cho ReflectionUtils đã tối ưu
        return ReflectionUtils.get(target, fieldName);
    }

    /**
     * Core Logic
     */
    private ValidateHelper<T> validate(String field, boolean isValid, String message) {
        if (result.details().containsKey(field)) {
            return this;
        }
        if (!isValid) {
            result.addError(field, message);
        }
        return this;
    }

    /* ==================================================================
       1. Common Null / Empty Checks
       ================================================================== */

    public ValidateHelper<T> isNull(String field, String message) {
        Object val = getFieldValue(field);
        return validate(field, val == null, message);
    }

    public ValidateHelper<T> notNull(String field, String message) {
        Object val = getFieldValue(field);
        return validate(field, val != null, message);
    }

    // --- String Checks ---

    public ValidateHelper<T> notBlank(String field, String message) {
        Object val = getFieldValue(field);
        // Ép kiểu an toàn: nếu không phải String thì coi như valid (hoặc fail tùy logic), ở đây check null & empty
        String s = (val instanceof String) ? (String) val : null;
        return validate(field, s != null && !s.trim().isEmpty(), message);
    }

    public ValidateHelper<T> notEmpty(String field, String message) {
        Object val = getFieldValue(field);
        if (val instanceof Collection) {
            return validate(field, val != null && !((Collection<?>) val).isEmpty(), message);
        }
        if (val instanceof Map) {
            return validate(field, val != null && !((Map<?, ?>) val).isEmpty(), message);
        }
        if (val instanceof Object[]) {
            return validate(field, val != null && ((Object[]) val).length > 0, message);
        }
        String s = (val instanceof String) ? (String) val : null;
        return validate(field, s != null && !s.isEmpty(), message);
    }

    /* ==================================================================
       2. String Length & Regex
       ================================================================== */

    public ValidateHelper<T> maxLength(String field, int max, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof String)) return this; // Ignore non-string
        String s = (String) val;
        return validate(field, s.length() <= max, message);
    }

    public ValidateHelper<T> minLength(String field, int min, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof String)) return this;
        String s = (String) val;
        return validate(field, s.length() >= min, message);
    }

    public ValidateHelper<T> isEmail(String field, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof String)) return this;
        return validate(field, EMAIL_PATTERN.matcher((String) val).matches(), message);
    }

    public ValidateHelper<T> matchesRegex(String field, String regex, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof String)) return this;
        return validate(field, ((String) val).matches(regex), message);
    }

    /* ==================================================================
       3. Number Checks
       ================================================================== */

    public ValidateHelper<T> isInRange(String field, int min, int max, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof Number)) return this; // Skip if null or not number
        int intVal = ((Number) val).intValue();
        return validate(field, intVal >= min && intVal <= max, message);
    }

    public ValidateHelper<T> isPositive(String field, String message) {
        Object val = getFieldValue(field);
        if (!(val instanceof Number)) return this;
        return validate(field, ((Number) val).doubleValue() > 0, message);
    }

    /* ==================================================================
       4. Custom Predicate (Nhận vào Object value đã được lấy tự động)
       ================================================================== */

    /**
     * Check tùy chỉnh.
     * @param predicate: Lambda nhận vào giá trị của field (Object) và trả về boolean.
     */
    public ValidateHelper<T> check(String field, Predicate<Object> predicate, String message) {
        Object val = getFieldValue(field);
        return validate(field, predicate.test(val), message);
    }

    public SimpleValidateResult validate() {
        return result;
    }
}


