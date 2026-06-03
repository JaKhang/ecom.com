<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/admin" %>
<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.admin.AdminReviewComponent"/>
<fmt:message key="review.msg.verify.success" var="msgVerifySuccess"/>
<fmt:message key="review.msg.verify.error" var="msgVerifyError"/>
<fmt:message key="review.msg.status.success" var="msgStatusSuccess"/>
<fmt:message key="review.msg.status.error" var="msgStatusError"/>
<c:set value="${component.data}" var="reviews"/>
<c:set value="${component.id}" var="id"/>
<div class="card mb-4" x-data="{
    verify(reviewId) {
        JWireInstance.dispatch('${id}', 'verify', reviewId)
            .then(() => {
                Toastify({
                    text: '${msgVerifySuccess}',
                    className: 'bg-success',
                    style: { background: 'unset' },
                    duration: 3000
                }).showToast();
            })
            .catch(() => {
                Toastify({
                    text: '${msgVerifyError}',
                    className: 'bg-danger',
                    style: { background: 'unset' },
                    duration: 3000
                }).showToast();
            });
    },

    setStatus(reviewId, status) {
        JWireInstance.dispatch('${id}', 'updateStatus', reviewId, status)
            .then(() => {
                Toastify({
                    text: '${msgStatusSuccess}',
                    className: 'bg-success',
                    style: { background: 'unset' },
                    duration: 3000
                }).showToast();
            })
            .catch(() => {
                Toastify({
                    text: '${msgStatusError}',
                    className: 'bg-danger',
                    style: { background: 'unset' },
                    duration: 3000
                }).showToast();
            });
    }
}">
    <header class="card-header">
        <div class="row gx-3">
            <div class="col-lg-4 col-md-6 me-auto">
                <input class="form-control" type="text"
                       placeholder="<fmt:message key='review.list.search_placeholder'/>">
            </div>
            <div class="col-lg-2 col-md-3 col-6">
                <select class="form-select" jw-debounce="500" jw-model="status">
                    <option value="PENDING" ${component.status == 'PENDING' ? 'selected' : ''}>
                        Pending
                    </option>

                    <option value="APPROVED" ${component.status == 'APPROVED' ? 'selected' : ''}>
                        Approved
                    </option>

                    <option value="REJECTED" ${component.status == 'REJECTED' ? 'selected' : ''}>
                        Rejected
                    </option>

                    <!-- Sử dụng value rỗng "" cho trường hợp "Tất cả" để dễ xử lý logic empty -->
                    <option value="null" ${empty component.status || component.status == null ? 'selected' : ''}>
                        <fmt:message key="review.list.filter.status.all"/>
                    </option>
                </select>
            </div>
            <div class="col-lg-2 col-md-3 col-6">
                <select class="form-select">
                    <option><fmt:message key="review.list.filter.limit.20"/></option>
                    <option><fmt:message key="review.list.filter.limit.30"/></option>
                    <option><fmt:message key="review.list.filter.limit.40"/></option>
                </select>
            </div>
        </div>
    </header>

    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th><fmt:message key="review.table.col.product"/></th>
                    <th><fmt:message key="review.table.col.name"/></th>
                    <th><fmt:message key="review.table.col.rating"/></th>
                    <th><fmt:message key="review.table.col.comment"/></th>
                    <th><fmt:message key="review.table.col.date"/></th>
                    <th><fmt:message key="review.table.col.status"/></th>
                    <th><fmt:message key="review.table.col.verified"/></th>
                    <th class="text-end"><fmt:message key="review.table.col.action"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="review" items="${reviews}">
                    <tr>
                        <td><b>${review.productName}</b></td>
                        <td>${review.username}</td>
                        <td>
                            <ul class="rating-stars">
                                <li class="stars-active" style="width: ${review.rating * 20}%;">
                                    <img src="<c:url value='/static/admin/imgs/icons/stars-active.svg'/>" alt="stars">
                                </li>
                                <li>
                                    <img src="<c:url value='/static/admin/imgs/icons/starts-disable.svg'/>" alt="stars">
                                </li>
                            </ul>
                        </td>
                        <td>
                            <p>${review.comment}</p>
                        </td>
                        <td>
                            <fmt:formatDate value="${review.asDate()}" pattern="dd.MM.yyyy"/>
                        </td>

                        <!-- Cột Status: Sử dụng badge rounded-pill alert-[color] -->
                        <td>
                            <c:choose>
                                <c:when test="${review.status == 'PENDING'}">
                                    <span class="badge rounded-pill alert-warning">Pending</span>
                                </c:when>
                                <c:when test="${review.status == 'APPROVED'}">
                                    <span class="badge rounded-pill alert-success">Approved</span>
                                </c:when>
                                <c:when test="${review.status == 'REJECTED'}">
                                    <span class="badge rounded-pill alert-danger">Rejected</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge rounded-pill alert-secondary">${review.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <!-- Cột Verified: Check icon hoặc Button -->
                        <td>
                            <c:choose>
                                <c:when test="${review.verified}">
                                    <!-- Đã verify: Icon check màu xanh -->
                                    <div class="icon-verify text-center">
                                        <i class="material-icons md-check_circle text-success font-xxl"></i>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- Chưa verify: Button -->
                                    <button class="btn btn-sm btn-brand rounded font-sm"
                                            @click="verify('${review.id}')"
                                    >
                                        <fmt:message key="review.action.verify"/>
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td class="text-end">
                            <a class="btn btn-md rounded font-sm" href="review-detail?id=${review.id}">
                                <fmt:message key="review.action.detail"/>
                            </a>
                            <div class="dropdown">
                                <a class="btn btn-light rounded btn-sm font-sm" href="#" data-bs-toggle="dropdown">
                                    <i class="material-icons md-more_horiz"></i>
                                </a>
                                <div class="dropdown-menu">
                                    <c:if test="${!review.verified}">
                                        <button type="button"
                                                @click="verify('${review.id}')"
                                                class="dropdown-item text-success">
                                            <i class="material-icons md-check_circle font-xxl"></i>
                                            <fmt:message key="review.action.verify"/>
                                        </button>

                                    </c:if>

                                    <c:if test="${review.status != 'APPROVED'}">
                                        <button type="button"
                                                @click="setStatus('${review.id}', 'APPROVED')"
                                                class="dropdown-item text-primary">
                                            <i class="material-icons md-thumb_up font-xxl"></i>
                                            <fmt:message key="review.action.approve"/>
                                        </button>
                                    </c:if>

                                    <c:if test="${review.status != 'REJECTED'}">
                                        <button type="button"
                                                @click="setStatus('${review.id}', 'REJECTED')"
                                                class="dropdown-item text-warning">
                                            <i class="material-icons md-thumb_down font-xxl"></i>
                                            <fmt:message key="review.action.reject"/>
                                        </button>
                                    </c:if>

                                    <div class="dropdown-divider"></div>

                                    <a class="dropdown-item" href="review-detail?id=${review.id}">
                                        <fmt:message key="review.action.view_detail"/>
                                    </a>
                                    <a class="dropdown-item" href="review-edit?id=${review.id}">
                                        <fmt:message key="review.action.edit_info"/>
                                    </a>
                                    <a class="dropdown-item text-danger" href="review-delete?id=${review.id}">
                                        <fmt:message key="review.action.delete"/>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty reviews}">
                    <tr>
                        <!-- Colspan = 8 vì có thêm 2 cột Status và Verified -->
                        <td colspan="8" class="text-center">No reviews found.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<div class="pagination-area mt-30 mb-50">
    <t:pagination pagination="${component}"/>
</div>