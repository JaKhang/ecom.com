<%@ tag language="java" pageEncoding="UTF-8" %>
<!-- Lưu ý: type phải trỏ đúng package chứa SimpleProduct -->
<%@ attribute name="product" required="true" type="com.nlu.store.modules.catalog.model.SimpleProduct" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="routes.details" var="detailsPath"/>
<fmt:message key="shop.product.btn.add_cart" var="addToCart"/>
<fmt:message key="cart.add.success" var="msgSuccess"/>
<fmt:message key="cart.add.error" var="msgError"/>

<div class="card-grid-style-3" x-data="{
        addDefault(productId) {
            JWireInstance.dispatch('cart', 'addDefault', productId)
                .then(() => {
                    Toastify({
                        text: '${msgSuccess}',
                        className: 'bg-success',
                        style: {
                            background: 'unset'
                        },
                        duration: 3000
                    }).showToast()
                 })
                .catch((e) => {
                    Toastify({
                        text: e.message || '${msgError}',
                        className: 'bg-danger',
                        style: {
                            background: 'unset'
                        },
                        duration: 3000
                    }).showToast()
                })
        }
    }">
    <div class="card-grid-inner">

        <!-- TOOLS -->
        <div class="tools">
            <!-- 1. Trend (Xu hướng) -->
            <c:if test="${product.featured}">
                <a class="btn btn-trend btn-tooltip mb-10" href="#"
                   aria-label="<fmt:message key='shop.tooltip.trend'/>"></a>
            </c:if>

            <!-- 2. Wishlist (Yêu thích) -->
            <a class="btn btn-wishlist btn-tooltip mb-10"
               href="<c:url value='/wishlist/add?id=${product.id}'/>"
               aria-label="<fmt:message key='shop.tooltip.wishlist'/>"></a>

            <!-- 3. Compare (So sánh) -->
            <a class="btn btn-compare btn-tooltip mb-10"
               href="<c:url value='/compare/add?id=${product.id}'/>"
               aria-label="<fmt:message key='shop.tooltip.compare'/>"></a>

            <!-- 4. Quickview (Xem nhanh) -->
            <a class="btn btn-quickview btn-tooltip" href="#ModalQuickview" data-bs-toggle="modal"
               data-id="${product.id}"
               aria-label="<fmt:message key='shop.tooltip.quickview'/>"></a>
        </div>


        <!-- IMAGE -->
        <div class="image-box">
            <c:if test="${product.discountPercentage > 0}">
                <span class="label bg-brand-2">-${product.discountPercentage}%</span>
            </c:if>

            <a href="<c:url value='/${detailsPath}/${product.slug}'/>">
                <img src="<c:url value='${product.thumbnail}'/>" alt="${product.name}">
            </a>
        </div>

        <!-- INFO -->
        <div class="info-right">
            <c:choose>
                <%-- Trường hợp CÓ Brand --%>
                <c:when test="${not empty product.brand}">
                    <a class="font-xs color-gray-500" href="<c:url value='/brand/${product.brand.slug}'/>">
                            ${product.brand.name}
                    </a>
                </c:when>
                <%-- Trường hợp KHÔNG CÓ Brand --%>
                <c:otherwise>
                        <span class="font-xs color-gray-500">
                            No Brand
                        </span>
                </c:otherwise>
            </c:choose>
            <br/>
            <a class="color-brand-3 font-sm-bold" href="<c:url value='/${detailsPath}/${product.slug}'/>">
                ${product.name}
            </a>

            <div class="rating">
                <c:forEach begin="1" end="5" var="i">
                    <img src="<c:url value='/static/client/imgs/template/icons/star.svg'/>"
                         class="${i <= product.extras['ratingAvg'] ? '' : 'opacity-50'}">
                </c:forEach>
                <span class="font-xs color-gray-500">(<c:out default="0"
                                                             value="${product.extras['reviewsCount']}"/>)</span>
            </div>

            <div class="price-info">
                <strong class="font-lg-bold color-brand-3 price-main">
                    <fmt:formatNumber value="${product.minPrice}" type="currency" currencySymbol="đ"/>
                </strong>

                <c:if test="${product.discountPercentage > 0}">
                        <span class="color-gray-500 price-line">
                            <fmt:formatNumber value="${product.maxPrice}" type="currency" currencySymbol="đ"/>
                        </span>
                </c:if>
            </div>

            <div class="mt-20 box-btn-cart">
                <button class="btn btn-cart" @click="addDefault('${product.id}')">${addToCart}</button>
            </div>

            <ul class="list-features pl-5">
                <c:forEach items="${product.specsSnapshot}" var="entry" varStatus="status">
                    <c:if test="${status.count <= 3}">
                        <li style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; list-style-position: inside; display: list-item;"
                            title="${entry.key}: ${entry.value}">
                                ${entry.key}: ${entry.value}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
