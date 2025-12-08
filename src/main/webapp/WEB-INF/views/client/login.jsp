<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<t:layout title="Login">
    <section class="section-box shop-template mt-60">
        <div class="container">
            <div class="row mb-100">
                <div class="col-lg-1"></div>
                <div class="col-lg-5">
                    <h3><fmt:message key="login.title"/></h3>
                    <p class="font-md color-gray-500"><fmt:message key="login.welcome"/></p>


                    <div class="form-register mt-30 mb-30">

                        <!-- Hiển thị lỗi chung (ví dụ: sai tài khoản/mật khẩu) -->
                        <c:if test="${not empty errors['global']}">
                            <div class="alert alert-danger mt-3">
                                <fmt:message key="${errors['global']}"/>
                            </div>
                        </c:if>
                        <form action="<c:url value='/login'/>" method="post">

                            <!-- === EMAIL / USERNAME FIELD === -->
                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700">
                                    <fmt:message key="login.label.identity"/>
                                </label>

                                <!-- 1. Thêm class 'is-invalid' nếu có lỗi 'email' -->
                                <!-- 2. Giữ lại value cũ bằng param.email -->
                                <input class="form-control ${not empty errors['email'] ? 'is-invalid' : ''}"
                                       name="email"
                                       type="text"
                                       value="${param.email}"
                                       placeholder="<fmt:message key='login.placeholder.identity'/>">

                                <!-- 3. Hiển thị thông báo lỗi -->
                                <c:if test="${not empty errors['email']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['email']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- === PASSWORD FIELD === -->
                            <div class="form-group">
                                <label class="mb-5 font-sm color-gray-700">
                                    <fmt:message key="login.label.password"/>
                                </label>

                                <input class="form-control ${not empty errors['password'] ? 'is-invalid' : ''}"
                                       name="password"
                                       type="password"
                                       placeholder="******************">

                                <c:if test="${not empty errors['password']}">
                                    <span class="invalid-feedback" style="display:block;">
                                        <fmt:message key="${errors['password']}"/>
                                    </span>
                                </c:if>
                            </div>

                            <!-- Checkbox & Forgot Password -->
                            <div class="row">
                                <div class="col-lg-6">
                                    <div class="form-group">
                                        <label class="color-gray-500 font-xs">
                                            <input class="checkagree" name="remember" type="checkbox">
                                            <fmt:message key="login.remember_me"/>
                                        </label>
                                    </div>
                                </div>
                                <div class="col-lg-6 text-end">
                                    <div class="form-group">
                                        <a class="font-xs color-gray-500" href="#">
                                            <fmt:message key="login.forgot_password"/>
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <!-- Submit Button -->
                            <div class="form-group">
                                <input class="font-md-bold btn btn-buy" type="submit"
                                       value="<fmt:message key='login.btn.submit'/>">
                            </div>

                            <!-- Register Link -->
                            <div class="mt-20">
                                <span class="font-xs color-gray-500 font-medium">
                                    <fmt:message key="login.text.no_account"/>
                                </span>
                                <a class="font-xs color-brand-3 font-medium" href="<c:url value='/register'/>">
                                    <fmt:message key="login.link.register"/>
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="col-lg-5"></div>
            </div>
        </div>
    </section>
    <!-- Newsletter Section -->
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

