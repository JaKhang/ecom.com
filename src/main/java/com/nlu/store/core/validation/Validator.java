package com.nlu.store.core.validation;

import java.util.Objects;

/**
 * Functional interface for validating objects of type T.
 * <p>
 * Implements the <strong>Combinator Pattern</strong>, allowing multiple validation rules
 * to be chained together using {@link #and(Validator)}.
 * </p>
 *
 * @param <T> the type of the object to validate.
 * @author Ja Khang
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Validates the given input.
     *
     * @param t the object to validate.
     * @return a {@link ValidateResult} containing success status and potential error messages.
     */
    ValidateResult validate(T t);

    /**
     * Combines this validator with another validator.
     * <p>
     * <strong>Strategy: Error Accumulation</strong><br>
     * Unlike short-circuiting logic (&&), this method executes <strong>both</strong> validators
     * and merges their results. This is ideal for form validation where users need to see
     * all errors at once.
     * </p>
     *
     * @param other the other validator to chain.
     * @return a new Validator that performs both checks.
     */
    default Validator<T> and(Validator<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> {
            ValidateResult result1 = this.validate(t);
            ValidateResult result2 = other.validate(t);

            // Merge results to accumulate all errors
            return result1.merge(result2);
        };
    }
}
