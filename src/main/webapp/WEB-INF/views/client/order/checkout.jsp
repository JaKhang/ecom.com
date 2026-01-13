<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<jsp:useBean id="cart" scope="request" type="com.nlu.store.modules.catalog.model.cart.Cart"/>
<jsp:useBean id="form" scope="request" type="com.nlu.store.modules.order.dto.CheckoutRequest"/>
<jsp:useBean id="errors" scope="request" type="java.util.Map"/>
<jsp:useBean id="isChanged" scope="request" type="java.lang.Boolean"/>

<fmt:message var="title" key="checkout.title"/>
<fmt:message var="checkoutPath" key="routes.checkout"/>
<fmt:message var="shopPath" key="routes.shop"/>
<fmt:message var="cartPath" key="routes.cart"/>
<fmt:message var="detailsPath" key="routes.details"/>
<c:set var="cartSummary" value="${cart.summary}"/>

<t:layout title="${title}">
    <!-- 1. BREADCRUMBS -->
    <div class="section-box">
        <div class="breadcrumbs-div">
            <div class="container">
                <ul class="breadcrumb">
                    <li>
                        <a class="font-xs color-gray-1000" href="<c:url value='/'/>">
                            <fmt:message key="menu.home"/>
                        </a>
                    </li>
                    <fmt:message key="routes.shop" var="pathShop"/>
                    <c:set var="baseShopPath" value="${not empty pathShop ? pathShop : 'shop'}"/>
                    <li>
                        <a class="font-xs color-gray-500" href="<c:url value='/${baseShopPath}'/>">
                            <fmt:message key="shop.breadcrumb.shop"/>
                        </a>
                    </li>
                    <li>
                        <a class="font-xs color-gray-500" href="#"> ${title} </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <section class="section-box shop-template">
        <div class="container">

            <c:choose>

                <c:when test="${cart == null || fn:length(cart.items) == 0}">
                    <div class="row">
                        <div class="col-12 text-center py-5">
                            <div class="mb-30">
                                <!-- Optional: Empty cart icon -->
                                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png"
                                     alt="Empty Cart"
                                     style="width: 120px; opacity: 0.5;">
                            </div>
                            <h3 class="font-md-bold color-brand-3 mb-20">
                                <fmt:message key="cart.empty.message"/>
                            </h3>
                            <p class="font-sm color-gray-500 mb-30">
                                <fmt:message key="cart.empty.instruction"/>
                            </p>
                            <a class="btn btn-buy w-auto" href="<c:url value='/${shopPath}'/>">
                                <fmt:message key="cart.action.continue"/>
                            </a>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="row">

                        <!-- LEFT COLUMN: CHECKOUT FORM -->
                        <div class="col-lg-6">
                            <form action="<c:url value='/${checkoutPath}'/>" method="POST">

                                <!-- Initialize AlpineJS with server-side data for repopulation -->
                                <div class="box-border"
                                     x-data="addressDropdown({
                                        province: '${fn:escapeXml(form.province)}',
                                        district: '${fn:escapeXml(form.district)}',
                                        ward: '${fn:escapeXml(form.ward)}'
                                     })">

                                    <div class="row">
                                        <!-- SECTION: CONTACT INFORMATION -->
                                        <div class="col-lg-6 col-sm-6 mb-20">
                                            <h5 class="font-md-bold color-brand-3 text-sm-start text-center">
                                                <fmt:message key="checkout.contact.title"/>
                                            </h5>
                                        </div>
                                        <div class="col-lg-6 col-sm-6 mb-20 text-sm-end text-center">
                                            <span class="font-sm color-brand-3"><fmt:message
                                                    key="checkout.login.question"/></span>
                                            <a class="font-sm color-brand-1" href="<c:url value='/login'/>">
                                                <fmt:message key="checkout.login.action"/>
                                            </a>
                                        </div>

                                        <!-- Email Field -->
                                        <div class="col-lg-12">
                                            <div class="form-group">
                                                <fmt:message key="checkout.form.email" var="phEmail"/>
                                                <input class="form-control font-sm ${not empty errors['email'] ? 'is-invalid' : ''}"
                                                       type="email"
                                                       name="email"
                                                       value="${form.email}"
                                                       placeholder="${phEmail} *" required>
                                                <c:if test="${not empty errors['email']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['email']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- Phone Field -->
                                        <div class="col-lg-12">
                                            <div class="form-group">
                                                <fmt:message key="checkout.form.phone" var="phPhone"/>
                                                <input class="form-control font-sm ${not empty errors['phoneNumber'] ? 'is-invalid' : ''}"
                                                       type="tel"
                                                       name="phoneNumber"
                                                       value="${form.phoneNumber}"
                                                       placeholder="${phPhone} *" required>
                                                <c:if test="${not empty errors['phoneNumber']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['phoneNumber']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- Subscribe Checkbox -->
                                        <div class="col-lg-12">
                                            <div class="form-group">
                                                <label class="font-sm color-brand-3" for="checkboxOffers">
                                                    <input class="checkboxOffer" id="checkboxOffers" type="checkbox"
                                                           name="subscribe">
                                                    <fmt:message key="checkout.form.subscribe"/>
                                                </label>
                                            </div>
                                        </div>

                                        <!-- SECTION: SHIPPING ADDRESS -->
                                        <div class="col-lg-12">
                                            <h5 class="font-md-bold color-brand-3 mt-15 mb-20">
                                                <fmt:message key="checkout.address.title"/>
                                            </h5>
                                        </div>

                                        <!-- First Name -->
                                        <div class="col-lg-12">
                                            <div class="form-group">
                                                <fmt:message key="checkout.form.fullname" var="phFName"/>
                                                <input class="form-control font-sm ${not empty errors['fullName'] ? 'is-invalid' : ''}"
                                                       type="text"
                                                       name="fullName"
                                                       value="${form.fullName}"
                                                       placeholder="${phFName} *" required>
                                                <c:if test="${not empty errors['fullName']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['fullName']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>



                                        <!-- Province / City -->
                                        <div class="col-lg-4">
                                            <div class="form-group autocomplete-wrapper"
                                                 @click.outside="provinceListShown = false">
                                                <fmt:message key="checkout.address.select_province" var="phProv"/>

                                                <!-- Visible Input (Search) -->
                                                <input type="text"
                                                       class="form-control font-sm input-autocomplete ${not empty errors['province'] ? 'is-invalid' : ''}"
                                                       placeholder="${phProv}"
                                                       x-model.debounce.300ms="provinceSearch"
                                                       @focus="startSearchingProvince">

                                                <!-- Hidden Input (Data Submission) -->
                                                <input type="hidden" name="province" :value="selectedProvince?.name"
                                                       value="${form.province}">

                                                <!-- Dropdown List -->
                                                <ul class="autocomplete-list"
                                                    x-show="provinceListShown && filteredProvinces.length">
                                                    <template x-for="(item, idx) in filteredProvinces" :key="idx">
                                                        <li x-html="highlightName(item)"
                                                            @click="selectProvince(item)"></li>
                                                    </template>
                                                </ul>

                                                <c:if test="${not empty errors['province']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['province']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- District -->
                                        <div class="col-lg-4">
                                            <div class="form-group autocomplete-wrapper"
                                                 @click.outside="districtListShown = false">
                                                <fmt:message key="checkout.address.select_district" var="phDist"/>

                                                <input type="text"
                                                       class="form-control font-sm input-autocomplete ${not empty errors['district'] ? 'is-invalid' : ''}"
                                                       placeholder="${phDist}"
                                                       x-model.debounce.300ms="districtSearch"
                                                       @focus="startSearchingDistrict"
                                                       @input="searchDistrictOnTyping"
                                                       :disabled="!selectedProvince">

                                                <input type="hidden" name="district" :value="selectedDistrict?.name"
                                                       value="${form.district}">

                                                <ul class="autocomplete-list"
                                                    x-show="districtListShown && filteredDistricts.length">
                                                    <template x-for="(item, idx) in filteredDistricts" :key="idx">
                                                        <li x-html="highlightName(item)"
                                                            @click="selectDistrict(item)"></li>
                                                    </template>
                                                </ul>

                                                <c:if test="${not empty errors['district']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['district']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- Ward -->
                                        <div class="col-lg-4">
                                            <div class="form-group autocomplete-wrapper"
                                                 @click.outside="wardListShown = false">
                                                <fmt:message key="checkout.address.select_ward" var="phWard"/>

                                                <input type="text"
                                                       class="form-control font-sm input-autocomplete ${not empty errors['ward'] ? 'is-invalid' : ''}"
                                                       placeholder="${phWard}"
                                                       x-model.debounce.300ms="wardSearch"
                                                       @focus="startSearchingWard"
                                                       :disabled="!selectedDistrict">

                                                <input type="hidden" name="ward" :value="selectedWard?.name"
                                                       value="${form.ward}">

                                                <ul class="autocomplete-list"
                                                    x-show="wardListShown && filteredWards.length">
                                                    <template x-for="(item, idx) in filteredWards" :key="idx">
                                                        <li x-html="highlightName(item)" @click="selectWard(item)"></li>
                                                    </template>
                                                </ul>

                                                <c:if test="${not empty errors['ward']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['ward']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- Address Detail -->
                                        <div class="col-lg-12">
                                            <div class="form-group">
                                                <fmt:message key="checkout.address.detail" var="phAddr"/>
                                                <input class="form-control font-sm ${not empty errors['addressDetail'] ? 'is-invalid' : ''}"
                                                       type="text"
                                                       name="addressDetail"
                                                       value="${form.addressDetail}"
                                                       placeholder="${phAddr} *" required>
                                                <c:if test="${not empty errors['addressDetail']}">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['addressDetail']}"/></span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- SECTION: PAYMENT METHOD -->
                                        <div class="col-lg-12">
                                            <h5 class="font-md-bold color-brand-3 mt-15 mb-20">
                                                <fmt:message key="checkout.payment.title"/>
                                            </h5>

                                            <c:if test="${not empty errors['paymentMethod']}">
                                                <div class="mb-10">
                                                    <span class="text-danger font-xs"><fmt:message
                                                            key="${errors['paymentMethod']}"/></span>
                                                </div>
                                            </c:if>

                                            <div class="list-group">
                                                <!-- VNPAY Option -->
                                                <label class="list-group-item d-flex gap-3 align-items-center"
                                                       style="cursor: pointer;">
                                                    <input class="form-check-input flex-shrink-0"
                                                           type="radio"
                                                           name="paymentMethod"
                                                           value="VNPAY"
                                                        ${form.paymentMethod == 'VNPAY' ? 'checked' : ''}>
                                                    <span class="d-flex g-2 py-2 align-items-center">
                                                        <img class="mr-10" style="height: 24px"
                                                             src="https://yt3.googleusercontent.com/JM1m2wng0JQUgSg9ZSEvz7G4Rwo7pYb4QBYip4PAhvGRyf1D_YTbL2DdDjOy0qOXssJPdz2r7Q=s900-c-k-c0x00ffffff-no-rj"/>
                                                        <span>
                                                            <strong class="d-block"><fmt:message
                                                                    key="checkout.payment.banking"/></strong>
                                                            <small class="text-muted"><fmt:message
                                                                    key="checkout.payment.banking_desc"/></small>
                                                        </span>
                                                    </span>
                                                </label>

                                                <!-- MOMO Option -->
                                                <label class="list-group-item d-flex gap-3 align-items-center"
                                                       style="cursor: pointer;">
                                                    <input class="form-check-input flex-shrink-0"
                                                           type="radio"
                                                           name="paymentMethod"
                                                           value="MOMO"
                                                        ${form.paymentMethod == 'MOMO' ? 'checked' : ''}>
                                                    <span class="d-flex g-2 py-2 align-items-center">
                                                        <img class="mr-10" style="height: 24px"
                                                             src="https://developers.momo.vn/v3/vi/assets/images/transparent-background-logo-138ebf0ffca865ec0f1a7d9c1e4a9f3c.png"/>
                                                        <span>
                                                            <strong class="d-block"><fmt:message
                                                                    key="checkout.payment.momo"/></strong>
                                                            <small class="text-muted"><fmt:message
                                                                    key="checkout.payment.momo_desc"/></small>
                                                        </span>
                                                    </span>
                                                </label>
                                            </div>
                                        </div>

                                        <!-- SECTION: NOTE -->
                                        <div class="col-lg-12 mt-20">
                                            <div class="form-group mb-0">
                                                <fmt:message key="checkout.form.note" var="phNote"/>
                                                <textarea class="form-control font-sm" name="note"
                                                          placeholder="${phNote}" rows="5">${form.note}</textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- ACTION BUTTONS -->
                                <div class="row mt-20">
                                    <div class="col-lg-6 col-5 mb-20">
                                        <a class="btn font-sm-bold color-brand-1 arrow-back-1"
                                           href="<c:url value='/cart'/>">
                                            <fmt:message key="checkout.btn.return"/>
                                        </a>
                                    </div>
                                    <div class="col-lg-6 col-7 mb-20 text-end">
                                        <button type="submit" class="btn btn-buy w-auto arrow-next">
                                            <fmt:message key="checkout.btn.place_order"/>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <!-- RIGHT COLUMN: ORDER SUMMARY -->
                        <div class="col-lg-6">
                            <div class="box-border">
                                <h5 class="font-md-bold mb-20">
                                    <fmt:message key="checkout.your_order"/>
                                </h5>
                                <c:if test="${isChanged}">

                                    <div class="d-flex align-items-start mb-20">
                                        <!-- Simple Info Icon (SVG) -->
                                        <svg style="width: 16px; height: 16px; margin-top: 2px;"
                                             class="mr-10 color-gray-500 flex-shrink-0" fill="none"
                                             stroke="currentColor"
                                             viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                  d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                        </svg>
                                        <p class="font-xs color-gray-500">
                                            <fmt:message key="checkout.notice.policy"/>
                                        </p>
                                    </div>
                                </c:if>

                                <!-- Cart Items List -->
                                <div class="listCheckout">
                                    <c:forEach items="${cart.items}" var="item">
                                        <c:if test="${item.inStock}">
                                            <c:url value='/${detailsPath}/${item.slug}' var="productUrl">
                                                <c:param name="variantId" value="${item.variantId}"/>
                                            </c:url>
                                            <div class="item-wishlist">
                                                <div class="wishlist-product w-auto" style="flex: 1">
                                                    <div class="product-wishlist">

                                                        <!-- Product Image -->
                                                        <div class="product-image">
                                                            <a href="${productUrl}">
                                                                <img src="${not empty item.thumbnail ? item.thumbnail : '/assets/imgs/page/${detailsPath}/img-sub.png'}"
                                                                     alt="${item.variantName}">
                                                            </a>
                                                        </div>

                                                        <!-- Product Info -->
                                                        <div class="product-info" style="align-items: unset">
                                                            <a href="<c:url value='${productUrl}'/>">
                                                                <h6 class="color-brand-3">${item.variantName}</h6>
                                                            </a>
                                                            <!-- Rating -->
                                                            <div class="rating">
                                                                <div class="product-rate d-inline-block">
                                                                    <div class="product-rating"
                                                                         style="width: ${item.ratingAvg * 20}%"></div>
                                                                </div>
                                                                <span class="font-xs color-gray-500"> (${item.reviewsCount})</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Quantity -->
                                                <div class="wishlist-status">
                                                    <h5 class="color-gray-500">x${item.quantity}</h5>
                                                </div>

                                                <!-- Total Price -->
                                                <div class="wishlist-price w-auto">
                                                    <h6 class="color-brand-3 ">
                                                        <fmt:formatNumber value="${item.total}" type="currency"
                                                                          currencySymbol="đ"/>
                                                    </h6>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <!-- Order Totals -->
                                <div class="form-group mb-0">
                                    <!-- Subtotal -->
                                    <div class="row mb-10">
                                        <div class="col-lg-6 col-6">
                                            <span class="font-md-bold color-brand-3">
                                                <fmt:message key="cart.summary.subtotal"/>
                                            </span>
                                        </div>
                                        <div class="col-lg-6 col-6 text-end">
                                            <span class=" color-brand-3">
                                                <fmt:formatNumber value="${cartSummary.subtotal}" type="currency"
                                                                  currencySymbol="đ"/>
                                            </span>
                                        </div>
                                    </div>

                                    <!-- Discount -->
                                    <div class="row mb-10">
                                        <div class="col-lg-6 col-6">
                                            <span class="font-md-bold color-brand-3">
                                                <fmt:message key="cart.summary.discount"/>
                                            </span>
                                        </div>
                                        <div class="col-lg-6 col-6 text-end">
                                            <span class=" text-success">
                                                - <fmt:formatNumber value="${cartSummary.discount}" type="currency"
                                                                    currencySymbol="đ"/>
                                            </span>
                                        </div>
                                    </div>

                                    <!-- Shipping -->
                                    <div class="border-bottom mb-10 pb-5">
                                        <div class="row">
                                            <div class="col-lg-6 col-6">
                                                <span class="font-md-bold color-brand-3">
                                                    <fmt:message key="cart.summary.shipping"/>
                                                </span>
                                            </div>
                                            <div class="col-lg-6 col-6 text-end">
                                                <span class=" color-brand-3">
                                                    <fmt:message key="cart.summary.shipping_free"/>
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Grand Total -->
                                    <div class="row">
                                        <div class="col-lg-6 col-6">
                                            <span class="font-md-bold color-brand-3">
                                                <fmt:message key="cart.summary.total"/>
                                            </span>
                                        </div>
                                        <div class="col-lg-6 col-6 text-end">
                                            <h5 class=" color-brand-3">
                                                <fmt:formatNumber value="${cartSummary.total}" type="currency"
                                                                  currencySymbol="đ"/>
                                            </h5>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <!-- Address Selection Script -->
    <script src="<c:url value='/static/common/js/address-select.js'/>"></script>
</t:layout>