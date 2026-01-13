package com.nlu.store.core.validation;

import java.util.Map;

/**
 * Represents the outcome of a validation process.
 * <p>
 * Instead of a simple boolean, this interface captures detailed error messages mapped to specific fields,
 * making it ideal for API responses or Form validation feedback.
 * </p>
 *
 * @author Ja Khang
 */
public interface ValidateResult {

    /**
     * Checks if the validation failed.
     *
     * @return {@code true} if there is at least one error, {@code false} otherwise.
     */
    boolean hasError();

    /**
     * Retrieves the detailed error messages.
     *
     * @return a Map where:
     *         <ul>
     *             <li><strong>Key:</strong> The field name (e.g., "email", "password").</li>
     *             <li><strong>Value:</strong> The error message (e.g., "Invalid format").</li>
     *         </ul>
     *         Returns an empty map if validation succeeded.
     */
    Map<String, String> details();

    /**
     * Retrieves the class type of the object being validated.
     * <p>
     * Useful for debugging or logging to know which entity caused the validation error.
     * </p>
     *
     * @return the Class object (e.g., User. Class), or {@code null} if not specified.
     */
    Class<?> getValidatedClass();

    /**
     * Merges this result with another validation result.
     * <p>
     * <strong>Merge Strategy:</strong>
     * <ul>
     *     <li>If both are valid, returns a valid result.</li>
     *     <li>If errors exist, combines error maps from both results.</li>
     *     <li><strong>Conflict Resolution:</strong> If both results have an error for the same field,
     *     the messages should be concatenated (e.g., "Too short; Missing number").</li>
     * </ul>
     * </p>
     *
     * @param other the other result to merge.
     * @return a new {@code ValidateResult} containing the accumulated errors.
     */
    ValidateResult merge(ValidateResult other);
}
