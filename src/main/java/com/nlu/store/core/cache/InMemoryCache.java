package com.nlu.store.core.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A thread-safe, in-memory implementation of the {@link Cache} interface.
 * <p>
 * Key Features:
 * <ul>
 *   <li><b>Thread-Safety:</b> Uses {@link ConcurrentHashMap} to handle concurrent access efficiently.</li>
 *   <li><b>TTL Support:</b> Implements both Lazy Eviction (on get) and Background Eviction (periodic cleanup).</li>
 *   <li><b>Type Conversion:</b> Automatically handles number conversions (e.g., Integer to Double).</li>
 * </ul>
 * </p>
 */
public class InMemoryCache implements Cache {

    // Primary storage. ConcurrentHashMap allows concurrent reads/writes without locking the entire map.
    private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();

    // Background thread for removing expired entries periodically.
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    /**
     * Initializes the cache and starts the background cleanup task.
     * The cleanup task runs every 60 seconds by default.
     */
    public InMemoryCache() {
        cleaner.scheduleAtFixedRate(this::cleanupTask, 60, 60, TimeUnit.SECONDS);
    }

    // --- INNER CLASS: Wrapper for Value & Expiry ---
    private static class CacheEntry {
        private final Object value;
        private final long expiryTime; // Expiration timestamp in milliseconds

        CacheEntry(Object value, long ttlSeconds) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // --- CORE IMPLEMENTATION ---

    @Override
    public void put(String key, Object value, long ttlSeconds) {
        if (key == null || value == null) return;
        store.put(key, new CacheEntry(value, ttlSeconds));
    }

    @Override
    public Object get(String key) {
        CacheEntry entry = store.get(key);

        // 1. Miss: Key does not exist
        if (entry == null) return null;

        // 2. Lazy Eviction: Key exists but is expired
        if (entry.isExpired()) {
            store.remove(key);
            return null;
        }

        // 3. Hit: Return value
        return entry.value;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key); // Retrieves value (handling TTL logic)

        if (value == null || clazz == null) return null;

        // Case 1: Exact type match or subclass
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        // Case 2: Flexible Number conversion
        // (e.g., Stored Integer(10), requested Double.class -> returns 10.0)
        if (value instanceof Number && Number.class.isAssignableFrom(clazz)) {
            return convertNumber((Number) value, clazz);
        }

        // Case 3: Type mismatch
        return null;
    }

    // --- TYPED GETTERS ---

    @Override
    public String getString(String key) {
        Object val = get(key);
        return (val != null) ? val.toString() : null;
    }

    @Override
    public Integer getInt(String key) {
        Object val = get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return null; }
    }

    @Override
    public Long getLong(String key) {
        Object val = get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.parseLong(val.toString()); } catch (Exception e) { return null; }
    }

    @Override
    public Double getDouble(String key) {
        Object val = get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return null; }
    }

    @Override
    public Boolean getBoolean(String key) {
        Object val = get(key);
        if (val == null) return null;
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(val.toString());
    }

    // --- MANAGEMENT ---

    @Override
    public void remove(String key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    /**
     * Shuts down the background cleanup thread.
     * <p>
     * <b>Important:</b> Call this method when destroying the application context
     * (e.g., in {@code ServletContextListener.contextDestroyed}) to prevent memory leaks.
     * </p>
     */
    public void shutdown() {
        cleaner.shutdown();
    }

    // --- PRIVATE HELPERS ---

    /**
     * Background task to remove expired entries.
     */
    private void cleanupTask() {
        store.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Helper to convert between different Number sub-types.
     */
    @SuppressWarnings("unchecked")
    private <T> T convertNumber(Number value, Class<T> clazz) {
        if (clazz == Integer.class) return (T) Integer.valueOf(value.intValue());
        if (clazz == Long.class)    return (T) Long.valueOf(value.longValue());
        if (clazz == Double.class)  return (T) Double.valueOf(value.doubleValue());
        if (clazz == Float.class)   return (T) Float.valueOf(value.floatValue());
        if (clazz == Short.class)   return (T) Short.valueOf(value.shortValue());
        return null;
    }
}
