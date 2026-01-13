package com.nlu.store.core.utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {

    // Regex để tách số và đơn vị (Ví dụ: "10m" -> "10" và "m")
    private static final Pattern SIMPLE_FORMAT = Pattern.compile("^\\s*(\\d+)\\s*([a-zA-Z]+)\\s*$");

    /**
     * Parse chuỗi thời gian thông minh.
     * Hỗ trợ:
     * - ISO-8601: "PT15M", "P2D"
     * - Simple: "10ns", "10ms", "10s", "10m", "10h", "10d"
     * - Raw number: "5000" (tính là ms)
     */
    public static Duration parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 1. Nếu là chuẩn ISO-8601 (Bắt đầu bằng P hoặc -P)
        if (text.toUpperCase().startsWith("P") || text.toUpperCase().startsWith("-P")) {
            return Duration.parse(text);
        }

        // 2. Nếu là định dạng đơn giản (Simple Format)
        Matcher matcher = SIMPLE_FORMAT.matcher(text);
        if (matcher.matches()) {
            long amount = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            switch (unit) {
                case "ns": return Duration.ofNanos(amount);
                case "us": return Duration.ofNanos(amount * 1000); // Microseconds
                case "ms": return Duration.ofMillis(amount);
                case "s":  return Duration.ofSeconds(amount);
                case "m":  return Duration.ofMinutes(amount);
                case "h":  return Duration.ofHours(amount);
                case "d":  return Duration.ofDays(amount);
                default:   throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        // 3. Nếu chỉ là số thuần túy -> Mặc định là Milliseconds
        try {
            return Duration.ofMillis(Long.parseLong(text.trim()));
        } catch (NumberFormatException e) {
            throw new DateTimeParseException("Cannot parse duration: " + text, text, 0);
        }
    }
}
