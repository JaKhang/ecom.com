<%@ tag description="pagination" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Khai báo Attribute --%>
<%@ attribute name="pagination" required="true" type="com.nlu.store.core.jawire.Pagination" %>
<%@ attribute name="visible" required="false" type="java.lang.Integer" %>
<%@ attribute name="scrollTarget" required="false" type="java.lang.String" %>

<c:set var="visible" value="${(empty visible) ? 5 : visible}"/>
<c:set var="scrollTarget" value="${(empty scrollTarget) ? 'body' : scrollTarget}"/>


<c:if test="${pagination.totalPages > 1}">
    <nav x-data="{
    scrollToTop() {
        let el = document.getElementById('${scrollTarget}');
        if (el) el.scrollIntoView({ behavior: 'smooth' });
    }
}">
        <ul class="pagination">
                <%-- Nút Previous --%>
            <li class="page-item ${!pagination.hasPrevious() ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link page-prev" jw-click="previousPage()"></a>
            </li>

                <%-- Danh sách các trang --%>
            <c:forEach items="${pagination.getVisiblePages(visible)}" var="pNum">
                <li class="page-item">
                    <a @click="scrollToTop()" class="page-link  ${pagination.page == pNum ? 'active' : ''}" jw-click="gotoPage(${pNum})">${pNum}</a>
                </li>
            </c:forEach>

                <%-- Nút Next --%>
            <li class="page-item ${!pagination.hasNext() ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link page-next" jw-click="nextPage()"></a>
            </li>
        </ul>
    </nav>
</c:if>
