<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.order.admin.controller.AdminOrderComponent"/>


<div class="card mb-4">
    <header class="card-header">
        <div class="row gx-3">
            <div class="col-lg-4 col-md-6 me-auto">
                <input class="form-control" type="text" placeholder="Search...">
            </div>
            <div class="col-lg-2 col-6 col-md-3">
                <select class="form-select">
                    <option>Status</option>
                    <option>Active</option>
                    <option>Disabled</option>
                    <option>Show all</option>
                </select>
            </div>
            <div class="col-lg-2 col-6 col-md-3">
                <select class="form-select">
                    <option>Show 20</option>
                    <option>Show 30</option>
                    <option>Show 40</option>
                </select>
            </div>
        </div>
    </header>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>#ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Email</th>
                    <th scope="col">Total</th>
                    <th scope="col">Status</th>
                    <th scope="col">Date</th>
                    <th class="text-end" scope="col"> Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${component.data}" var="order">
                    <tr>
                        <!-- Order Code / ID -->
                        <td>${order.code != null ? order.code : order.id}</td>

                        <!-- Customer Name -->
                        <td>
                            <b>${order.shippingDetails.contactName}</b>
                        </td>

                        <!-- Customer Email -->
                        <td>
                            <a href="mailto:${order.shippingDetails.contactEmail}" class="__cf_email__">
                                    ${order.shippingDetails.contactEmail}
                            </a>
                        </td>

                        <!-- Grand Total -->
                        <td>
                            <fmt:formatNumber value="${order.grandTotal}" type="currency" currencySymbol="đ"/>
                        </td>

                        <!-- Status Badge -->
                        <td>
                            <c:choose>
                                <c:when test="${order.status == 'DELIVERED'}">
                                    <c:set var="statusClass" value="alert-success "/>
                                </c:when>
                                <c:when test="${order.status == 'CANCELLED' || order.status == 'RETURNED' || order.status == 'REFUNDED'}">
                                    <c:set var="statusClass" value="alert-danger"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="statusClass" value="alert-warning "/>
                                </c:otherwise>
                            </c:choose>

                                <%-- Render the localized Order Status --%>
                            <span class="badge rounded-pill ${statusClass}">
                                <fmt:message key="order.status.${order.status}"/>
                                </span>
                        </td>

                        <!-- Created Date (Giả sử controller đã format hoặc dùng toString) -->
                        <td>
                                ${fn:substring(order.createdAt, 0, 10)}
                        </td>

                        <!-- Actions -->
                        <td class="text-end">
                            <a class="btn btn-md rounded font-sm" href="<c:url value='/admin/orders/${order.id}'/>">Detail</a>
                            <div class="dropdown">
                                <a class="btn btn-light rounded btn-sm font-sm" href="#" data-bs-toggle="dropdown">
                                    <i class="material-icons md-more_horiz"></i>
                                </a>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="<c:url value='/admin/orders/${order.id}'/>">View detail</a>
                                    <a class="dropdown-item" href="#">Edit info</a>
                                    <a class="dropdown-item text-danger" href="#">Delete</a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>


<div class="pagination-area mt-15 mb-50">
    <t:pagination pagination="${component}"/>
</div>