package com.nlu.store.core.web.bind;

/**
 * The central service interface for type conversion logic.
 * <p>
 * This interface acts as both a <b>Registry</b> for custom converters and a <b>Facade</b>
 * for executing conversions from raw {@code String} inputs (typically from HTTP requests)
 * to strongly-typed Java objects.
 */
public interface ConversionService {

    /**
     * Converts the given source string to the specified target type.
     *
     * @param source     The raw input string to convert. Can be {@code null}.
     * @param targetType The class of the desired result type.
     * @param <T>        The type of the result.
     * @return The converted object.
     * @throws IllegalArgumentException if conversion fails.
     */
    <T> T convert(String source, Class<T> targetType);

    /**
     * Registers a custom converter for a specific target type.
     *
     * @param targetType The class type that this converter handles.
     * @param converter  The conversion logic.
     * @param <T>        The target type.
     */
    <T> void addConverter(Class<T> targetType, Converter<T> converter);

    /**
     * A functional interface representing the logic to convert a String to a specific type T.
     */
    @FunctionalInterface
    interface Converter<T> {
        /**
         * Converts the source string to the target type.
         *
         * @param source The input string (guaranteed to be non-null).
         * @return The converted object.
         * @throws Exception If the input format is invalid.
         */
        T convert(String source) throws Exception; // Renamed from 'apply'
    }
}
