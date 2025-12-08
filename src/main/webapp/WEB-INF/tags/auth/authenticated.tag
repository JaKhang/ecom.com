<%@ tag description="Show body if user is logged in" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Lấy object từ session dựa trên key "USER_PRINCIPAL" --%>
<c:set var="auth" value="${sessionScope['USER_PRINCIPAL']}"/>

<%-- Kiểm tra tồn tại và active --%>
<c:if test="${not empty auth and auth.isActive()}">
    <jsp:doBody/>
</c:if>
