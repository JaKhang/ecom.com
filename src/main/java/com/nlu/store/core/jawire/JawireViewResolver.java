package com.nlu.store.core.jawire;

/**
 * Interface dùng để phân giải tên logical view thành đường dẫn vật lý JSP.
 * <p>
 * Ví dụ: Chuyển "auth/login" thành "/WEB-INF/views/auth/login.jsp".
 * </p>
 */
public interface JawireViewResolver {

    /**
     * Resolve a view name to a JSP path.
     *
     * @param viewName Tên view được trả về từ Component.render()
     * @return Đường dẫn tuyệt đối tới file JSP (bắt đầu bằng /)
     */
    String resolve(String viewName);
}
