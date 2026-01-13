<%@ tag import="com.nlu.store.core.web.HttpContext" %>
<%@ tag description="Show body if user has specific authority" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="role" required="true" type="java.lang.String" %>
<c:set var="authKey" value="<%= HttpContext.AUTHENTICATION_KEY %>" />

<c:set var="auth" value="${sessionScope[authKey]}" />

<%-- Gọi method .authorities() của record/interface --%>
<c:if test="${not empty auth and auth.authorities().contains(role)}">
    <jsp:doBody/>
</c:if>
