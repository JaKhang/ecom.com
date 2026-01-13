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
        <ul class="pagination justify-content-start">

                <%-- Nút First (Về trang 1) --%>
            <li class="page-item ${pagination.page == 1 ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link" jw-click="gotoPage(1)" href="#" aria-label="First">
                        <%-- Dùng icon Material Design: first_page --%>
                    <i class="material-icons md-first_page"></i>
                        <%-- Hoặc nếu muốn dùng text thuần: &laquo; --%>
                </a>
            </li>

                <%-- Nút Previous --%>
            <li class="page-item ${!pagination.hasPrevious() ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link" jw-click="previousPage()" href="#" aria-label="Previous">
                    <i class="material-icons md-chevron_left"></i>
                </a>
            </li>

                <%-- Danh sách các trang --%>
            <c:forEach items="${pagination.getVisiblePages(visible)}" var="pNum">
                <li class="page-item ${pagination.page == pNum ? 'active' : ''}">
                    <a @click="scrollToTop()" class="page-link" jw-click="gotoPage(${pNum})" href="#">
                            ${pNum < 10 ? '0' : ''}${pNum}
                    </a>
                </li>
            </c:forEach>

                <%-- Nút Next --%>
            <li class="page-item ${!pagination.hasNext() ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link" jw-click="nextPage()" href="#" aria-label="Next">
                    <i class="material-icons md-chevron_right"></i>
                </a>
            </li>

                <%-- Nút Last (Về trang cuối) --%>
            <li class="page-item ${pagination.page == pagination.totalPages ? 'disabled' : ''}">
                <a @click="scrollToTop()" class="page-link" jw-click="gotoPage(${pagination.totalPages})" href="#" aria-label="Last">
                        <%-- Dùng icon Material Design: last_page --%>
                    <i class="material-icons md-last_page"></i>
                        <%-- Hoặc nếu muốn dùng text thuần: &raquo; --%>
                </a>
            </li>

        </ul>
    </nav>
</c:if>
