<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="products" required="true" type="java.util.List<com.nlu.store.modules.catalog.model.SimpleProduct>" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<div class="list-products-5">
    <c:forEach items="${products}" var="i">
        <pro:product-card-grid product="${i}"/>
    </c:forEach>
</div>