

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>

<fmt:message var="shopPath" key="routes.shop"/>
<fmt:message var="loginPath" key="routes.login"/>
<fmt:message var="registerPath" key="routes.register"/>

<!-- Preloader -->
<div id="preloader-active">
    <div class="preloader d-flex align-items-center justify-content-center">
        <div class="preloader-inner position-relative">
            <div class="text-center">
                <img class="mb-10" src="<c:url value='/static/client/imgs/template/favicon.svg'/>" alt="Ecom">
                <div class="preloader-dots"></div>
            </div>
        </div>
    </div>
</div>

<!-- Topbar -->
<div class="topbar">
    <div class="container-topbar">
        <div class="menu-topbar-left d-none d-xl-block">
            <ul class="nav-small">
                <li><a class="font-xs" href="<c:url value='/about'/>"><fmt:message key="header.topbar.about"/></a></li>
                <li><a class="font-xs" href="<c:url value='/careers'/>"><fmt:message key="header.topbar.careers"/></a></li>
                <li><a class="font-xs" href="<c:url value='/${registerPath}-shop'/>"><fmt:message key="header.topbar.openshop"/></a></li>
            </ul>
        </div>
        <div class="info-topbar text-center d-none d-xl-block">
            <span class="font-xs color-brand-3"><fmt:message key="header.topbar.freeship"/></span>
            <span class="font-sm-bold color-success"> $75.00</span>
        </div>
        <div class="menu-topbar-right">
            <span class="font-xs color-brand-3"><fmt:message key="header.topbar.support"/></span>
            <span class="font-sm-bold color-success"> + 1800 900</span>

            <!-- Language Dropdown -->
            <div class="dropdown dropdown-language">
                <button class="btn dropdown-toggle" id="dropdownPage" type="button" data-bs-toggle="dropdown"
                        aria-expanded="true" data-bs-display="static">
        <span class="dropdown-right font-xs color-brand-3">
            <%-- Logic hiển thị ngôn ngữ đang chọn --%>
            <c:choose>
                <c:when test="${sessionScope.lang == 'vi'}">
                    <img src="<c:url value='/static/client/imgs/template/flag-vi.svg'/>" alt="VN"
                         style="width: 18px; margin-right: 5px;">
                    Tiếng Việt
                </c:when>
                <c:otherwise>
                    <img src="<c:url value='/static/client/imgs/template/flag-en.svg'/>" alt="EN"
                         style="width: 18px; margin-right: 5px;">
                    English
                </c:otherwise>
            </c:choose>
        </span>
                </button>

                <ul class="dropdown-menu dropdown-menu-light" aria-labelledby="dropdownPage">
                    <!-- Tiếng Anh -->
                    <li>
                        <a class="dropdown-item ${sessionScope.lang != 'vi' ? 'active' : ''}"
                           href="#"
                           onclick="changeLanguage('en'); return false;">
                            <img src="<c:url value='/static/client/imgs/template/flag-en.svg'/>" alt="English"> English
                        </a>
                    </li>

                    <!-- Tiếng Việt -->
                    <li>
                        <a class="dropdown-item ${sessionScope.lang == 'vi' ? 'active' : ''}"
                           href="#"
                           onclick="changeLanguage('vi'); return false;">
                            <img src="<c:url value='/static/client/imgs/template/flag-vi.svg'/>" alt="Tiếng Việt"> Tiếng Việt
                        </a>
                    </li>
                </ul>

            </div>

        </div>
    </div>
</div>

<!-- Header -->
<header class="header sticky-bar">
    <div class="container">
        <div class="main-header">
            <div class="header-left">
                <!-- LOGO -->
                <div class="header-logo">
                    <a class="d-flex" href="<c:url value='/'/>">
                        <img alt="Ecom" src="<c:url value='/static/client/imgs/template/logo.svg'/>">
                    </a>
                </div>

                <!-- SEARCH -->
                <div class="header-search">
                    <jw:jawire component="com.nlu.store.modules.catalog.components.SearchComponent"/>
                </div>

                <!-- NAVIGATION MENU -->
                <div class="header-nav">
                    <nav class="nav-main-menu d-none d-xl-block">
                        <ul class="main-menu">
                            <li><a class="active" href="<c:url value='/'/>"><fmt:message key="menu.home"/></a></li>
                            <li><a href="<c:url value='/${shopPath}'/>"><fmt:message key="menu.shop"/></a></li>
                            <li><a href="<c:url value='/vendors'/>"><fmt:message key="menu.vendors"/></a></li>
                            <li><a href="<c:url value='/blog'/>"><fmt:message key="menu.blog"/></a></li>
                            <li><a href="<c:url value='/contact'/>"><fmt:message key="menu.contact"/></a></li>
                        </ul>
                    </nav>
                    <div class="burger-icon burger-icon-white">
                        <span class="burger-icon-top"></span>
                        <span class="burger-icon-mid"></span>
                        <span class="burger-icon-bottom"></span>
                    </div>
                </div>

                <!-- USER ACTIONS (ACCOUNT & CART) -->
                <div class="header-shop">
                    <div class="d-inline-block box-dropdown-cart">
                        <!-- Logic hiển thị User -->
                        <sec:authenticated>
                            <span class="font-lg icon-list icon-account">
                                <span class="font-sm-bold color-brand-3">
                                    <sec:user property="fullName"/>
                                </span>
                            </span>
                            <div class="dropdown-account">
                                <ul>
                                    <li><a href="<c:url value='/account/profile'/>"><fmt:message key="menu.account"/></a></li>
                                    <li><a href="<c:url value='/account/orders'/>"><fmt:message key="menu.account.orders"/></a></li>
                                    <li><a href="<c:url value='/logout'/>"><fmt:message key="menu.logout"/></a></li>
                                </ul>
                            </div>
