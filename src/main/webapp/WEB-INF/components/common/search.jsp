<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.SearchComponent"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<fmt:message key="global.image.thumbnail" var="defaultThumbnail"/>
<fmt:message key="routes.details" var="detailsPath"/>

<div class="box-header-search" id="box-header-search">
    <form id="form-search" class="form-search position-relative" method="post" action="<c:url value='/shop'/>">
        <div class="box-category" jw-ignore>
            <select name="categoryId" class="select-active select2-hidden-accessible" onchange="JWireInstance.dispatch('${component.id}', 'setSelectedCategory', this.value);">
                <option value=""><fmt:message key="header.search.category.all"/></option>
                <c:forEach items="${component.categories}" var="cat">

                    <option value="${cat.id}" ${component.selectedCategory == cat.id ? 'sefflected' : ''}>
                            ${cat.name}
                    </option>
                </c:forEach>

            </select>
        </div>
        <div class="box-keysearch">
            <input class="form-control font-xs"
                   jw-debounce="500"
                   type="text"
                   name="keyword"
                   value="${component.keyword}"
                   jw-model="keyword"
                   autocomplete="off"
                   placeholder="<fmt:message key='header.search.placeholder'/>">
        </div>
        <div class="search-suggestions shadow-sm"
             style="display: ${not empty component.keyword ? 'block' : 'none'};">

            <!-- Header: Kết quả cho "keyword" -->
            <div class="suggestion-header font-xs text-muted mb-2">
                <fmt:message key="shop.search.results_for"/> <strong>"${component.keyword}"</strong>
            </div>

            <c:choose>
                <%-- TRƯỜNG HỢP 1: CÓ SẢN PHẨM --%>
                <c:when test="${fn:length(component.simpleProducts) > 0}">
                    <ul class="list-unstyled mb-0">
                        <c:forEach items="${component.simpleProducts}" var="product">
                            <li>
                                <a href="<c:url value='/${detailsPath}/${product.slug}'/>" class="d-flex align-items-center">
                                    <img src="${not empty product.thumbnail ? product.thumbnail : defaultThumbnail}"
                                         alt="${product.name}"
                                         class="product-thumb">
                                    <div class="product-info">
                                        <span class="product-name font-sm-bold color-brand-3">${product.name}</span>
                                        <span class="product-price font-xs color-brand-1">
                                    <fmt:formatNumber value="${product.minPrice}" type="currency" currencySymbol="đ"/>
                                </span>
                                    </div>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>

                    <!-- Footer: Xem tất cả -->
                    <div class="suggestion-footer">
                        <button type="submit" class="btn-view-more font-sm-bold">
                            <fmt:message key="shop.search.btn_view_all"/> <i class="fi-rr-arrow-small-right"></i>
                        </button>
                    </div>
                </c:when>

                <%-- TRƯỜNG HỢP 2: KHÔNG CÓ KẾT QUẢ --%>
                <c:otherwise>
                    <div class="no-results text-center" style="padding: 20px 0;">
                        <p class="font-sm text-muted mb-2">
                            <i class="fi-rr-search-alt" style="font-size: 20px; display: block; margin-bottom: 5px;"></i>
                            <!-- Text: Không tìm thấy kết quả -->
                            <fmt:message key="shop.search.no_results"/>
                        </p>
                    </div>

                    <!-- Footer: Đến trang cửa hàng -->
                    <div class="suggestion-footer">
                        <button type="submit" class="btn-view-more font-sm-bold">
                            <!-- Text: Đến trang cửa hàng -->
                            <fmt:message key="shop.search.go_to_shop"/> <i class="fi-rr-arrow-small-right"></i>
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </form>
</div>

