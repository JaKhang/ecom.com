package com.nlu.store.core.config;

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
}
