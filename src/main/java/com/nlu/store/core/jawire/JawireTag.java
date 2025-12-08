package com.nlu.store.core.jawire;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Field;

@Setter
public class JawireTag extends SimpleTagSupport {

    private String component; // Tên class component được truyền vào từ attribute

    @Override
    public void doTag() throws JspException, IOException {
        try {
            // 1. Setup CDI & Component
            ObjectMapper mapper = CDI.current().select(ObjectMapper.class).get();

            Class<?> clazz = Class.forName(component);
            Component instance = (Component) CDI.current().select(clazz).get();
            injectMapper(instance, mapper);

            // 2. Lifecycle
            instance.mount();
            instance.boot();
            instance.rendering();

            // 3. Prepare Snapshot
            Component.JawireResponse snapshot = instance.dehydrate();
            String jsonSnapshot = mapper.writeValueAsString(snapshot).replace("\"", "&quot;");

            // --- BẮT ĐẦU SỬA ---

            // Lấy Context và Writer ra biến để dùng chung
            PageContext pageContext = (PageContext) getJspContext();
            JspWriter out = pageContext.getOut();

            // 4. Render thẻ mở
            pageContext.getOut().write(String.format(
                    "<div id=\"%s\" jw-snapshot=\"%s\">",
                    instance.getId(), // ID duy nhất
                    jsonSnapshot
            ));

            // Đặt biến vào Request Scope
            pageContext.setAttribute("component", instance, PageContext.REQUEST_SCOPE);

            // [QUAN TRỌNG] Đẩy dữ liệu từ bộ đệm ra trình duyệt ngay lập tức
            // Để đảm bảo thẻ <div> nằm TRƯỚC nội dung include
            out.flush();

            // 5. Include View
            JawireViewResolver viewResolver = (JawireViewResolver) CDI.current().select(JawireViewResolver.class).get();
            String viewPath = viewResolver.resolve(instance.view());
            System.out.println(viewPath);
            // Kiểm tra đường dẫn an toàn
            if (viewPath != null && !viewPath.isEmpty()) {
                pageContext.include(viewPath);
            } else {
                out.write("Error: Component.render() returned null or empty path.");
            }

            // 6. Render thẻ đóng
            out.write("</div>");

            // --- KẾT THÚC SỬA ---

        } catch (Exception e) {
            throw new JspException("Error rendering Livewire component: " + component, e);
        }
    }

    // Helper: Inject Mapper (giống bên Servlet)
    private void injectMapper(Component component, ObjectMapper mapper) throws Exception {
        Field field = Component.class.getDeclaredField("mapper");
        field.setAccessible(true);
        field.set(component, mapper);
    }
}
