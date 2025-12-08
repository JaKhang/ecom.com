<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--@elvariable id="form" type="com.nlu.store.modules.user.dto.RegisterRequest"--%>
<%--@elvariable id="errors" type="java.util.Map"--%>
<!-- 1. Lấy text tiêu đề -->
<fmt:message key="register.title" var="pageTitle"/>
<t:layout title="${pageTitle}">

    <section class="section-box shop-template mt-60">
        <div class="container">
            <div class="row mb-100">
                <div class="col-lg-1"></div>

                <!-- REGISTER FORM COLUMN -->
                <div class="col-lg-5">
                    <h3>${pageTitle}</h3>
                    <p class="font-md color-gray-500"><fmt:message key="register.subtitle"/></p>

                    <!-- Global Error Alert (Lỗi chung hệ thống) -->
                    <c:if test="${not empty errors['global']}">
                        <div class="alert alert-danger mt-3">
                            <fmt:message key="${errors['global']}"/>
                        </div>
                    </c:if>

                    <div class="form-register mt-30 mb-30">
                        <form action="<c:url value='/register'/>" method="post">

                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700"><fmt:message key="register.label.fullname"/></label>
                                <input class="form-control ${not empty errors['fullName'] ? 'is-invalid' : ''}"
                                       name="fullName" type="text"
                                       value="${form.fullName}"
                                       placeholder="<fmt:message key='register.placeholder.fullname'/>">
                                <c:if test="${not empty errors['fullName']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['fullName']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- 2. Email -->
                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700"><fmt:message key="register.label.email"/></label>
                                <input class="form-control ${not empty errors['email'] ? 'is-invalid' : ''}"
                                       name="email" type="text"
                                       value="${form.email}"
                                       placeholder="<fmt:message key='register.placeholder.email'/>">
                                <c:if test="${not empty errors['email']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['email']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- 3. Password -->
                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700"><fmt:message key="register.label.password"/></label>
                                <input class="form-control ${not empty errors['password'] ? 'is-invalid' : ''}"
                                       name="password" type="password"
                                       value="${form.password}"
                                       placeholder="******************">
                                <c:if test="${not empty errors['password']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['password']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700"><fmt:message key="register.label.repassword"/></label>
                                <input class="form-control ${not empty errors['confirmPassword'] ? 'is-invalid' : ''}"
                                       name="confirmPassword" type="password"
                                       value="${form.confirmPassword}"
                                       placeholder="******************">
                                <c:if test="${not empty errors['confirmPassword']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['confirmPassword']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- 5. Terms Checkbox -->
                            <div class="form-group">
                                <label>
                                    <input class="checkagree" name="agree" value="true" type="checkbox" ${form.agree ? 'checked' : ''}>
                                    <fmt:message key="register.terms"/>
                                </label>
                                <c:if test="${not empty errors['agree']}">
                                    <span class="text-danger d-block font-xs mt-1">
                                        <fmt:message key="${errors['agree']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- Submit Button -->
                            <div class="form-group">
                                <input class="font-md-bold btn btn-buy" type="submit" value="<fmt:message key='register.btn.submit'/>">
                            </div>

                            <!-- Link to Login -->
                            <div class="mt-20">
                                <span class="font-xs color-gray-500 font-medium"><fmt:message key="register.text.has_account"/></span>
                                <a class="font-xs color-brand-3 font-medium" href="<c:url value='/login'/>">
                                    <fmt:message key="register.link.login"/>
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="box-login-social pt-65 pl-50">
                        <h5 class="text-center"><fmt:message key="register.social.title"/></h5>
                        <div class="box-button-login mt-25">
                            <a class="btn btn-login font-md-bold color-brand-3 mb-15">
                                Sign up with <img src="<c:url value='/static/imgs/page/account/google.svg'/>" alt="Google">
                            </a>
                            <a class="btn btn-login font-md-bold color-brand-3 mb-15">
                                Sign up with <span class="color-blue font-md-bold">Facebook</span>
                            </a>
                            <a class="btn btn-login font-md-bold color-brand-3 mb-15">
                                Sign up with <img src="<c:url value='/static/imgs/page/account/amazon.svg'/>" alt="Amazon">
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="section-box box-newsletter">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 col-md-7 col-sm-12">
                    <h3 class="color-white">
                        <fmt:message key="newsletter.title"/>
                        <span class="color-warning">10%</span>
                        <fmt:message key="newsletter.discount_text"/>
                    </h3>
                    <p class="font-lg color-white">
                        <fmt:message key="newsletter.desc"/>
                        <span class="font-lg-bold"><fmt:message key="newsletter.desc_highlight"/></span>
                    </p>
                </div>
                <div class="col-lg-4 col-md-5 col-sm-12">
                    <div class="box-form-newsletter mt-15">
                        <form class="form-newsletter">
                            <input class="input-newsletter font-xs" value=""
                                   placeholder="<fmt:message key='newsletter.placeholder'/>">
                            <button class="btn btn-brand-2">
                                <fmt:message key="newsletter.btn"/>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</t:layout>
