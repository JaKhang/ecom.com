<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<fmt:message var="title" key="cart.title"/>


<t:layout title="${title}">
    <!-- 1. BREADCRUMBS -->
    <div class="section-box">
        <div class="breadcrumbs-div">
            <div class="container">
                <ul class="breadcrumb">
                    <li>
                        <a class="font-xs color-gray-1000" href="<c:url value='/'/>">
                            <fmt:message key="menu.home"/>
                        </a>
                    </li>
                    <fmt:message key="routes.shop" var="pathShop"/>
                    <c:set var="baseShopPath" value="${not empty pathShop ? pathShop : 'shop'}"/>
                    <li>
                        <a class="font-xs color-gray-500" href="<c:url value='/${baseShopPath}'/>">
                            <fmt:message key="shop.breadcrumb.shop"/>
                        </a>
                    </li>
                    <li>
                        <a class="font-xs color-gray-500" href="#"> ${title} </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <section class="section-box shop-template">
        <div class="container">
            <jw:jawire component="com.nlu.store.modules.catalog.components.PageCartComponent" id="cart-list"/>
        </div>
    </section>


</t:layout>
