<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<fmt:message var="title" key="account.title"/>
<fmt:message var="accountGreetingText" key="account.greeting"/>
<fmt:message var="accountDesc1Text" key="account.desc_1"/>
<fmt:message var="accountDesc2Text" key="account.desc_2"/>


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
                    <li>
                        <a class="font-xs color-gray-500" href="#"> ${title} </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <section class="section-box shop-template mt-30">
        <div class="container box-account-template">
            <h3>${accountGreetingText} <sec:user property="fullName"/></h3>
            <p class="font-md color-gray-500">${accountDesc1Text}<br class="d-none d-lg-block">${accountDesc2Text}</p>
            <div class="box-tabs mb-100">
                <ul class="nav nav-tabs nav-tabs-account" role="tablist">
                    <li><a class="active" href="#tab-orders" data-bs-toggle="tab" role="tab" aria-controls="tab-orders"
                           aria-selected="true"> <fmt:message key="account.tab.orders"/></a></li>
                    <li><a href="#tab-setting" data-bs-toggle="tab" role="tab" aria-controls="tab-setting"
                           aria-selected="true">                            <fmt:message key="account.tab.settings"/></a></li>
                </ul>
                <div class="border-bottom mt-20 mb-40"></div>
                <div class="tab-content mt-30">
                    <div class="active show tab-pane fade" id="tab-orders" role="tabpanel" aria-labelledby="tab-orders">
                        <jw:jawire component="com.nlu.store.modules.order.client.component.OrderTableComponents"/>
                    </div>
                    <div class="tab-pane fade" id="tab-setting" role="tabpanel" aria-labelledby="tab-setting">
                    </div>
                </div>
            </div>
        </div>
    </section>


</t:layout>
