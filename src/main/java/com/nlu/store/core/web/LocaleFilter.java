package com.nlu.store.core.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.jstl.core.Config;
import java.io.IOException;
import java.util.Locale;

@WebFilter(filterName = "LocaleFilter", urlPatterns = {"/*"})
public class LocaleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // 1. Kiểm tra xem người dùng có đang yêu cầu đổi ngôn ngữ không (có tham số ?lang=...)
        String langParam = req.getParameter("lang");

        if (langParam != null) {
            // === TRƯỜNG HỢP 1: NGƯỜI DÙNG CHỦ ĐỘNG ĐỔI NGÔN NGỮ ===

            // Cập nhật Session & JSTL
            updateLocale(session, langParam);

            // Lưu vào Cookie (để nhớ cho lần sau) - Hết hạn sau 30 ngày
            setCookie(res, "lang", langParam, 30 * 24 * 60 * 60);

        } else {
            // === TRƯỜNG HỢP 2: TRUY CẬP BÌNH THƯỜNG ===

            // Nếu Session chưa có thông tin ngôn ngữ (lần đầu vào hoặc mới mở lại trình duyệt)
            if (session.getAttribute("lang") == null) {
                // Thử tìm trong Cookie xem trước đây đã chọn chưa
                String cookieLang = getCookieValue(req, "lang");

                if (cookieLang != null) {
                    // Có cookie -> Khôi phục ngôn ngữ từ cookie
                    updateLocale(session, cookieLang);
                } else {
                    // Không có cookie -> Mặc định là Tiếng Anh (hoặc ngôn ngữ bạn muốn)
                    updateLocale(session, "en");
                }
            }
        }

        chain.doFilter(request, response);
    }

    // --- Hàm hỗ trợ: Cập nhật Locale cho Session và JSTL ---
    private void updateLocale(HttpSession session, String langCode) {
        Locale locale;
        if ("vi".equals(langCode)) {
            locale = new Locale("vi", "VN");
        } else {
            locale = Locale.ENGLISH;
        }

        // Cấu hình cho thẻ <fmt:message>
        Config.set(session, Config.FMT_LOCALE, locale);

        // Lưu biến lang để dùng trong JSP (ví dụ: để highlight nút cờ)
        session.setAttribute("lang", langCode);
    }

    // --- Hàm hỗ trợ: Lưu Cookie ---
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/"); // Cookie có hiệu lực trên toàn bộ website
        response.addCookie(cookie);
    }

    // --- Hàm hỗ trợ: Lấy giá trị Cookie ---
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
