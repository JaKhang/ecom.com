<%@ tag description="Show body if user is NOT logged in" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="auth" value="${sessionScope['USER_PRINCIPAL']}" />

<c:if test="${empty auth}">
    <jsp:doBody/>
</c:if>
