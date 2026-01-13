<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%-- Initialize the Order Component bean from request scope --%>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.order.client.component.OrderTableComponents"/>

<%-- Main loop: Iterate through the list of orders --%>
<c:forEach var="order" items="${component.data}">
    <div>
        <div class="box-orders">
                <%-- Order Header: Contains ID, Date, and Statuses --%>
            <div class="head-orders">
                <div class="head-left">
                        <%-- Display Order ID --%>
                    <h5 class="mr-20">
                        <fmt:message key="order.list.id"/>: #${order.code}
                    </h5>

                        <%-- Display Order Date (formatted to YYYY-MM-DD) --%>
                    <span class="font-md color-brand-3 mr-20">
                        <fmt:message key="order.list.date"/>: ${fn:substring(order.createdAt, 0, 10)}
                    </span>

                        <%--
                            Logic: Determine CSS class for Payment Status
                            - PAID: Success (Green)
                            - UNPAID: Danger (Red)
                            - Others: Muted (Gray)
                        --%>
                    <c:choose>
                        <c:when test="${order.paymentStatus == 'PAID'}">
                            <c:set var="paymentClass" value="text-success"/>
                        </c:when>
                        <c:when test="${order.paymentStatus == 'UNPAID'}">
                            <c:set var="paymentClass" value="text-danger"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="paymentClass" value="text-muted"/>
                        </c:otherwise>
                    </c:choose>

                        <%-- Payment Status Display Section --%>
                    <div class="payment-info mr-20">
                        <span class="font-md color-brand-3">
                            <fmt:message key="payment.label.status"/>:
                        </span>
                        <span class="font-md-bold ${paymentClass}">
                            <fmt:message key="payment.status.${order.paymentStatus}"/>
                        </span>
                            <%-- Show Payment Method (e.g., COD, VNPAY) --%>
                        <span class="font-xs color-gray-500 ml-5">
                            (${order.paymentMethod})
                        </span>
                    </div>

                        <%--
                            Logic: Determine CSS class for Order Fulfillment Status
                            - DELIVERED: label-delivered
                            - CANCELLED/RETURNED/REFUNDED: label-canceled
                            - Others (PENDING/SHIPPING): label-delivery
                        --%>
                    <c:choose>
                        <c:when test="${order.status == 'DELIVERED'}">
                            <c:set var="statusClass" value="label-delivered"/>
                        </c:when>
                        <c:when test="${order.status == 'CANCELLED' || order.status == 'RETURNED' || order.status == 'REFUNDED'}">
                            <c:set var="statusClass" value="label-canceled"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="statusClass" value="label-delivery"/>
                        </c:otherwise>
                    </c:choose>

                        <%-- Render the localized Order Status --%>
                    <span class="${statusClass}">
                        <fmt:message key="order.status.${order.status}"/>
                    </span>
                </div>

                <div class="head-right">
                        <%-- Action: Link to Order Detail Page --%>
                    <a href="<c:url value='/account/orders/${order.id}'/>" class="btn btn-buy font-sm-bold w-auto">
                        <fmt:message key="order.list.btn.view"/>
                    </a>
                </div>
            </div>

                <%-- Order Body: Contains the list of products in this order --%>
            <div class="body-orders">
                <div class="list-orders">
                        <%-- Sub-loop: Iterate through items within the order --%>
                    <c:forEach var="item" items="${order.items}">
                        <div class="item-orders">
                                <%-- Product Thumbnail --%>
                            <div class="image-orders">
                                <img src="${item.thumbnail}" alt="${item.productName}">
                            </div>

                                <%-- Product Name and Variant Details --%>
                            <div class="info-orders">
                                <h5>${item.productName}</h5>

                                    <%-- Display dynamic attributes like Color, Size from variant snapshot --%>
                                <c:if test="${not empty item.variantSnapshot}">
                                    <p class="font-xs color-gray-500 mt-5">
                                        <c:forEach var="entry" items="${item.variantSnapshot}">
                                            <span class="mr-10">${entry.key}: <strong>${entry.value}</strong></span>
                                        </c:forEach>
                                    </p>
                                </c:if>
                            </div>

                                <%-- Quantity Display (Formatted with leading zero if needed) --%>
                            <div class="quantity-orders">
                                <h5>
                                    <fmt:message key="order.list.quantity"/>: ${String.format("%02d", item.quantity)}
                                </h5>
                            </div>

                                <%-- Price Display with Currency Formatting --%>
                            <div class="price-orders">
                                <h3>
                                    <fmt:formatNumber value="${item.price}" type="currency"
                                                      currencyCode="${order.currency != null ? order.currency : 'VND'}"/>
                                </h3>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</c:forEach>

<%-- Pagination Component --%>
<t:pagination pagination="${component}" scrollTarget="scroll-target"/>
