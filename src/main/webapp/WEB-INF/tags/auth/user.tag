<%@ tag description="Print user property" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="property" required="false" type="java.lang.String" %>

<c:set var="auth" value="${sessionScope['USER_PRINCIPAL']}" />

<c:if test="${not empty auth}">
    <c:choose>
        <%-- Mặc định in username --%>
        <c:when test="${empty property or property == 'username'}">
            ${auth.username()}
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
