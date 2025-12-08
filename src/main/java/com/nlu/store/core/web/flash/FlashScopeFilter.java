package com.nlu.store.core.web.flash;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

// Áp dụng cho toàn bộ ứng dụng (/*)
@WebFilter("/*")
public class FlashScopeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 1. Lấy tất cả tên attribute trong session
            Enumeration<String> attributeNames = session.getAttributeNames();
            List<String> flashAttributesToRemove = new ArrayList<>();

            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();

                // 2. Kiểm tra nếu attribute bắt đầu bằng tiền tố FLASH_SCOPE_
                if (attributeName.startsWith(Flash.PREFIX)) {

                    // Lấy giá trị gốc
                    Object value = session.getAttribute(attributeName);

                    // Tên key thực tế (bỏ tiền tố đi). VD: FLASH_SCOPE_msg -> msg
                    String realKey = attributeName.substring(Flash.PREFIX.length());

                    // 3. CHUYỂN TỪ SESSION SANG REQUEST
                    // Để JSP có thể gọi trực tiếp bằng ${realKey}
                    request.setAttribute(realKey, value);

                    // Đánh dấu để xóa
                    flashAttributesToRemove.add(attributeName);
                }
            }

            // 4. XÓA KHỎI SESSION (Auto Remove)
            for (String attr : flashAttributesToRemove) {
                session.removeAttribute(attr);
            }
        }

        // Cho phép request đi tiếp đến Servlet/JSP đích
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}