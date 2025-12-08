package com.nlu.store.core.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.data.ULID;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

public class JdbcResultSetReader implements ResultSetReader {

    private final ResultSet rs;
    private final ObjectMapper objectMapper;

    public JdbcResultSetReader(ResultSet rs, ObjectMapper objectMapper) {
        this.rs = rs;
        this.objectMapper = objectMapper;
    }

    // Default constructor if you want to create a default ObjectMapper
    public JdbcResultSetReader(ResultSet rs) {
        this(rs, new ObjectMapper());
    }

    @Override
    public boolean next() throws SQLException {
        return rs.next();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }

    @Override
    public ResultSet getRawResultSet() {
        return rs;
    }

    // --- Primitives (Delegation to ResultSet) ---

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return rs.getInt(columnLabel);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return rs.getInt(columnIndex);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return rs.getLong(columnLabel);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return rs.getLong(columnIndex);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return rs.getDouble(columnLabel);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return rs.getDouble(columnIndex);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return rs.getBoolean(columnLabel);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return rs.getBoolean(columnIndex);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return rs.getFloat(columnLabel);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return rs.getFloat(columnIndex);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return rs.getShort(columnLabel);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return rs.getShort(columnIndex);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return rs.getByte(columnLabel);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return rs.getByte(columnIndex);
    }

    // --- Strings & Objects ---

    @Override
    public String getString(String columnLabel) throws SQLException {
        return rs.getString(columnLabel);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public <T> T get(String columnLabel, Class<T> type) throws SQLException {
        return rs.getObject(columnLabel, type);
    }

    @Override
    public <T> T get(int columnIndex, Class<T> type) throws SQLException {
        return rs.getObject(columnIndex, type);
    }

    // --- Custom Types Implementation ---

    @Override
    public <E extends Enum<E>> E getEnum(String columnLabel, Class<E> enumType) throws SQLException {
        String value = rs.getString(columnLabel);
        if (value == null) {
            return null;
        }
        // Case-insensitive matching
        for (E enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }
        throw new SQLException("Cannot map value '" + value + "' to enum " + enumType.getName());
    }

    @Override
    public LocalDateTime getLocalDateTime(String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    @Override
    public LocalDate getLocalDate(String columnLabel) throws SQLException {
        java.sql.Date date = rs.getDate(columnLabel);
        return date != null ? date.toLocalDate() : null;
    }

    @Override
    public Locale getLocale(String columnLabel) throws SQLException {
        String localeStr = rs.getString(columnLabel);
        if (localeStr == null || localeStr.isEmpty()) {
            return null;
        }
        // Handle "en_US" (Database style) vs "en-US" (IETF style)
        return Locale.forLanguageTag(localeStr.replace('_', '-'));
    }

    @Override
    public <T> T getJsonObject(String columnLabel, Class<T> classType) throws SQLException {
        String json = rs.getString(columnLabel);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, classType);
        } catch (IOException e) {
            throw new SQLException("Failed to parse JSON from column " + columnLabel, e);
        }
    }

    @Override
    public ULID getULID(String columnLabel) throws SQLException {
        // Assuming ULID is stored as a String.
        // If stored as BINARY(16), use rs.getBytes(columnLabel)
        String ulidStr = rs.getString(columnLabel);
        if (ulidStr == null) {
            return null;
        }
        try {
            // Assuming your ULID class has a static parse/fromString method
            return ULID.from(ulidStr);
        } catch (Exception e) {
            throw new SQLException("Invalid ULID format in column " + columnLabel + ": " + ulidStr, e);
        }
    }
}
