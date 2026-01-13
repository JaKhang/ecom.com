package com.nlu.store.core.jdbc;

import java.util.Locale;

public enum NamingConvention {

    /**
     * Giữ nguyên tên property.
     * Ví dụ: "numberOfProduct" -> "numberOfProduct"
     * Dùng khi tên cột trong DB đặt theo kiểu CamelCase hoặc bạn đã map thủ công.
     */
    NONE {
        @Override
        public String convert(String input) {
            return input;
        }
    },

    /**
     * Chuyển đổi sang Snake Case (thông dụng nhất trong SQL).
     * Ví dụ: "numberOfProduct" -> "number_of_product"
     *        "c.userName" -> "c.user_name"
     */
    SNAKE_CASE {
        @Override
        public String convert(String input) {
            if (input == null || input.isEmpty()) return input;

            // Regex tìm ký tự thường đi liền trước ký tự hoa
            // Thay thế bằng: ký tự thường + "_" + ký tự hoa
            String result = input.replaceAll("([a-z])([A-Z]+)", "$1_$2");
            return result.toLowerCase(Locale.US);
        }
    },

    /**
     * Chuyển sang chữ hoa toàn bộ (thường dùng cho Oracle cũ).
     * Ví dụ: "userName" -> "USER_NAME"
     */
    UPPER_SNAKE_CASE {
        @Override
        public String convert(String input) {
            return SNAKE_CASE.convert(input).toUpperCase(Locale.US);
        }
    };

    // Phương thức abstract để các enum constant implement
    public abstract String convert(String input);
}
