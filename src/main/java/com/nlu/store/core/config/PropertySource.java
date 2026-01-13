package com.nlu.store.core.config;

import com.nlu.store.core.utils.DurationUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Base abstract class for property resolution strategies.
 * <p>
 * This class defines the core contract for retrieving properties via {@link #getProperty(String)}
 * and provides <b></b> implementations for type conversion (String to int, long, double).
 * This ensures consistent parsing logic across all specific implementations (File, DB, Env, etc.).
 * </p>
 */
public abstract class PropertySource {

    /**
     * Retrieves the raw string value for the given key.
     * <p>
     * Subclasses must implement this method to fetch data from their specific source
     * (e.g., a Map, a Properties object, or a Database).
     * </p>
     *
     * @param key the property name
     * @return the property value, or {@code null} if not found
     */
    public abstract String getProperty(String key);

    // ========================================================================
    // String Methods
    // ========================================================================

    /**
     * Retrieves a property value, returning a default value if missing.
     *
     * @param key          the property name
     * @param defaultValue the value to return if the property is not found
     * @return the property value or the default value
     */
    public  String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null) ? value : defaultValue;
    }

    /**
     * Retrieves a required property value.
     *
     * @param key the property name
     * @return the property value
     * @throws IllegalArgumentException if the property is not found
     */
    public  String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' not found");
        }
        return value;
    }

    /**
     * Retrieves a property value, returning a default value if missing.
     *
     * @param key          the property name
     * @param defaultValue the value to return if the property is not found
     * @return the property value or the default value
     */
    public  String getString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    /**
     * Retrieves a property value, returning a default value if missing.
     *
     * @param key the property name
     * @return the property value or the default value
     */
    public  String getString(String key) {
        return getRequiredProperty(key);
    }


    // ========================================================================
    // Integer Methods
    // ========================================================================

    /**
     * Retrieves an integer property. Throws exception if missing or invalid.
     *
     * @param key the property name
     * @return the parsed integer value
     */
    public  int getInt(String key) {
        String value = getRequiredProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for key '" + key + "': " + value);
        }
    }

    /**
     * Retrieves an integer property with a default value.
     *
     * @param key          the property name
     * @param defaultValue the fallback value
     * @return the parsed integer or the default value
     */
    public  int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logWarning(key, value, "integer", defaultValue);
            return defaultValue;
        }
    }

    // ========================================================================
    // Long Methods
    // ========================================================================

    public  long getLong(String key) {
        String value = getRequiredProperty(key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid long for key '" + key + "': " + value);
        }
    }

    public  long getLong(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logWarning(key, value, "long", defaultValue);
            return defaultValue;
        }
    }

    // ========================================================================
    // Double Methods
    // ========================================================================

    public  double getDouble(String key) {
        String value = getRequiredProperty(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid double for key '" + key + "': " + value);
        }
    }

    public  double getDouble(String key, double defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logWarning(key, value, "double", defaultValue);
            return defaultValue;
        }
    }

    public Duration getDuration(String key) {
        String value = getRequiredProperty(key);
        try {
            return DurationUtils.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid double for key '" + key + "': " + value);
        }
    }

    public Duration getDuration(String key, Duration defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return DurationUtils.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid double for key '" + key + "': " + value);
        }
    }

    // ========================================================================
    // Internal Helpers
    // ========================================================================

    /**
     * Logs a warning message when parsing fails.
     * This method is protected so subclasses could potentially override it
     * if they want to use a specific Logger (SLF4J, Log4j) instead of System.err.
     */
    protected void logWarning(String key, String value, String type, Object defaultValue) {
        System.err.printf("Warning: Invalid %s for key '%s': '%s'. Using default: %s%n",
                type, key, value, defaultValue);
    }

    // ========================================================================
    // Collection Methods (String)
    // ========================================================================

    /**
     * Lấy danh sách chuỗi từ config dạng: "admin, user, guest"
     */
    public List<String> getStringList(String key) {
        return parseStringList(getRequiredProperty(key));
    }

    public List<String> getStringList(String key, List<String> defaultValue) {
        String value = getProperty(key);
        return (value != null) ? parseStringList(value) : defaultValue;
    }

    /**
     * Lấy tập hợp chuỗi (không trùng lặp)
     */
    public Set<String> getStringSet(String key) {
        return new HashSet<>(getStringList(key));
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        String value = getProperty(key);
        return (value != null) ? new HashSet<>(parseStringList(value)) : defaultValue;
    }

    // ========================================================================
    // Collection Methods (Integer)
    // ========================================================================

    /**
     * Lấy danh sách số nguyên từ config dạng: "12, 24, 48"
     */
    public List<Integer> getIntegerList(String key) {
        String value = getRequiredProperty(key);
        return parseIntegerList(value, key);
    }

    public List<Integer> getIntegerList(String key, List<Integer> defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return parseIntegerList(value, key);
        } catch (IllegalArgumentException e) {
            logWarning(key, value, "integer list", defaultValue);
            return defaultValue;
        }
    }

    /**
     * Lấy tập hợp số nguyên (dùng cho whitelist limit, status code...)
     */
    public Set<Integer> getIntegerSet(String key) {
        return new HashSet<>(getIntegerList(key));
    }

    public Set<Integer> getIntegerSet(String key, Set<Integer> defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return new HashSet<>(parseIntegerList(value, key));
        } catch (IllegalArgumentException e) {
            logWarning(key, value, "integer set", defaultValue);
            return defaultValue;
        }
    }

    // ========================================================================
    // Private Parsing Helpers
    // ========================================================================

    private List<String> parseStringList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Integer> parseIntegerList(String value, String key) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer list for key '" + key + "': " + value);
        }
    }
}
