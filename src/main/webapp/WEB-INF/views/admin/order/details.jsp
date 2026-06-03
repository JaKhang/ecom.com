<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/utils" %>

<jsp:useBean id="order" scope="request" type="com.nlu.store.modules.order.models.Order"/>

<t:layout title="Order" active="orders">

    <div class="content-header">
        <div>
            <h2 class="content-title card-title">Order detail</h2>
            <p>Details for Order ID: #${order.code != null ? order.code : order.id}</p>
        </div>
    </div>

    <div class="card">
        <header class="card-header">
            <div class="row align-items-center">
                <div class="col-lg-6 col-md-6 mb-lg-0 mb-15">
                    <span>
                        <i class="material-icons md-calendar_today"></i>
                        <b>
                            <util:formatDate value="${order.createdAt}" pattern="EEE, MMM d, yyyy, h:mm a"/>
                        </b>
                    </span>
                    <br>
                    <small class="text-muted">Order ID: ${order.code != null ? order.code : order.id}</small>
                </div>
                <form action="" method="post" class="d-inline-block col-lg-6 col-md-6 ">
                    <div class="ms-auto text-md-end">
                            <%-- Form cập nhật trạng thái đơn hàng --%>
                        <input type="hidden" name="id" value="${order.id}">
                        <select class="form-select d-inline-block mb-lg-0 mb-15 mw-200" name="status">
                            <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed
                            </option>
                            <option value="SHIPPING" ${order.status == 'SHIPPING' ? 'selected' : ''}>Shipped</option>
                            <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered
                            </option>
                            <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled
                            </option>
                        </select>
                        <button type="submit" class="ms-2 btn btn-primary">Save</button>
                        <a class="btn btn-secondary print ms-2" href="#" onclick="window.print();">
                            <i class="icon material-icons md-print"></i>
                        </a>
                    </div>
                </form>

            </div>

        </header>

        <div class="card-body">
            <!-- INFO CARDS -->
            <div class="row mb-50 mt-20 order-info-wrap">
                <!-- 1. Customer Info -->
                <div class="col-md-4">
                    <article class="icontext align-items-start">
                        <span class="icon icon-sm rounded-circle bg-primary-light">
                            <i class="text-primary material-icons md-person"></i>
                        </span>
                        <div class="text">
                            <h6 class="mb-1">Customer</h6>
                            <p class="mb-1">
                                    ${order.shippingDetails.contactName} <br>
                                <a href="mailto:${order.shippingDetails.contactEmail}">${order.shippingDetails.contactEmail}</a>
                                <br>
                                    ${order.shippingDetails.contactPhone}
                            </p>
                            <a href="/admin/users/detail?id=${order.userId}">View profile</a>
                        </div>
                    </article>
                </div>

                <!-- 2. Order Info -->
                <div class="col-md-4">
                    <article class="icontext align-items-start">
                        <span class="icon icon-sm rounded-circle bg-primary-light">
                            <i class="text-primary material-icons md-local_shipping"></i>
                        </span>
                        <div class="text">
                            <h6 class="mb-1">Order info</h6>
                            <p class="mb-1">
                                Shipping: ${order.shippingDetails.carrierName != null ? order.shippingDetails.carrierName : 'Standard Delivery'}
                                <br>
                                Pay method: ${order.paymentMethod} <br/>
                                Status:
                                <span class="d-inline-block badge rounded-pill ${order.status == 'DELIVERED' ? 'alert-success' : (order.status == 'CANCELLED' ? 'alert-danger' : 'alert-warning')}">
                                        ${order.status}
                                </span>
                            </p>
                            <a href="#">Download info</a>
                        </div>
                    </article>
                </div>

                <!-- 3. Deliver To -->
                <div class="col-md-4">
                    <article class="icontext align-items-start">
                        <span class="icon icon-sm rounded-circle bg-primary-light">
                            <i class="text-primary material-icons md-place"></i>
                        </span>
                        <div class="text">
                            <h6 class="mb-1">Deliver to</h6>
                            <p class="mb-1">
                                    <%-- Ưu tiên hiển thị fullAddress nếu có, nếu không thì ghép chuỗi --%>
                                <c:choose>
                                    <c:when test="${not empty order.shippingDetails.fullAddress}">
                                        ${order.shippingDetails.fullAddress}
                                    </c:when>
                                    <c:otherwise>
                                        ${order.shippingDetails.addressDetail}<br>
                                        ${order.shippingDetails.ward}, ${order.shippingDetails.district}<br>
                                        ${order.shippingDetails.province}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <a href="#">View profile</a>
                        </div>
                    </article>
                </div>
            </div>

            <!-- PRODUCT TABLE -->
            <div class="row">
                <div class="col-lg-7">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                            <tr>
                                <th width="40%">Product</th>
                                <th width="20%">Unit Price</th>
                                <th width="20%">Quantity</th>
                                <th class="text-end" width="20%">Total</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${order.items}" var="item">
                                <tr>
                                    <td>
                                        <a class="itemside" href="#">
                                            <div class="left">
                                                <img class="img-xs"
                                                     src="${item.thumbnail != null ? item.thumbnail : '/assets/imgs/items/default.jpg'}"
                                                     alt="Item" width="40" height="40">
                                            </div>
                                            <div class="info">
                                                    ${item.productName} <br>
                                                    <%-- Hiển thị Variant (Màu, Size...) --%>
                                                <c:if test="${not empty item.variantSnapshot}">
                                                    <small class="text-muted">
                                                        <c:forEach items="${item.variantSnapshot}" var="entry"
                                                                   varStatus="loop">
                                                            ${entry.key}: ${entry.value}${!loop.last ? ', ' : ''}
                                                        </c:forEach>
                                                    </small>
                                                </c:if>
                                            </div>
                                        </a>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="đ"/>
                                    </td>
                                    <td>${item.quantity}</td>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${item.totalPrice}" type="currency"
                                                          currencySymbol="đ"/>
                                    </td>
                                </tr>
                            </c:forEach>

                            <!-- SUMMARY SECTION -->
                            <tr>
                                <td colspan="4">
                                    <article class="float-end">
                                        <dl class="dlist">
                                            <dt>Subtotal:</dt>
                                            <dd><fmt:formatNumber value="${order.subTotal}" type="currency"
                                                                  currencySymbol="đ"/></dd>
                                        </dl>
                                        <dl class="dlist">
                                            <dt>Shipping cost:</dt>
                                            <dd><fmt:formatNumber value="${order.shippingFee}" type="currency"
                                                                  currencySymbol="đ"/></dd>
                                        </dl>
                                        <c:if test="${order.discountAmount != null && order.discountAmount > 0}">
                                            <dl class="dlist">
                                                <dt>Discount:</dt>
                                                <dd class="text-danger">-<fmt:formatNumber
                                                        value="${order.discountAmount}" type="currency"
                                                        currencySymbol="$"/></dd>
                                            </dl>
                                        </c:if>
                                        <dl class="dlist">
                                            <dt>Grand total:</dt>
                                            <dd><b class="h5"><fmt:formatNumber value="${order.grandTotal}"
                                                                                type="currency" currencySymbol="đ"/></b>
                                            </dd>
                                        </dl>
                                        <dl class="dlist">
                                            <dt class="text-muted">Payment Status:</dt>
                                            <dd>
                                                <c:choose>
                                                    <c:when test="${order.paymentStatus == 'PAID'}">
                                                        <span class="badge rounded-pill alert-success text-success">Paid</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'UNPAID'}">
                                                        <span class="badge rounded-pill alert-warning text-warning">Unpaid</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'REFUNDED'}">
                                                        <span class="badge rounded-pill alert-danger text-danger">Refunded</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge rounded-pill alert-secondary">${order.paymentStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </dd>
                                        </dl>
                                    </article>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <a class="btn btn-primary" href="#">View Order Tracking</a>
                </div>

                <!-- SIDEBAR INFO -->
                <div class="col-lg-1"></div>
                <div class="col-lg-4">
                    <div class="box shadow-sm bg-light">
                        <h6 class="mb-15">Payment info</h6>
                        <p>
                            <!-- Vì Model Order không lưu số thẻ, hiển thị thông tin giao dịch -->
                            <strong>Method:</strong> ${order.paymentMethod}<br>
                            <strong>Ref ID:</strong> ${order.transactionRef != null ? order.transactionRef : 'N/A'}<br>
                            <strong>Paid At:</strong>
                            <c:if test="${order.paidAt != null}">
                                <%-- SỬ DỤNG TAG MỚI ĐỂ FORMAT NGÀY THANH TOÁN --%>
                                <util:formatDate value="${order.paidAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </c:if>
                            <c:if test="${order.paidAt == null}">Pending</c:if>
                        </p>
                    </div>

                    <!-- Note Form -->
                    <div class="h-25 pt-4">
                        <form action="/admin/orders/update-note" method="post">
                            <input type="hidden" name="id" value="${order.id}">
                            <div class="mb-3">
                                <label>Notes</label>
                                <textarea class="form-control" id="notes" name="note"
                                          placeholder="Type some note">${order.note}</textarea>
                            </div>
                            <button class="btn btn-primary" type="submit">Save note</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

</t:layout>
