package com.nlu.store.core.jawire;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.web.ServletHttpContext;
import com.nlu.store.core.web.WebInfrastructure;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import lombok.Setter;

import java.io.IOException;

@Setter
public class JawireTag extends SimpleTagSupport {

    private String component; // Tên class component được truyền vào từ attribute
    private String id;

    @Override
    public void doTag() throws JspException, IOException {
        try {
            PageContext pageContext = (PageContext) getJspContext();
            // 1. Setup CDI & Component
            WebInfrastructure webInfrastructure = CDI.current().select(WebInfrastructure.class).get();
            ObjectMapper mapper = webInfrastructure.objectMapper().copy();

            Class<?> clazz = Class.forName(component);
            Component instance = (Component) CDI.current().select(clazz).get();
            instance.setHttpContext(new ServletHttpContext((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), webInfrastructure));
            instance.setId(id == null ? ULID.fast().toString() : id);
            // 2. Lifecycle
            instance.mount();
            instance.boot();
            instance.rendering();

            // 3. Prepare Snapshot
            Component.JawireResponse snapshot = instance.dehydrate();

            String jsonSnapshot = mapper.writeValueAsString(snapshot);
            // --- BẮT ĐẦU SỬA ---

            // Lấy Context và Writer ra biến để dùng chung
            JspWriter out = pageContext.getOut();

            // 4. Render thẻ mở
            pageContext.getOut().write(String.format(
                    "<div id=\"%s\" jw-snapshot='%s'>",
                    instance.getId(), // ID duy nhất
                    jsonSnapshot
            ));

            // Đặt biến vào Request Scope
            pageContext.setAttribute("component", instance, PageContext.REQUEST_SCOPE);

            // [QUAN TRỌNG] Đẩy dữ liệu từ bộ đệm ra trình duyệt ngay lập tức
            // Để đảm bảo thẻ <div> nằm TRƯỚC nội dung include
            out.flush();

            // 5. Include View
            JawireViewResolver viewResolver = CDI.current().select(JawireViewResolver.class).get();
            String viewPath = viewResolver.resolve(instance.view());

            // Kiểm tra đường dẫn an toàn
            if (viewPath != null && !viewPath.isEmpty()) {
                pageContext.include(viewPath);
            } else {
                out.write("Error: Component.render() returned null or empty path.");
            }

            // 6. Render thẻ đóng
            out.write("</div>");

        } catch (Exception e) {
            throw new JspException("Error rendering Livewire component: " + component, e);
        }
    }


}
