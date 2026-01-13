package com.nlu.store.core.jdbc;

import com.nlu.store.core.data.ULID;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A functional wrapper around {@link ResultSet} providing a more expressive and
 * type-safe API for data retrieval.
 * <p>
 * This interface simplifies common JDBC tasks such as handling SQL {@code NULL} values,
 * mapping JSON columns, and working with modern Java 8+ types like {@link LocalDateTime}
 * and {@link Optional}.
 * </p>
 *
 * @author Ja Khang
 * @version 1.0
 */
public interface ResultSetReader {

    /**
     * Moves the cursor forward one row from its current position.
     *
     * @return {@code true} if the new current row is valid; {@code false} if there are no more rows.
     * @throws SQLException if a database access error occurs.
     */
    boolean next() throws SQLException;

    /**
     * Reports whether the last column read had a value of SQL {@code NULL}.
     * <p>
     * Note: You must first call one of the getter methods on a column to use this method.
     * </p>
     *
     * @return {@code true} if the last column value read was SQL {@code NULL}; {@code false} otherwise.
     * @throws SQLException if a database access error occurs.
     */
    boolean wasNull() throws SQLException;

    /**
     * Returns the underlying {@link ResultSet} object.
     *
     * @return the raw JDBC result set.
     */
    ResultSet getRawResultSet();

    // --- Primitive Accessors ---

    /**
     * Retrieves the value of the designated column as an {@code int}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    int getInt(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as an {@code int}.
     * Use index-based access for better performance in tight loops.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    int getInt(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code long}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0L if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    long getLong(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code long}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0L if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    long getLong(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code double}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0.0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    double getDouble(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code double}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0.0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    double getDouble(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code boolean}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; {@code false} if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    boolean getBoolean(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code boolean}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; {@code false} if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    boolean getBoolean(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code float}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0.0f if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    float getFloat(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code float}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0.0f if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    float getFloat(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code short}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    short getShort(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code short}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    short getShort(int columnIndex) throws SQLException;

    /**
     * Retrieves the value of the designated column as a {@code byte}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    byte getByte(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@code byte}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; 0 if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    byte getByte(int columnIndex) throws SQLException;

    // --- Nullable Wrappers ---

    /**
     * Retrieves the value as an {@link Integer}, returning {@code null} if the SQL value is NULL.
     *
     * @param columnLabel the label for the column.
     * @return the value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    default Integer getNullableInt(String columnLabel) throws SQLException {
        int result = getInt(columnLabel);
        return wasNull() ? null : result;
    }

    /**
     * Retrieves the value as an {@link Integer} by index, returning {@code null} if the SQL value is NULL.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    default Integer getNullableInt(int columnIndex) throws SQLException {
        int result = getInt(columnIndex);
        return wasNull() ? null : result;
    }

    /**
     * Retrieves the value as a {@link Long}, returning {@code null} if the SQL value is NULL.
     *
     * @param columnLabel the label for the column.
     * @return the value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    default Long getNullableLong(String columnLabel) throws SQLException {
        long result = getLong(columnLabel);
        return wasNull() ? null : result;
    }

    /**
     * Retrieves the value as a {@link Double}, returning {@code null} if the SQL value is NULL.
     *
     * @param columnLabel the label for the column.
     * @return the value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    default Double getNullableDouble(String columnLabel) throws SQLException {
        double result = getDouble(columnLabel);
        return wasNull() ? null : result;
    }

    /**
     * Retrieves the value as a {@link Boolean}, returning {@code null} if the SQL value is NULL.
     *
     * @param columnLabel the label for the column.
     * @return the value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    default Boolean getNullableBoolean(String columnLabel) throws SQLException {
        boolean result = getBoolean(columnLabel);
        return wasNull() ? null : result;
    }

    // --- Objects & Custom Types ---

    /**
     * Retrieves the value of the designated column as a {@link String}.
     *
     * @param columnLabel the label for the column.
     * @return the column value; {@code null} if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    String getString(String columnLabel) throws SQLException;

    /**
     * Retrieves the value of the designated column index as a {@link String}.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the column value; {@code null} if the value is SQL {@code NULL}.
     * @throws SQLException if a database access error occurs.
     */
    String getString(int columnIndex) throws SQLException;

    /**
     * Retrieves the value as an {@link Optional} of {@link String}.
     *
     * @param columnLabel the label for the column.
     * @return an Optional containing the string, or an empty Optional if the value is NULL.
     * @throws SQLException if a database access error occurs.
     */
    default Optional<String> getOptionalString(String columnLabel) throws SQLException {
        return Optional.ofNullable(getString(columnLabel));
    }

