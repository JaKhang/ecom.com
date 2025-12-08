package com.nlu.store.core.validation;

import java.util.Objects;

@FunctionalInterface
public interface Validator<T> {

    /**
     * Phương thức trừu tượng duy nhất.
     * Thực hiện validate logic.
     */
    ValidateResult validate(T t);

    /**
     * Kết hợp validator hiện tại với một validator khác.
     * Short-circuit: Cả 2 phải đúng. Nếu cái đầu sai, vẫn chạy cái sau để gom lỗi (hoặc tùy logic).
     * Ở đây tôi chọn phương án: Gom tất cả lỗi của cả 2.
     */
    default Validator<T> and(Validator<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> {
            ValidateResult result1 = this.validate(t);
            ValidateResult result2 = other.validate(t);

            // Gộp kết quả của cả 2
            return result1.merge(result2);
        };
    }


}
