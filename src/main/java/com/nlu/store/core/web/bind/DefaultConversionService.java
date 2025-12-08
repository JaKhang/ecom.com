package com.nlu.store.core.web.bind;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link ConversionService}.
 * <p>
 * Pre-configured with converters for:
 * <ul>
 *     <li>Primitives (int, long, double, boolean)</li>
 *     <li>Wrappers (Integer, Long, Double, Boolean)</li>
 *     <li>Date/Time (LocalDate, LocalDateTime, LocalTime) - ISO format</li>
 *     <li>String (Identity)</li>
 * </ul>
 */
public class DefaultConversionService implements ConversionService {

    private final Map<Class<?>, Converter<?>> converters = new HashMap<>();

    public DefaultConversionService() {
        registerDefaults();
    }

    private void registerDefaults() {
        // 1. String (Identity)
        addConverter(String.class, s -> s);

        // 2. Integers
        Converter<Integer> toInt = Integer::valueOf;
        addConverter(int.class, toInt);
        addConverter(Integer.class, toInt);

        // 3. Longs
        Converter<Long> toLong = Long::valueOf;
        addConverter(long.class, toLong);
        addConverter(Long.class, toLong);

        // 4. Doubles
        Converter<Double> toDouble = Double::valueOf;
        addConverter(double.class, toDouble);
        addConverter(Double.class, toDouble);

        // 5. Booleans (Handles "true", "1", "on")
        Converter<Boolean> toBoolean = s ->
                "true".equalsIgnoreCase(s) || "1".equals(s) || "on".equalsIgnoreCase(s);
        addConverter(boolean.class, toBoolean);
        addConverter(Boolean.class, toBoolean);

        // 6. Date Time (ISO-8601 Standard)
        addConverter(LocalDate.class, LocalDate::parse);         // yyyy-MM-dd
        addConverter(LocalDateTime.class, LocalDateTime::parse); // yyyy-MM-ddTHH:mm:ss
        addConverter(LocalTime.class, LocalTime::parse);         // HH:mm:ss
    }

    @Override
    public <T> void addConverter(Class<T> targetType, Converter<T> converter) {
        converters.put(targetType, converter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(String source, Class<T> targetType) {
        // 1. Handle Null/Empty inputs safely
        if (source == null || source.trim().isEmpty()) {
            return handleDefaultValue(targetType);
        }

        try {
            // 2. Handle Enums dynamically (No registration needed)
            if (targetType.isEnum()) {
                return (T) Enum.valueOf((Class<Enum>) targetType, source);
            }

            // 3. Look up registered converter
            Converter<?> converter = converters.get(targetType);
            if (converter != null) {
                // Call the functional interface method
                return (T) converter.convert(source);
            }

            throw new IllegalArgumentException("No converter found for type: " + targetType.getName());

        } catch (Exception e) {
            // Wrap low-level parsing errors into a clean RuntimeException
            throw new IllegalArgumentException(
                    String.format("Failed to convert value '%s' to type %s", source, targetType.getSimpleName()), e);
        }
    }

    /**
     * Returns safe default values for primitives to avoid NullPointerException during unboxing.
     */
    @SuppressWarnings("unchecked")
    private <T> T handleDefaultValue(Class<T> targetType) {
        if (targetType == int.class) return (T) Integer.valueOf(0);
        if (targetType == long.class) return (T) Long.valueOf(0L);
        if (targetType == double.class) return (T) Double.valueOf(0.0);
        if (targetType == boolean.class) return (T) Boolean.FALSE;
        // For Objects (Integer, String, Date...), return null
        return null;
    }
}