    /**
     * Retrieves the value of the designated column and casts it to the specified type.
     *
     * @param <T> the type to cast to.
     * @param columnLabel the label for the column.
     * @param type the class of the type to return.
     * @return the column value.
     * @throws SQLException if a database access error occurs.
     */
    <T> T get(String columnLabel, Class<T> type) throws SQLException;

    /**
     * Retrieves the value of the designated column index and casts it to the specified type.
     *
     * @param <T> the type to cast to.
     * @param columnIndex the first column is 1, the second is 2, ...
     * @param type the class of the type to return.
     * @return the column value.
     * @throws SQLException if a database access error occurs.
     */
    <T> T get(int columnIndex, Class<T> type) throws SQLException;

    /**
     * Maps a column value to a specific Java Enum.
     *
     * @param <E> the Enum type.
     * @param columnLabel the label for the column.
     * @param enumType the class of the Enum.
     * @return the Enum constant.
     * @throws SQLException if a database access error occurs or mapping fails.
     */
    <E extends Enum<E>> E getEnum(String columnLabel, Class<E> enumType) throws SQLException;

    /**
     * Retrieves the value as a {@link LocalDateTime}.
     *
     * @param columnLabel the label for the column.
     * @return the LocalDateTime or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    LocalDateTime getLocalDateTime(String columnLabel) throws SQLException;

    /**
     * Retrieves the value as a {@link LocalDate}.
     *
     * @param columnLabel the label for the column.
     * @return the LocalDate or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    LocalDate getLocalDate(String columnLabel) throws SQLException;

    /**
     * Retrieves the value as a {@link Locale}.
     *
     * @param columnLabel the label for the column.
     * @return the Locale object.
     * @throws SQLException if a database access error occurs.
     */
    Locale getLocale(String columnLabel) throws SQLException;

    /**
     * Deserializes a JSON column into a Java object of the specified class.
     *
     * @param <T> the type of the resulting object.
     * @param columnLabel the label for the column.
     * @param classType the class to deserialize into.
     * @return the deserialized object.
     * @throws SQLException if a database access error or deserialization error occurs.
     */
    <T> T getJsonObject(String columnLabel, Class<T> classType) throws SQLException;

    /**
     * Retrieves the value as a {@link ULID}.
     *
     * @param columnLabel the label for the column.
     * @return the ULID or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    ULID getULID(String columnLabel) throws SQLException;

    /**
     * Retrieves a JSON column as a {@link Map}.
     *
     * @param columnLabel the label for the column.
     * @return a Map representing the JSON data.
     * @throws SQLException if a database access error occurs.
     */
    Map<String, String> getJson(String columnLabel) throws SQLException;

    /**
     * Retrieves the value as a {@link BigDecimal}.
     *
     * @param columnLabel the label for the column.
     * @return the BigDecimal value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    BigDecimal getBigDecimal(String columnLabel) throws SQLException;

    /**
     * Retrieves the value as a {@link BigDecimal} by index.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @return the BigDecimal value or {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    BigDecimal getBigDecimal(int columnIndex) throws SQLException;

    /**
     * Checks if a column with the specified label exists in the result set.
     *
     * @param columnLabel the label for the column.
     * @return {@code true} if the column exists; {@code false} otherwise.
     */
    boolean hasColumn(String columnLabel);

    /**
     * Retrieves an {@link Integer} only if the column is present and not NULL.
     *
     * @param columnLabel the label for the column.
     * @return the value, or {@code null} if the column is missing or the value is NULL.
     * @throws SQLException if a database access error occurs.
     */
    Integer getIntIfPresent(String columnLabel) throws SQLException;

    /**
     * Retrieves a {@link Long} only if the column is present.
     * Useful for optional aggregate columns like {@code COUNT}.
     *
     * @param columnLabel the label for the column.
     * @return the value, or {@code null} if the column is missing.
     * @throws SQLException if a database access error occurs.
     */
    Long getLongIfPresent(String columnLabel) throws SQLException;

    /**
     * Retrieves a {@link String} only if the column is present.
     *
     * @param columnLabel the label for the column.
     * @return the value, or {@code null} if the column is missing.
     * @throws SQLException if a database access error occurs.
     */
    String getStringIfPresent(String columnLabel) throws SQLException;

    /**
     * Retrieves a {@link BigDecimal} only if the column is present.
     *
     * @param columnLabel the label for the column.
     * @return the value, or {@code null} if the column is missing.
     * @throws SQLException if a database access error occurs.
     */
    BigDecimal getBigDecimalIfPresent(String columnLabel) throws SQLException;

    ULID getULIDIfPresent(String productId) throws SQLException;
}
