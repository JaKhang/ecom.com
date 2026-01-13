package com.nlu.store.core.cache;

/**
 * A generic Cache interface defining core caching behaviors.
 * <p>
 * This interface supports Key-Value storage with Time-To-Live (TTL),
 * automatic expiration, and type-safe retrieval methods.
 * </p>
 */
public interface Cache {

    /**
     * Stores a value in the cache with a specified expiration time.
     *
     * @param key        The unique identifier for the cache entry (cannot be null).
     * @param value      The value to store (cannot be null).
     * @param ttlSeconds The Time-To-Live in seconds. The entry will expire after this duration.
     */
    void put(String key, Object value, long ttlSeconds);

    /**
     * Retrieves the raw object associated with the given key.
     * <p>
     * This method performs lazy eviction: if the entry exists but has expired,
     * it will be removed and null will be returned.
     * </p>
     *
     * @param key The unique identifier.
     * @return The stored value, or {@code null} if the key does not exist or has expired.
     */
    Object get(String key);

    /**
     * Retrieves a value and safely casts it to the specified type.
     * <p>
     * Example: {@code User user = cache.get("u1", User.class);}
     * </p>
     *
     * @param key   The unique identifier.
     * @param clazz The target class to cast the value to.
     * @param <T>   The generic type of the class.
     * @return The value cast to type T, or {@code null} if not found or type mismatch.
     */
    <T> T get(String key, Class<T> clazz);

    // --- Type-Safe Convenience Methods ---

    /**
     * Retrieves the value as a String.
     * @param key The unique identifier.
     * @return The string representation of the value, or null.
     */
    String getString(String key);

    /**
     * Retrieves the value as an Integer.
     * <p>
     * This method attempts to parse Strings ("123") or cast other Number types automatically.
     * </p>
     * @param key The unique identifier.
     * @return The Integer value, or null if conversion fails.
     */
    Integer getInt(String key);

    /**
     * Retrieves the value as a Long.
     * @param key The unique identifier.
     * @return The Long value, or null if conversion fails.
     */
    Long getLong(String key);

    /**
     * Retrieves the value as a Double.
     * @param key The unique identifier.
     * @return The Double value, or null if conversion fails.
     */
    Double getDouble(String key);

    /**
     * Retrieves the value as a Boolean.
     * <p>
     * Supports parsing strings like "true"/"false" (case-insensitive).
     * </p>
     * @param key The unique identifier.
     * @return The Boolean value, or null.
     */
    Boolean getBoolean(String key);

    // --- Management Methods ---

    /**
     * Manually removes a specific entry from the cache.
     * @param key The unique identifier to remove.
     */
    void remove(String key);

    /**
     * Clears all entries from the cache.
     */
    void clear();

    /**
     * Returns the current number of entries in the cache.
     * @return The size of the cache.
     */
    int size();
}
