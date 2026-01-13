package com.nlu.store.core.web;

import com.nlu.store.core.data.ULID;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents the security principal (authenticated user) in the system.
 * <p>
 * This interface holds the essential identity and security information of a logged-in user.
 * It is designed to be stored in the HTTP Session, hence it extends {@link Serializable}.
 * </p>
 * <p>
 * The design favors a record-like accessor style (e.g., {@code id()} instead of {@code getId()})
 * for brevity and immutability semantics.
 * </p>
 *
 * @author Ja Khang
 */
public interface Authentication extends Serializable {

    /**
     * Returns the unique identifier of the user.
     * <p>
     * Uses {@link ULID} (Universally Unique Lexicographically Sortable Identifier)
     * for better database indexing and sorting compared to standard UUIDs.
     * </p>
     *
     * @return the user's unique ID.
     */
    ULID id();

    /**
     * Returns the identifier used for logging in (typically email or login ID).
     *
     * @return the identifier string.
     */
    String identifier();

    /**
     * Checks if the user's account has been verified (e.g., via email confirmation).
     * <p>
     * Unverified users might have restricted access to certain features.
     * </p>
     *
     * @return {@code true} if the account is verified.
     */
    boolean isVerified();

    /**
     * Checks if the user's account is currently active.
     * <p>
     * Returns {@code false} if the user is banned, locked, or soft-deleted.
     * </p>
     *
     * @return {@code true} if the account is active.
     */
    boolean isActive();

    /**
     * Retrieves the list of roles or permissions granted to the user.
     * <p>
     * Examples: "ROLE_ADMIN", "USER_READ", "USER_WRITE".
     * </p>
     *
     * @return a collection of authority strings.
     */
    Collection<String> authorities();

    /**
     * Provides a flexible map for additional user metadata.
     * <p>
     * This allows storing non-essential UI data (like "avatarUrl", "fullName", "theme")
     * in the session without modifying the core Authentication interface schema.
     * </p>
     *
     * @return a map of additional user attributes.
     */
    Map<String, String> info();
}
