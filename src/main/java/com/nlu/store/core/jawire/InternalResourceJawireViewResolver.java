package com.nlu.store.core.jawire;

public class InternalResourceJawireViewResolver implements JawireViewResolver {


    /**
     * Constructor cấu hình Resolver.
     *
     * @param prefix Đường dẫn thư mục chứa view (VD: "/WEB-INF/views/")
     * @param suffix Đuôi file (VD: ".jsp")
     */
    private final String prefix;
    private final String suffix;

    /**
     * @param prefix Path prefix (e.g., "/WEB-INF/views/")
     * @param suffix Path suffix (e.g., ".jsp")
     */
    public InternalResourceJawireViewResolver(String prefix, String suffix) {
        // Đảm bảo prefix luôn kết thúc bằng "/" và suffix luôn bắt đầu bằng "." nếu cần
        this.prefix = prefix.endsWith("/") ? prefix : prefix + "/";
        this.suffix = suffix.startsWith(".") ? suffix : "." + suffix;
    }

    @Override
    public String resolve(String viewName) {
        // Nếu viewName bắt đầu bằng "/", ta bỏ đi để tránh double slash (//)
        if (viewName.startsWith("/")) {
            viewName = viewName.substring(1);
        }
        return prefix + viewName + suffix;
    }
}
