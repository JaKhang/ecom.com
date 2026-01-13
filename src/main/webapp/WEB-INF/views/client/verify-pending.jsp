<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>

<fmt:message key="verify.pending.title" var="pageTitle"/>

<t:layout title="${pageTitle}">
    <section class="section-box shop-template mt-60">
        <div class="container">
            <div class="row mb-100">
                <div class="col-lg-3"></div>

                <div class="col-lg-6 text-center">
                    <!-- Icon Email (Dùng ảnh có sẵn của theme hoặc icon font) -->
                    <div class="mb-30">
                        <img src="<c:url value='/static/client/imgs/template/icons/email-sent.svg'/>"
                             alt="Email Sent" style="width: 80px; opacity: 0.7;">
                    </div>

                    <h3 class="mb-20">${pageTitle}</h3>

                    <p class="font-md color-gray-500 mb-10">
                        <fmt:message key="verify.pending.hello"/>
                        <strong><sec:user property="username"/></strong>,
                    </p>

                    <p class="font-md color-gray-500">
                        <fmt:message key="verify.pending.desc"/> <br/>
                        <!-- Hiển thị email hiện tại của user -->
                        <strong class="color-brand-1">
                            <sec:user property="email"/>
                        </strong>
                    </p>

                    <!-- Thông báo kết quả sau khi bấm nút gửi lại -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success mt-30">
                            <i class="fi-rs-check"></i> <fmt:message key="${successMessage}"/>
                        </div>
                    </c:if>

                    <c:if test="${not empty errors['global']}">
                        <div class="alert alert-danger mt-30">
                            <fmt:message key="${errors['global']}"/>
                        </div>
                    </c:if>

                    <div class="mt-40">
                        <jw:jawire component="com.nlu.store.modules.user.controllers.ResendVerifyComponent"/>
                    </div>

                    <div class="mt-20">
                        <p class="font-xs color-gray-500">
                            <fmt:message key="verify.pending.wrong_email"/>
                            <a href="<c:url value='/logout'/>" class="color-brand-1">
                                <fmt:message key="menu.logout"/>
                            </a>
                        </p>
                    </div>
                </div>

                <div class="col-lg-3"></div>
            </div>
        </div>
    </section>

    <script>
        function timerLogic(serverSeconds) {
            return {
                timeLeft: serverSeconds,

                startCountdown() {
                    if (this.timeLeft > 0) {
                        const interval = setInterval(() => {
                            this.timeLeft--;
                            if (this.timeLeft <= 0) {
                                this.timeLeft = 0;
                                clearInterval(interval);
                            }
                        }, 1000);
                    }
                },

                handleClick(e) {
                    // Logic phụ: Chặn chuyển trang nếu timeLeft > 0
                    // (Thực tế dòng :style='pointer-events: none' đã chặn rồi, đây là lớp bảo vệ thứ 2)
                    if (this.timeLeft > 0) {
                        e.preventDefault();
                    }
                }
            }
        }
    </script>

    <style>
        /* Ẩn nội dung khi Alpine chưa load xong để tránh giật giao diện */
        [x-cloak] { display: none !important; }
    </style>
</t:layout>
