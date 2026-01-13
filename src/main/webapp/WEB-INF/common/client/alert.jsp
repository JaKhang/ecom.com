<%@ page import="com.nlu.store.core.web.HttpContext" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="alertKey" value="<%= HttpContext.ALERT_KEY %>" />
<c:set var="alertObj" value="${requestScope[alertKey]}" />

<c:if test="${not empty alertObj}">
    <fmt:message key="${alertObj.messageKey}" var="alertText" />
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            Toastify({
                text: "${alertText}",
                className: "bg-${alertObj.cssClass} shadow-sm", // Thêm class Bootstrap
                style: {
                    background: "unset", // Để CSS class quyết định màu nền
                    boxShadow: "none"
                },
                gravity: "top", // top or bottom
                position: "right", // left, center or right
                duration: 4000,
                close: true,
                stopOnFocus: true
            }).showToast();
        });
    </script>

</c:if>