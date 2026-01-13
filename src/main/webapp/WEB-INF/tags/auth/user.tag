<%@ tag import="com.nlu.store.core.web.HttpContext" %>
<%@ tag description="Print user property" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="property" required="false" type="java.lang.String" %>
<c:set var="authKey" value="<%= HttpContext.AUTHENTICATION_KEY %>"/>
<c:set var="auth" value="${sessionScope[authKey]}"/>
<c:if test="${not empty auth}">
    <c:choose>
        <%-- Mặc định in username --%>
        <c:when test="${empty property or property == 'identifier'}">
            ${auth.identifier()}
        </c:when>

        <c:when test="${property == 'id'}">
            ${auth.id()}
        </c:when>

        <%-- Lấy từ Map info() --%>
        <c:otherwise>
            ${auth.info().get(property)}
        </c:otherwise>
    </c:choose>
</c:if>
