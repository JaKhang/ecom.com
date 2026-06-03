<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.ProductDetailsComponent"/>
<c:set var="product" value="${component.product}"/>
<fmt:message key="fallback.image" var="fallbackImage"/>
<fmt:message key="cart.add.success" var="msgSuccess"/>
<fmt:message key="cart.add.error" var="msgError"/>

<div class="container" id="pview-container" jw-childOnly
     x-data="{
        addToCart(variantId, quantity) {
            JWireInstance.dispatch('cart', 'addOrUpdate', variantId, quantity)
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
    }"
>
    <div class="row">
        <!-- ================================================= -->
        <!-- 1. LEFT COLUMN: IMAGES (col-lg-5)                 -->
        <!-- ================================================= -->
        <div class="col-lg-5" jw-ignore="">
            <div class="gallery-image">
                <div class="galleries">
                    <div class="detail-gallery">
                        <!-- Label giảm giá -->
                        <c:choose>
                            <%-- Case 1: Có Variant -> Tính % theo Variant --%>
                            <c:when test="${component.selectedVariant != null && component.selectedVariant.originalPrice > component.selectedVariant.price}">
                                <c:set var="percent"
                                       value="${(component.selectedVariant.originalPrice - component.selectedVariant.price) / component.selectedVariant.originalPrice * 100}"/>
                                <label class="label">-<fmt:formatNumber value="${percent}"
                                                                        maxFractionDigits="0"/>%</label>
                            </c:when>
                            <%-- Case 2: Null -> Tính % theo Product (Max - Min) hoặc logic riêng --%>
                            <c:when test="${component.selectedVariant == null && product.maxPrice > product.minPrice}">
                                <c:set var="percent"
                                       value="${(product.maxPrice - product.minPrice) / product.maxPrice * 100}"/>
                                <label class="label">-<fmt:formatNumber value="${percent}"
                                                                        maxFractionDigits="0"/>%</label>
                            </c:when>
                        </c:choose>

                        <div class="product-image-slider">
                            <c:forEach var="image" items="${component.gallery}">
                                <figure class="border-radius-10"><img
                                        src="${image.url}"
                                        alt="${image.altText}"></figure>
                            </c:forEach>

                            <c:if test="${fn:length(component.gallery) == 0}">
                                <figure class="border-radius-10"><img
                                        src="${fallbackImage}"
                                        alt="<fmt:message key="details.image.comingSoon"/>"/></figure>

                            </c:if>
                        </div>
                    </div>
                    <div class="slider-nav-thumbnails">
                        <c:forEach var="image" items="${component.gallery}">
                            <div>
                                <div class="item-thumb">
                                    <img
                                            src="${image.url}"
                                            alt="${image.altText}"/>
                                </div>
                            </div>

                        </c:forEach>
                        <c:if test="${fn:length(component.gallery) == 0}">
                            <div>
                                <div class="item-thumb">
                                    <img
                                            src="${fallbackImage}"
                                            alt="<fmt:message key="details.image.comingSoon"/>"/>
                                </div>
                            </div>
                        </c:if>

                    </div>
                </div>
            </div>
        </div>

        <!-- ================================================= -->
        <!-- 2. RIGHT COLUMN: INFO (col-lg-7)                  -->
        <!-- ================================================= -->
        <div class="col-lg-7">
            <h3 class="color-brand-3 mb-25">
                <c:choose>
                    <c:when test="${component.selectedVariant != null}">
                        ${component.selectedVariant.name}
                    </c:when>
                    <c:otherwise>
                        ${product.name}
                    </c:otherwise>
                </c:choose>
            </h3>

            <div class="row align-items-center">
                <div class="col-lg-4 col-md-4 col-sm-3 mb-mobile">
                    <span class="bytext color-gray-500 font-xs font-medium"><fmt:message key="details.label.by"/></span>
                    <a class="byAUthor color-gray-900 font-xs font-medium" href="#"> ${product.brand.name}</a>
                    <div class="rating mt-5">
                        <c:set var="avg" value="${product.extras['ratingAvg']}"/>

                        <c:forEach var="i" begin="1" end="5">
                            <c:choose>
                                <c:when test="${avg >= i}">
                                    <img src="<c:url value='/static/client/imgs/template/icons/star.svg'/>"
                                         alt="<fmt:message key="details.rating.full"/>">
                                </c:when>

                                <c:when test="${avg >= i - 0.5}">
                                    <img src="<c:url value='/static/client/imgs/template/icons/star-half.svg'/>"
                                         alt="<fmt:message key="details.rating.half"/>">
                                </c:when>

                                <c:otherwise>
                                    <img src="<c:url value='/static/client/imgs/template/icons/star-gray.svg'/>"
                                         alt="<fmt:message key="details.rating.empty"/>">
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <span class="font-xs color-gray-500 font-medium"> (${product.extras["reviewsCount"]} <fmt:message
                                key="details.label.reviews"/>)</span>
                    </div>
                </div>
                <div class="col-lg-8 col-md-8 col-sm-9 text-start text-sm-end">
                    <a class="mr-20" href="#"><span
                            class="btn btn-wishlist mr-5 opacity-100 transform-none"></span><span
                            class="font-md color-gray-900"><fmt:message key="details.button.addWishlist"/></span></a>
                    <a href="#"><span class="btn btn-compare mr-5 opacity-100 transform-none"></span><span
                            class="font-md color-gray-900"><fmt:message key="details.button.addCompare"/></span></a>
                </div>
            </div>

            <div class="border-bottom pt-10 mb-20"></div>

            <!-- PRICE -->
            <div class="box-product-price">
                <h3 class="color-brand-3 price-main d-inline-block mr-10">
                    <c:choose>
                        <c:when test="${component.selectedVariant != null}">
                            <fmt:formatNumber value="${component.selectedVariant.price}" type="currency"/>
                        </c:when>
                        <c:otherwise>
                            <%-- Fallback: Hiện giá thấp nhất của Product --%>
                            <fmt:formatNumber value="${product.minPrice}" type="currency"/>
                        </c:otherwise>
                    </c:choose>
                </h3>

                <c:choose>
                    <c:when test="${component.selectedVariant != null}">
                        <c:if test="${component.selectedVariant.originalPrice > component.selectedVariant.price}">
                            <span class="color-gray-500 price-line font-xl line-througt">
                                <fmt:formatNumber value="${component.selectedVariant.originalPrice}" type="currency"/>
                            </span>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <%-- Fallback: Nếu Max > Min thì hiện Max gạch ngang --%>
                        <c:if test="${product.maxPrice > product.minPrice}">
                            <span class="color-gray-500 price-line font-xl line-througt">
                                <fmt:formatNumber value="${product.maxPrice}" type="currency"/>
                            </span>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="product-description mt-20 color-gray-900">
                <div class="row">
                    <div class="col-lg-6 col-md-6 col-sm-12">
                        <ul class="list-dot">
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
                    <div class="col-lg-6 col-md-6 col-sm-12">

                    </div>
                </div>
            </div>

            <!-- ================================================= -->
            <!-- DYNAMIC ATTRIBUTES SECTION                        -->
            <!-- ================================================= -->

            <!-- 1. RENDER COLOR / IMAGE ATTRIBUTES -->
            <c:forEach var="attr" items="${component.attributes}">
                <c:set var="selectedLabel" value=""/>
                <c:set var="selectedId" value="${component.selectedOptions[attr.id.toString()]}"/>
                <div class=" mt-10">
                    <p class="font-sm color-gray-900">${attr.label}:<span
                            class="color-brand-2 nameColor">${selectedLabel}</span></p>

                    <c:choose>
                        <c:when test="${attr.visualType == 'image'}">
                            <ul class="list-colors">
                                <c:forEach var="opt" items="${attr.options}">
                                    <li jw-click="choose('${attr.id}', '${opt.id}')"
                                        class="${selectedId == opt.id.toString() ? "active": ""}">
                                        <img src="${opt.visualValue}" alt="${opt.label}"
                                             title="${opt.label}">
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:when test="${attr.visualType == 'color'}">
                            <ul class="list-colors">
                                <c:forEach var="opt" items="${attr.options}">
                                    <li>
                                        <button style="background-color: ${opt.visualValue}">

                                        </button>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <ul class="list-styles">
                                <c:forEach var="opt" items="${attr.options}">
                                    <li title="${opt.label}" jw-click="choose('${attr.id}', '${opt.id}')"
                                        class="${selectedId == opt.id.toString() ? "active": ""} ${component.disableOptions.contains(opt.id.toString()) ? "disabled": ""}">${opt.label}</li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>

                    </c:choose>
                </div>

            </c:forEach>


            <!-- ACTIONS -->
            <div class="buy-product mt-30">
                <p class="font-sm mb-20"><fmt:message key="details.label.quantity"/></p>
                <div class="box-quantity">
                    <div class="input-quantity">
                        <input class="font-xl color-brand-3" id="input-quantity" type="text" value="1"><span
                            class="minus-cart"></span><span class="plus-cart"></span>
                    </div>
                    <c:set var="canBuy"
                           value="${fn:length(component.selectedOptions) == fn:length(component.attributes)}"/>
                    <div class="button-buy">
                        <button @click="addToCart('${component.selectedVariant != null ? component.selectedVariant.id : ''}',  $('#input-quantity').val())"
                                class="btn btn-cart" ${ canBuy ? '' : 'disabled'}><fmt:message
                                key="details.button.addToCart"/></button>
                        <button class="btn btn-buy" ${ canBuy ? '' : 'disabled'}><fmt:message
                                key="details.button.buyNow"/></button>
                    </div>

                </div>
            </div>

            <!-- FOOTER INFO -->
            <div class="info-product mt-40">
                <div class="row align-items-end">
                    <div class="col-lg-4 col-md-4 mb-20">
                                <span class="font-sm font-medium color-gray-900">
                                    <fmt:message key="details.label.sku"/>:<span class="color-gray-500">
                                        <c:choose>
                                            <c:when test="${component.selectedVariant != null}">
                                                ${component.selectedVariant.sku}
                                            </c:when>
                                            <c:otherwise>
                                                ${product.slug} <%-- Fallback SKU --%>
                                            </c:otherwise>
                                        </c:choose>
                                    </span><br>
                                    <span jw-ignore>
                                        <fmt:message key="details.label.category"/>:
                                        <c:if test="${categories != null}">
                                            <c:forEach var="cas" items="${categories}">
                                                <span class="color-gray-500">${cas.name}</span>
                                            </c:forEach>
                                        </c:if></span>
                                    <br>
                                </span>
                    </div>
                    <div class="col-lg-4 col-md-4 mb-20">
                                <span class="font-sm font-medium color-gray-900"><fmt:message
                                        key="details.label.freeDelivery"/><br>
                                    <span class="color-gray-500"><fmt:message
                                            key="details.message.availableAllLocations"/></span><br>
                                    <span class="color-gray-500"><fmt:message
                                            key="details.link.deliveryOptions"/></span>
                                </span>
                    </div>
                    <div class="col-lg-4 col-md-4 mb-20 text-start text-md-end">
                        <div class="d-inline-block">
                            <div class="share-link">
                                <span class="font-md-bold color-brand-3 mr-15"><fmt:message
                                        key="details.label.share"/></span>
                                <a class="facebook hover-up" href="#"></a>
                                <a class="printest hover-up" href="#"></a>
                                <a class="twitter hover-up" href="#"></a>
                                <a class="instagram hover-up" href="#"></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