<%--                            <!-- Wishlist -->--%>
<%--                            <a class="font-lg icon-list icon-wishlist" href="<c:url value='/wishlist'/>">--%>
<%--                                <span>Wishlist</span>--%>
<%--                                <span class="number-item font-xs">5</span>--%>
<%--                            </a>--%>

                            <!-- Cart -->
                        </sec:authenticated>

                        <sec:anonymous>
                            <span class="font-lg icon-list icon-account">
                                <span class="font-sm-bold color-brand-3">Account</span>
                            </span>
                            <div class="dropdown-account">
                                <ul>
                                    <li><a href="<c:url value='/${loginPath}'/>"><fmt:message key="menu.login"/></a></li>
                                    <li><a href="<c:url value='/${registerPath}'/>"><fmt:message key="menu.register"/></a></li>
                                </ul>
                            </div>

                        </sec:anonymous>
                        <div class="d-inline-block">
                            <jw:jawire component="com.nlu.store.modules.catalog.components.HeaderCartComponent" id="cart"/>
                        </div>
                    </div>


                </div>
            </div>
        </div>
    </div>
</header>

<!-- Mobile Header & Sidebar -->
<div class="mobile-header-active mobile-header-wrapper-style perfect-scrollbar">
    <div class="mobile-header-wrapper-inner">
        <div class="mobile-header-content-area">
            <div class="mobile-logo">
                <a class="d-flex" href="<c:url value='/'/>">
                    <img alt="Ecom" src="<c:url value='/static/client/imgs/template/logo.svg'/>">
                </a>
            </div>
            <div class="perfect-scroll">
                <div class="mobile-menu-wrap mobile-header-border">
                    <nav class="mt-15">
                        <ul class="mobile-menu font-heading">
                            <li><a class="active" href="<c:url value='/'/>"><fmt:message key="menu.home"/></a></li>
                            <li><a href="<c:url value='/${shopPath}'/>"><fmt:message key="menu.shop"/></a></li>
                            <li><a href="<c:url value='/vendors'/>"><fmt:message key="menu.vendors"/></a></li>
                            <li><a href="<c:url value='/blog'/>"><fmt:message key="menu.blog"/></a></li>
                            <li><a href="<c:url value='/contact'/>"><fmt:message key="menu.contact"/></a></li>
                        </ul>
                    </nav>
                </div>

                <!-- Mobile Account Section -->
                <div class="mobile-account">
                    <sec:authenticated>
                        <div class="mobile-header-top">
                            <div class="user-account">
                                <a href="<c:url value='/account/profile'/>">
                                    <img src="<c:url value='/static/client/imgs/template/ava_1.png'/>" alt="User">
                                </a>
                                <div class="content">
                                    <h6 class="user-name">
                                        <fmt:message key="header.welcome"/>
                                        <span class="text-brand"><sec:user property="username"/>!</span>
                                    </h6>
                                </div>
                            </div>
                        </div>
                        <ul class="mobile-menu">
                            <li><a href="<c:url value='/account/profile'/>"><fmt:message key="menu.account"/></a></li>
                            <li><a href="<c:url value='/logout'/>"><fmt:message key="menu.logout"/></a></li>
                        </ul>
                    </sec:authenticated>

                    <sec:anonymous>
                        <div class="mobile-header-top">
                            <div class="user-account">
                                <div class="content">
                                    <h6 class="user-name">Guest User</h6>
                                </div>
                            </div>
                        </div>
                        <ul class="mobile-menu">
                            <li><a href="<c:url value='/${loginPath}'/>"><fmt:message key="menu.login"/></a></li>
                            <li><a href="<c:url value='/${registerPath}'/>"><fmt:message key="menu.register"/></a></li>
                        </ul>
                    </sec:anonymous>
                </div>

                <div class="site-copyright color-gray-400 mt-30">
                    Copyright 2022 &copy; Ecom.
                </div>
            </div>
        </div>
    </div>
</div>
