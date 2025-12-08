package com.nlu.store.core.dao;

import com.nlu.store.core.data.ULID;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class AbstractMapper<T> implements RowMapper<T> {
    protected LocalDateTime getLocalDateTime(ResultSet rs, String colName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(colName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    // Helper for LocalDate
    protected LocalDate getLocalDate(ResultSet rs, String colName) throws SQLException {
        Date date = rs.getDate(colName);
        return date != null ? date.toLocalDate() : null;
    }

    // Helper for ULID (Assuming you store it as String)
    protected ULID getULID(ResultSet rs, String colName) throws SQLException {
        String ulidString = rs.getString(colName);
        return ulidString != null ? ULID.from(ulidString) : null;
    }


    protected <E extends Enum<E>> E getEnum(ResultSet rs, String colName, Class<E> enumClass) throws SQLException {
        String value = rs.getString(colName);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Duyệt qua tất cả các constant của Enum để so sánh equalsIgnoreCase
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        // Nếu không tìm thấy, ném lỗi để biết data bị sai
        throw new SQLException("Can't map " + value + " to enum " + enumClass.getSimpleName());
    }

}
