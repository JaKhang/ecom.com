<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>

<fmt:message key="verify.pending.title" var="pageTitle"/>

<t:layout title="${pageTitle}">
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.15.2/dist/cdn.min.js"></script>
    <section class="section-box shop-template mt-60">
        <div class="container">
            <div class="row mb-100">
                <div class="col-lg-3"></div>

                <div class="col-lg-6 text-center">
                    <!-- Icon Email (Dùng ảnh có sẵn của theme hoặc icon font) -->
                    <div class="mb-30">
                        <img src="<c:url value='/static/imgs/template/icons/email-sent.svg'/>"
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
                        <!-- Khởi tạo Alpine với thời gian từ Server -->
                        <div x-data="timerLogic(20)" x-init="startCountdown()">

                            <a href="<c:url value='/verify/resend'/>"
                               class="btn btn-buy w-auto font-md-bold"

                            :class="{ 'disabled': timeLeft > 0 }"

                            :style="timeLeft > 0 ? 'pointer-events: none; opacity: 0.5;' : ''"

                            @click="handleClick($event)">

                            <span x-show="timeLeft === 0">
            <fmt:message key="verify.pending.btn_resend"/>
        </span>

                            <!-- Hiển thị khi Đang đếm ngược -->
                            <span x-show="timeLeft > 0" x-cloak>
            <fmt:message key="verify.pending.btn_resend"/>
            (<span x-text="timeLeft"></span>s)
        </span>
                            </a>

                        </div>
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
                    // Nếu còn thời gian thì chặn chuyển trang
                    if (this.timeLeft > 0) {
                        return;
                    }
                    // Nếu hết giờ -> Chuyển trang (Reload để gọi Server)
                    window.location.href = e.currentTarget.href;
                }
            }
        }
    </script>

    <style>
        [x-cloak] {
            display: none !important;
        }

        .cursor-not-allowed {
            cursor: not-allowed !important;
        }
    </style>
</t:layout>
