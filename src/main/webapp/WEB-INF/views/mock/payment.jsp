<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<t:layout title="Pay ment">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white text-center">
                        <h4 class="mb-0">Cổng thanh toán MOCK</h4>
                    </div>
                    <div class="card-body text-center">
                        <h5 class="card-title mb-4">Thông tin giao dịch</h5>
                        <p class="card-text fs-5">
                            Đơn hàng: <strong>${param.orderRef}</strong>
                        </p>
                        <p class="card-text fs-4 text-primary fw-bold">
                            Số tiền: <fmt:formatNumber value="${param.amount}" type="currency" currencySymbol="₫"/>
                        </p>
                        <hr class="my-4">
                        <p class="mb-3">Vui lòng chọn trạng thái giao dịch giả lập:</p>

                        <div class="d-grid gap-3">
                            <a href="${linkSuccess}" class="btn btn-success btn-lg">
                                <i class="bi bi-check-circle-fill me-2"></i>Thanh toán THÀNH CÔNG
                            </a>

                            <a href="${linkFail}" class="btn btn-danger btn-lg">
                                <i class="bi bi-x-circle-fill me-2"></i>Thanh toán THẤT BẠI
                            </a>
                        </div>
                    </div>
                    <div class="card-footer text-muted text-center small">
                        <i class="bi bi-shield-lock me-1"></i>Giao dịch được bảo mật bằng HMAC-SHA256
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
