<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.ShopGridComponent"/>

<div class="row" id="shop-grid-row">
    <!-- === SIDEBAR (LEFT) === -->
    <div class="col-lg-3 order-last order-lg-first ">
        <fmt:message key="routes.category" var="pathCategory"/>
        <c:set var="baseCatPath" value="${not empty pathCategory ? pathCategory : 'category'}"/>

        <!-- Categories Widget -->
        <div class="sidebar-border mb-0" jw-ignore>
            <div class="sidebar-head">
                <h6 class="color-gray-900">
                    <fmt:message key="shop.sidebar.categories" var="lblCategories"/>
                    ${not empty lblCategories ? lblCategories : 'Product Categories'}
                </h6>
            </div>
            <div class="sidebar-content">
                <ul class="list-nav-arrow">
                    <c:forEach items="${component.categories}" var="cat" varStatus="status" end="9">
                        <li class="${cat.id == component.selectedCategory.id ? 'active' : ''}">
                            <a href="<c:url value='/${baseCatPath}/${cat.slug}'/>">
                                    ${cat.name}
                                <span class="number">${cat.productCount}</span>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
                <c:if test="${fn:length(component.categories) > 10}">
                    <div>
                        <div class="collapse" id="moreMenu">
                            <ul class="list-nav-arrow">
                                <c:forEach items="${component.categories}" var="cat" begin="10">
                                    <li class="${cat.id == component.selectedCategory.id ? 'active' : ''}">
                                        <a href="<c:url value='/${baseCatPath}/${cat.slug}'/>">
                                                ${cat.name}
                                            <span class="number">${cat.productCount}</span>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                        <a class="link-see-more mt-5" data-bs-toggle="collapse" href="#moreMenu" role="button"
                           aria-expanded="false" aria-controls="moreMenu">
                            <fmt:message key="shop.sidebar.see_more" var="lblSeeMore"/>
                                ${not empty lblSeeMore ? lblSeeMore : 'See More'}
                        </a>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Brands Filter -->
        <div class="sidebar-border mb-40">
            <div class="sidebar-head">
                <h6 class="color-gray-900"><fmt:message key="shop.sidebar.filter.title"/></h6>
            </div>
            <div class="sidebar-content">
                <h6 class="color-gray-900 mt-20 mb-10"><fmt:message key="shop.sidebar.filter.brands"/></h6>
                <ul class="list-checkbox">
                    <c:forEach items="${component.brands}" var="brand">
                        <li>
                            <label class="cb-container">
                                <input type="checkbox" name="selectedBrandIds"
                                       autocomplete="false"
                                       onchange="console.log('changed')"
                                       value="${brand.id}"
                                       jw-model="selectedBrandIds"
                                       <c:if test="${fn:contains(component.selectedBrandIds, brand.id)}">checked</c:if>>
                                <span class="checkmark"></span>
                                <span class="text-small">${brand.name}</span>
                            </label>
                            <span class="number-item"><c:out value="${brand.extras['productsCount']}"
                                                             default="0"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <!-- Banner Left (Đã thay text cứng bằng message) -->
        <div class="banner-right h-500 text-center mb-30">
            <span class="text-no font-11">
                <fmt:message key="shop.sidebar.banner.badge"/>
            </span>
            <h5 class="font-23 mt-20">
                <fmt:message key="shop.sidebar.banner.title_1"/>
                <br class="d-none d-lg-block">
                <fmt:message key="shop.sidebar.banner.title_2"/>
            </h5>
            <p class="text-desc font-16 mt-15">
                <fmt:message key="shop.sidebar.banner.desc"/>
            </p>
            <a href="#">
                <fmt:message key="shop.sidebar.categories.see_more"/>
            </a>
        </div>
    </div>

    <!-- === MAIN CONTENT (RIGHT) === -->
    <div class="col-lg-9 order-first order-lg-last">
        <!-- Banner Top -->
        <div class="banner-top-gray-100" id="scroll-target">
            <div class="banner-ads-top mb-30">
                <a href="#"><img src="<c:url value="/static/client/imgs/page/shop/grid-2/banner.png"/>" alt="Ecom"></a>
            </div>
        </div>

        <!-- === 1. HIỂN THỊ KEYWORD TÌM KIẾM === -->
        <c:if test="${not empty param.keyword}">
            <div class="d-flex align-items-center mb-20 mt-20">
                <span class="font-md color-gray-500 mr-10"><fmt:message key="shop.search.results_for"/></span>
                <h5 class="color-brand-3 mb-0 mr-15">
                    &quot;<c:out value="${param.keyword}"/>&quot;
                </h5>
                <a href="?sort=${component.sort}&page=1"
                   class="btn btn-sm btn-light rounded-pill d-flex align-items-center px-3"
                   style="height: 30px; line-height: 1;">
                    <span class="font-lg mr-5">&times;</span> <fmt:message key="shop.search.btn_clear"/>
                </a>
            </div>
        </c:if>

        <!-- === 2. HIỂN THỊ SELECTED CATEGORY (Đã thay text cứng bằng message) === -->
        <c:if test="${not empty component.selectedCategory and empty param.keyword}">
            <div class="d-flex align-items-center mb-20 mt-20">
                <h5 class="color-brand-3 mb-0 mr-15">
                        ${component.selectedCategory.name}
                </h5>
                <span class="font-md color-gray-500 font-medium mr-15">
                    (${component.selectedCategory.productCount} <fmt:message key="shop.category.product_count_label"/>)
                </span>

                    <%-- Nút Xóa Category --%>
                <fmt:message key="routes.shop" var="pathShop"/>
                <c:set var="baseShopPath" value="${not empty pathShop ? pathShop : 'shop'}"/>

                <a href="<c:url value='/${baseShopPath}'/>"
                   class="btn btn-sm btn-light rounded-pill d-flex align-items-center px-3"
                   style="height: 30px; line-height: 1;">
                    <span class="font-lg mr-5">&times;</span> <fmt:message key="shop.search.btn_clear"/>
                </a>
            </div>
        </c:if>

        <!-- Toolbar -->
        <div class="box-filters mt-0 pb-5 border-bottom" id="tool-bar">
            <div class="row">
                <div class="col-xl-2 col-lg-3 mb-10 text-lg-start text-center">
                    <a class="btn btn-filter font-sm color-brand-3 font-medium" href="#ModalFiltersForm"
                       data-bs-toggle="modal">
                        <fmt:message key="shop.toolbar.btn_all_filters"/>
                    </a>
                </div>
                <div class="col-xl-10 col-lg-9 mb-10 text-lg-end text-center">
                    <span class="font-sm color-gray-900 font-medium border-1-right span" style="--before-height: 28px;">
                        <fmt:message key="shop.toolbar.showing_results">
                            <fmt:param value="${component.offset + 1}"/>
                            <fmt:param value="${component.offset + fn:length(component.data)}"/>
                            <fmt:param value="${component.totalItems}"/>
                        </fmt:message>
                    </span>

                    <div class="d-inline-block">
                        <span class="font-sm color-gray-500 font-medium"><fmt:message
                                key="shop.toolbar.sort_by"/></span>
                        <div class="d-inline-block border-1-right" style="--before-height: 28px;">
                            <select class="form-select form-select-sm font-sm color-gray-900 font-medium border-0 shadow-none"
                                    style="width: auto; cursor: pointer; padding-right: 30px;"
                                    jw-model="sort">
                                <option value="release_date:desc" ${component.sort == 'release_date:desc' ? 'selected' : ''}>
                                    <fmt:message key="shop.toolbar.sort.latest"/>
                                </option>
                                <option value="min_price:asc" ${component.sort == 'min_price:asc' ? 'selected' : ''}>
                                    <fmt:message key="shop.toolbar.sort.price_asc"/>
                                </option>
                                <option value="min_price:desc" ${component.sort == 'min_price:desc' ? 'selected' : ''}>
                                    <fmt:message key="shop.toolbar.sort.price_desc"/>
                                </option>
                            </select>
                        </div>
                    </div>
                    <div class="d-inline-block">
                        <a class="view-type-grid mr-5 active" href="shop-grid.html"></a><a
                            class="view-type-list" href="shop-list.html"></a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Product Grid -->
        <div class="row mt-20" style="transition: .5s">
            <c:forEach items="${component.data}" var="product">
                <pro:product-card-grid product="${product}"/>
            </c:forEach>

            <c:if test="${empty component.data}">
                <div class="col-12 text-center mt-50">
                    <p class="font-md color-gray-500"><fmt:message key="shop.grid.no_products"/></p>
                </div>
            </c:if>
        </div>

        <!-- Pagination -->
        <%--        <nav x-data="{ scrollToTop() { document.getElementById('scroll-target').scrollIntoView({ behavior: 'smooth' }); } }">--%>
        <%--            <ul class="pagination">--%>
        <%--                <li class="page-item ${!component.hasPrevious() ? 'disabled' : ''}">--%>
        <%--                    <button @click="scrollToTop()" class="page-link page-prev" jw-click="previousPage()"></button>--%>
        <%--                </li>--%>

        <%--                <c:forEach items="${component.getVisiblePages(5)}" var="pNum">--%>
        <%--                    <li class="page-item ${component.page == pNum ? 'active' : ''}">--%>
        <%--                        <button @click="scrollToTop()" class="page-link" jw-click="gotoPage(${pNum})">${pNum}</button>--%>
        <%--                    </li>--%>
        <%--                </c:forEach>--%>

        <%--                <li class="page-item ${!component.hasNext() ? 'disabled' : ''}">--%>
        <%--                    <button @click="scrollToTop()" class="page-link page-next" jw-click="nextPage()"></button>--%>
        <%--                </li>--%>
        <%--            </ul>--%>
        <%--        </nav>--%>

        <t:pagination pagination="${component}" scrollTarget="scroll-target"/>
    </div>
</div>
