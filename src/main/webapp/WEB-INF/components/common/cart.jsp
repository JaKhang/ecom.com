<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.HeaderCartComponent"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<fmt:message key="global.image.thumbnail" var="defaultThumbnail"/>


<%-- Khai báo các route từ Resource Bundle --%>
<fmt:message key="routes.cart" var="cartUrl"/>
<fmt:message key="routes.checkout" var="checkouPath"/>
<fmt:message key="routes.shop" var="shopUrl"/>
<fmt:message key="routes.details" var="detailsPath"/>
<fmt:message key="global.image.thumbnail" var="defaultThumbnail"/>

<div class="d-inline-block box-dropdown-cart" id="cart-box">
    <span class="font-lg icon-list icon-cart" jw-childOnly id="cart-icon">
        <%-- Thay "Cart" bằng key --%>
        <span><fmt:message key="header.cart.title"/></span>

        <span class="number-item font-xs">
            ${not empty component.cart ? component.cart.totalQuantity : 0}
        </span>
    </span>

    <div class="dropdown-cart" jw-childOnly>
        <c:choose>
            <c:when test="${not empty component.cart and component.cart.totalQuantity > 0}">
                <div class="cart-items-scroll" style="max-height: 300px; overflow-y: auto;">
                    <c:forEach items="${component.cart.items}" var="item">
                        <div class="item-cart mb-20">
                            <div class="cart-image">
                                <img src="${not empty item.thumbnail ? item.thumbnail : defaultThumbnail}"
                                     alt="<c:out value='${item.variantName}'/>">
                            </div>
                            <div class="cart-info">
                                <a class="font-sm-bold color-brand-3"
                                   href="<c:url value='/${detailsPath}/${item.slug}'/>">
                                    <c:out value="${item.variantName}"/>
                                </a>
                                <p>
                                    <span class="color-brand-2 font-sm-bold">
                                        ${item.quantity} x
                                        <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫"
                                                          maxFractionDigits="0"/>
                                    </span>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="border-bottom pt-0 mb-15"></div>

                <div class="cart-total">
                    <div class="row">
                        <div class="col-6 text-start">
                                <%-- Thay "Total:" bằng key --%>
                            <span class="font-md-bold color-brand-3">
                                <fmt:message key="cart.label.total"/>:
                            </span>
                        </div>
                        <div class="col-6 text-end">
                            <span class="font-md-bold color-brand-2">
                                <fmt:formatNumber value="${component.cart.totalPrice}" type="currency"
                                                  currencySymbol="₫" maxFractionDigits="0"/>
                            </span>
                        </div>
                    </div>

                    <div class="row mt-15">
                        <div class="col-6 text-start">
                                <%-- Thay "View cart" bằng key --%>
                            <a class="btn btn-cart w-auto" href="/${cartUrl}">
                                <fmt:message key="cart.button.view_cart"/>
                            </a>
                        </div>
                        <div class="col-6">
                                <%-- Thay "Checkout" bằng key --%>
                            <a class="btn btn-buy w-auto" href="/${checkouPath}">
                                <fmt:message key="cart.button.checkout"/>
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <div class="text-center p-20">
                        <%-- Thay "Your cart is empty" bằng key --%>
                    <p class="font-sm color-gray-500">
                        <fmt:message key="cart.message.empty"/>
                    </p>

                        <%-- Thay "Shop now" bằng key --%>
                    <a href="${shopUrl}" class="btn btn-cart btn-sm mt-10">
                        <fmt:message key="cart.button.shop_now"/>
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
