<%@ tag import="com.nlu.store.core.web.HttpContext" %>
<%@ tag description="Show body if user is NOT logged in" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="authKey" value="<%= HttpContext.AUTHENTICATION_KEY %>" />
<c:set var="auth" value="${sessionScope[authKey]}" />

<c:if test="${empty auth}">
    <jsp:doBody/>
</c:if>
