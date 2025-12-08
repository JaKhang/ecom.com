package com.nlu.store.core.web;

import com.nlu.store.core.web.ViewResolver;

public class InternalResourceViewResolver implements ViewResolver {

    private final String prefix;
    private final String suffix;

    /**
     * @param prefix Tiền tố đường dẫn (VD: "/WEB-INF/views/")
     * @param suffix Hậu tố file (VD: ".jsp")
     */
    public InternalResourceViewResolver(String prefix, String suffix) {
        this.prefix = (prefix != null) ? prefix : "";
        this.suffix = (suffix != null) ? suffix : "";
    }

    @Override
    public String resolveViewName(String viewName) {
        // Nếu viewName bắt đầu bằng "redirect:", trả về nguyên bản để HttpContext xử lý
        if (viewName.startsWith("redirect:")) {
            return viewName;
        }
        return prefix + viewName + suffix;
    }
}
