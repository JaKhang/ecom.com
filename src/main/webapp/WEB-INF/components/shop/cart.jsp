<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.CartComponent"/>

<%-- Khởi tạo các biến từ Bundle --%>
<fmt:message key="global.image.thumbnail" var="defaultThumbnail"/>
<fmt:message key="routes.details" var="productPath"/>
<fmt:message key="routes.shop" var="shopPath"/>
<fmt:message key="cart.remove.success" var="msgSuccess"/>
<fmt:message key="cart.remove.error" var="msgError"/>
<fmt:message key="cart.update.success" var="updateSuccessMessage"/>
<fmt:message key="cart.update.error" var="updateErrorMessage"/>

<c:set var="cart" value="${component.cart}"/>
<c:set var="cartSummary" value="${cart.summary}"/>
<c:set var="id" value="${component.id}"/>

<div class="row" id="cart-row" jw-childOnly
     x-data="{
        remove(variantId) {
            JWireInstance.dispatch('cart', 'remove', variantId)
                .then(() => {
                    Toastify({
                        text: '${msgSuccess}',
                        className: 'bg-success',
                        style: { background: 'unset' },
                        duration: 3000
                    }).showToast();
                })
                .then(() => JWireInstance.dispatch('${id}', 'refresh'))
                .catch(() => {
                    Toastify({
                        text: '${msgError}',
                        className: 'bg-danger',
                        style: { background: 'unset' },
                        duration: 3000
                    }).showToast();
                });
        }
    }">

    <div class="col-lg-9">
        <div class="box-carts">
            <%-- HEADER BẢNG GIỎ HÀNG --%>
            <div class="head-wishlist">
                <div class="item-wishlist">
                    <div class="wishlist-cb">
                        <input class="cb-layout cb-all" type="checkbox">
                    </div>
                    <div class="wishlist-product">
                        <span class="font-md-bold color-brand-3"><fmt:message key="cart.table.product"/></span>
                    </div>
                    <div class="wishlist-price">
                        <span class="font-md-bold color-brand-3"><fmt:message key="cart.table.price"/></span>
                    </div>
                    <div class="wishlist-status">
                        <span class="font-md-bold color-brand-3"><fmt:message key="cart.table.quantity"/></span>
                    </div>
                    <div class="wishlist-action">
                        <span class="font-md-bold color-brand-3"><fmt:message key="cart.table.subtotal"/></span>
                    </div>
                    <div class="wishlist-remove">
                        <span class="font-md-bold color-brand-3"><fmt:message key="cart.table.remove"/></span>
                    </div>
                </div>
            </div>

            <div class="content-wishlist mb-20">
                <c:choose>
                    <c:when test="${cart != null && fn:length(cart.items) > 0}">
                        <c:forEach items="${cart.items}" var="item">
                            <div class="item-wishlist">
                                    <%-- CHECKBOX --%>
                                <div class="wishlist-cb">
                                    <input class="cb-layout cb-select" type="checkbox">
                                </div>

                                    <%-- THÔNG TIN SẢN PHẨM --%>
                                <div class="wishlist-product">
                                    <div class="product-wishlist">
                                        <div class="product-image">
                                            <a href="<c:url value='${productPath}/${item.productId}'/>">
                                                <img src="${not empty item.thumbnail ? item.thumbnail : '/assets/imgs/page/product/img-sub.png'}"
                                                     alt="${item.variantName}">
                                            </a>
                                        </div>
                                        <div class="product-info" style="align-items: unset">
                                            <a href="<c:url value='${productPath}/${item.productId}'/>">
                                                <h6 class="color-brand-3">${item.variantName}</h6>
                                            </a>
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

                                    <%-- ĐƠN GIÁ --%>
                                <div class="wishlist-price">
                                    <h6 class="color-brand-3">
                                        <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="đ"/>
                                    </h6>
                                    <c:if test="${item.unitOriginalPrice > item.unitPrice}">
                                        <span class="text-muted text-decoration-line-through font-sm ml-5">
                                            <fmt:formatNumber value="${item.unitOriginalPrice}" type="currency"
                                                              currencySymbol="đ"/>
                                        </span>
                                    </c:if>
                                </div>

                                    <%-- SỐ LƯỢNG --%>
                                <div class="wishlist-status"
                                     x-data="{
                                        qty: ${item.quantity},
                                        variantId: '${item.variantId}',
                                        timer: null,
                                        changeQty(amount) {
                                            let newValue = parseInt(this.qty) + amount;
                                            if (newValue < 1) newValue = 1;
                                            this.qty = newValue;
                                        },
                                        updateCartOnServer() {
                                            JWireInstance.dispatch('cart', 'updateCart', this.variantId, this.qty)
                                        .then(() => JWireInstance.dispatch('${id}', 'refresh'))
                                        .then(() => {
                                               Toastify({
                                                    text: '${updateSuccessMessage}',
                        className: 'bg-success',
                        style: { background: 'unset' },
                        duration: 3000
                    }).showToast();
                })
                .catch(() => {
                    Toastify({
                        text: '${updateErrorMessage}',
                        className: 'bg-danger',
                        style: { background: 'unset' },
                        duration: 3000
                    }).showToast();
                });
                                        }
                                     }"
                                     x-init="$watch('qty', (value) => {
                                        clearTimeout(timer);
                                        timer = setTimeout(() => {
                                            updateCartOnServer();
                                        }, 800);
                                     })"
                                >
                                    <div class="box-quantity">
                                        <div class="input-quantity">
                                            <input class="font-xl color-brand-3" type="text" x-model="qty" readonly
                                                   data-variant-id="${item.variantId}">
                                            <span class="minus-cart" @click="changeQty(-1)"></span>
                                            <span class="plus-cart" @click="changeQty(1)"></span>
                                        </div>
                                    </div>
                                </div>

                                    <%-- THÀNH TIỀN --%>
                                <div class="wishlist-action">
                                    <h6 class="color-brand-3">
                                        <fmt:formatNumber value="${item.total}" type="currency" currencySymbol="đ"/>
                                    </h6>
                                </div>

                                    <%-- NÚT XÓA --%>
                                <div class="wishlist-remove">
                                    <button class="btn btn-delete" @click="remove('${item.variantId}')"></button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>

                    <%-- TRƯỜNG HỢP GIỎ HÀNG TRỐNG --%>
                    <c:otherwise>
                        <div class="text-center p-5">
                            <h4 class="color-gray-500 mb-5"><fmt:message key="cart.empty.message"/></h4>
                            <a class="btn btn-buy mt-10" href="<c:url value='/${shopPath}'/>">
                                <fmt:message key="cart.empty.action"/>
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- CÁC NÚT ĐIỀU HƯỚNG --%>
            <div class="row mb-40">
                <div class="col-lg-6 col-md-6 col-sm-6-col-6">
                    <a class="btn btn-buy w-auto arrow-back mb-10" href="<c:url value='/${shopPath}'/>">
                        <fmt:message key="cart.action.continue"/>
                    </a>
                </div>
                <div class="col-lg-6 col-md-6 col-sm-6-col-6 text-md-end">

                </div>
            </div>
        </div>
    </div>

    <%-- TỔNG KẾT ĐƠN HÀNG (SIDEBAR) --%>
    <div class="col-lg-3">
        <div class="summary-cart">
            <div class="border-bottom mb-10">
                <div class="row">
                    <div class="col-6">
                        <span class="font-md-bold color-gray-500"><fmt:message key="cart.summary.subtotal"/></span>
                    </div>
                    <div class="col-6 text-end">
                        <h6><fmt:formatNumber value="${cartSummary.subtotal}" type="currency" currencySymbol="đ"/></h6>
                    </div>
                </div>
            </div>

            <div class="border-bottom mb-10">
                <div class="row">
                    <div class="col-6">
                        <span class="font-md-bold color-gray-500"><fmt:message key="cart.summary.discount"/></span>
                    </div>
                    <div class="col-6 text-end">
                        <h6 class="text-success">
                            - <fmt:formatNumber value="${cartSummary.discount}" type="currency" currencySymbol="đ"/>
                        </h6>
                    </div>
                </div>
            </div>

            <div class="border-bottom mb-10">
                <div class="row">
                    <div class="col-6">
                        <span class="font-md-bold color-gray-500"><fmt:message key="cart.summary.shipping"/></span>
                    </div>
                    <div class="col-6 text-end">
                        <h6><fmt:message key="cart.summary.shipping_free"/></h6>
                    </div>
                </div>
            </div>

            <div class="mb-10">
                <div class="row">
                    <div class="col-6">
                        <span class="font-md-bold color-gray-500"><fmt:message key="cart.summary.total"/></span>
                    </div>
                    <div class="col-6 text-end">
                        <h6 class="color-brand-3">
                            <fmt:formatNumber value="${cartSummary.total}" type="currency" currencySymbol="đ"/>
                        </h6>
                    </div>
                </div>
            </div>

            <div class="box-button">
                <a class="btn btn-buy" href="<c:url value='/checkout'/>">
                    <fmt:message key="cart.summary.checkout"/>
                </a>
            </div>
        </div>
    </div>
</div>
