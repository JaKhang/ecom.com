package com.nlu.store.core.web.flash;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Flash {
    // Tiền tố để nhận diện Flash Attribute, tránh trùng với session thường
    public static final String PREFIX = "FLASH_SCOPE_";

    /**
     * Lưu dữ liệu Flash
     */
    public static void put(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession();
        session.setAttribute(PREFIX + name, value);
    }

    /**
     * Xóa thủ công dữ liệu Flash (nếu cần hủy bỏ giữa chừng)
     */
    public static void remove(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(PREFIX + name);
        }
    }
}
