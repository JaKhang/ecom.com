package com.nlu.store.core.dao;

import com.nlu.store.core.data.ULID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

public interface ResultSetReader {
    boolean next() throws SQLException;

    // 2. Expose wasNull to allow default methods to handle nullable logic
    boolean wasNull() throws SQLException;

    ResultSet getRawResultSet();


    // --- Primitives (Add Index Support for Performance) ---

    int getInt(String columnLabel) throws SQLException;
    int getInt(int columnIndex) throws SQLException;

    long getLong(String columnLabel) throws SQLException;
    long getLong(int columnIndex) throws SQLException;

    double getDouble(String columnLabel) throws SQLException;
    double getDouble(int columnIndex) throws SQLException;

    boolean getBoolean(String columnLabel) throws SQLException;
    boolean getBoolean(int columnIndex) throws SQLException;

    float getFloat(String columnLabel) throws SQLException;
    float getFloat(int columnIndex) throws SQLException;

    short getShort(String columnLabel) throws SQLException;
    short getShort(int columnIndex) throws SQLException;

    byte getByte(String columnLabel) throws SQLException;
    byte getByte(int columnIndex) throws SQLException;

    // --- Nullable Wrappers (Optimized with Default Methods) ---
    // Logic: Get primitive -> Check wasNull -> Return value or null

    default Integer getNullableInt(String columnLabel) throws SQLException {
        int result = getInt(columnLabel);
        return wasNull() ? null : result;
    }

    default Integer getNullableInt(int columnIndex) throws SQLException {
        int result = getInt(columnIndex);
        return wasNull() ? null : result;
    }

    default Long getNullableLong(String columnLabel) throws SQLException {
        long result = getLong(columnLabel);
        return wasNull() ? null : result;
    }

    default Double getNullableDouble(String columnLabel) throws SQLException {
        double result = getDouble(columnLabel);
        return wasNull() ? null : result;
    }

    default Boolean getNullableBoolean(String columnLabel) throws SQLException {
        boolean result = getBoolean(columnLabel);
        return wasNull() ? null : result;
    }

    // --- Objects & Custom Types ---

    String getString(String columnLabel) throws SQLException;
    String getString(int columnIndex) throws SQLException;

    // 3. Add Optional support for safer null handling
    default Optional<String> getOptionalString(String columnLabel) throws SQLException {
        return Optional.ofNullable(getString(columnLabel));
    }

    <T> T get(String columnLabel, Class<T> type) throws SQLException;
    <T> T get(int columnIndex, Class<T> type) throws SQLException;

    <E extends Enum<E>> E getEnum(String columnLabel, Class<E> enumType) throws SQLException;

    LocalDateTime getLocalDateTime(String columnLabel) throws SQLException;

    LocalDate getLocalDate(String columnLabel) throws SQLException;

    Locale getLocale(String columnLabel) throws SQLException;

    <T> T getJsonObject(String columnLabel, Class<T> classType) throws SQLException;

    ULID getULID(String columnLabel) throws SQLException;
}
