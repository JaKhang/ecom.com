<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key='home.title' var="title"/>
<fmt:message key='routes.category' var="categoryPath"/>
<fmt:message key='fallback.image' var="fallbackImage"/>
<jsp:useBean id="categories" scope="request" type="java.util.List<com.nlu.store.modules.catalog.model.SimpleCategory>"/>
<jsp:useBean id="brands" scope="request" type="java.util.List<com.nlu.store.modules.catalog.model.Brand>"/>

<t:layout title="${title}">
    <section class="section-box">
        <div class="banner-hero banner-1">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8">
                        <div class="box-swiper">
                            <div class="swiper-container swiper-group-1">
                                <div class="swiper-wrapper">
                                    <!-- Slide 1 -->
                                    <div class="swiper-slide">
                                        <div class="banner-big bg-11"
                                             style="background-image: url(/static/client/imgs/page/homepage1/banner.png)">
                                            <span class="font-sm text-uppercase"><fmt:message key="home.banner.slide1.tag"/></span>
                                            <h2 class="mt-10"><fmt:message key="home.banner.slide1.subtitle"/></h2>
                                            <h1><fmt:message key="home.banner.slide1.title"/></h1>
                                            <div class="row">
                                                <div class="col-lg-5 col-md-7 col-sm-12">
                                                    <p class="font-sm color-brand-3">
                                                        <fmt:message key="home.banner.slide1.desc"/>
                                                    </p>
                                                </div>
                                            </div>
                                            <div class="mt-30">
                                                <a class="btn btn-brand-2" href="shop-grid.html"><fmt:message key="home.banner.btn.shop_now"/></a>
                                                <a class="btn btn-link" href="shop-grid.html"><fmt:message key="home.banner.btn.learn_more"/></a>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Slide 2 -->
                                    <div class="swiper-slide">
                                        <div class="banner-big bg-11-2"
                                             style="background-image: url(/static/client/imgs/page/homepage1/banner-hero-2.png)">
                                            <span class="font-sm text-uppercase"><fmt:message key="home.banner.slide2.tag"/></span>
                                            <h2 class="mt-10"><fmt:message key="home.banner.slide2.subtitle"/></h2>
                                            <h1><fmt:message key="home.banner.slide2.title"/></h1>
                                            <div class="row">
                                                <div class="col-lg-5 col-md-7 col-sm-12">
                                                    <p class="font-sm color-brand-3">
                                                        <fmt:message key="home.banner.slide2.desc"/>
                                                    </p>
                                                </div>
                                            </div>
                                            <div class="mt-30">
                                                <a class="btn btn-brand-2" href="shop-grid.html"><fmt:message key="home.banner.btn.shop_now"/></a>
                                                <a class="btn btn-link" href="shop-grid.html"><fmt:message key="home.banner.btn.learn_more"/></a>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Slide 3 -->
                                    <div class="swiper-slide">
                                        <div class="banner-big bg-11-3"
                                             style="background-image: url(/static/client/imgs/page/homepage1/banner-hero-3.png)">
                                            <span class="font-sm text-uppercase"><fmt:message key="home.banner.slide3.tag"/></span>
                                            <h2 class="mt-10"><fmt:message key="home.banner.slide3.subtitle"/></h2>
                                            <h1><fmt:message key="home.banner.slide3.title"/></h1>
                                            <div class="row">
                                                <div class="col-lg-5 col-md-7 col-sm-12">
                                                    <p class="font-sm color-brand-3">
                                                        <fmt:message key="home.banner.slide3.desc"/>
                                                    </p>
                                                </div>
                                            </div>
                                            <div class="mt-30">
                                                <a class="btn btn-brand-2" href="shop-grid.html"><fmt:message key="home.banner.btn.shop_now"/></a>
                                                <a class="btn btn-link" href="shop-grid.html"><fmt:message key="home.banner.btn.learn_more"/></a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="swiper-pagination swiper-pagination-1"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="row">
                            <!-- Small Banner 1 -->
                            <div class="col-lg-12 col-md-6 col-sm-12">
                                <div class="banner-small banner-small-1 bg-13">
                                    <span class="color-danger text-uppercase font-sm-lh32">10% <span class="color-brand-3"><fmt:message key="home.banner.small.sale_off"/></span></span>
                                    <h4 class="mb-10"><fmt:message key="home.banner.small1.title"/></h4>
                                    <p class="color-brand-3 font-desc">
                                        <fmt:message key="home.banner.small.desc"/>
                                    </p>
                                    <div class="mt-20">
                                        <a class="btn btn-brand-3 btn-arrow-right" href="shop-grid.html"><fmt:message key="home.banner.btn.shop_now"/></a>
                                    </div>
                                </div>
                            </div>
                            <!-- Small Banner 2 -->
                            <div class="col-lg-12 col-md-6 col-sm-12">
                                <div class="banner-small banner-small-2 bg-14">
                                    <span class="color-danger text-uppercase font-sm-lh32"><fmt:message key="home.banner.small2.tag"/></span>
                                    <h4 class="mb-10"><fmt:message key="home.banner.small2.title"/></h4>
                                    <p class="color-brand-3 font-md">
                                        <fmt:message key="home.banner.small.desc"/>
                                    </p>
                                    <div class="mt-20">
                                        <a class="btn btn-brand-2 btn-arrow-right" href="shop-grid.html"><fmt:message key="home.banner.btn.shop_now"/></a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <div class="section-box">
        <div class="container">
            <div class="list-brands list-none-border">
                <div class="box-swiper">
                    <div class="swiper-container swiper-group-10">
                        <div class="swiper-wrapper">
                            <c:forEach var="brand" items="${brands}">
                                <div class="swiper-slide"><a href=""><img src="${brand.logo != null ? brand.logo : fallbackImage}" alt="Ecom"></a></div>

                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <section class="section-box mt-30">
        <div class="container">
            <div class="border-bottom pb-25 head-main">
                <h3><fmt:message key="home.featured_categories.title"/></h3>
                <p class="font-base"><fmt:message key="home.featured_categories.subtitle"/></p>
                <!-- Button slider-->
                <div class="box-button-slider">
                    <div class="swiper-button-next swiper-button-next-group-2"></div>
                    <div class="swiper-button-prev swiper-button-prev-group-2"></div>
                </div>
                <!-- End Button slider-->
            </div>

            <div class="mt-10">
                <div class="box-swiper">
                    <div class="swiper-container swiper-group-style-2">
                        <div class="swiper-wrapper pt-5">
                            <div class="swiper-slide">
                                <ul class="list-9-col">
                                    <c:forEach items="${categories}" var="cat">
                                        <c:url var="categoryUrl" value="/${categoryPath}/${cat.slug}"/>
                                        <c:set var="catImage" value="${cat.icon == null ? fallbackImage : cat.icon}"/>
                                        <li class="" style="width: 25%">
                                            <div class="box-category hover-up">
                                                <div class="image"><a href="${categoryUrl}"><img src="${catImage}" alt="Ecom"></a></div>
                                                <div class="text-info"><a class="font-sm color-gray-900 font-bold" href="${categoryUrl}">${cat.name}</a>
                                                    <p class="font-xs color-gray-500">${cat.productCount} <fmt:message key="shop.category.product_count_label"/></p>
                                                </div>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="section-box pt-50">
        <div class="container">
            <div class="head-main">
                <div class="row">
                    <div class="col-xl-7 col-lg-6">
                        <h3 class="mb-5"><fmt:message key="home.new_product.title"/></h3>
                        <p class="font-base color-gray-500">
                            <fmt:message key="home.new_product.subtitle"/>
                        </p>
                    </div>
                    <div class="col-xl-5 col-lg-6">
                        <ul class="nav nav-tabs" role="tablist">
                            <li>
                                <a
                                        class="active"
                                        href="#tab-all"
                                        data-bs-toggle="tab"
                                        role="tab"
                                        aria-controls="tab-all"
                                        aria-selected="true"
                                >All</a>

                        </ul>
                    </div>
                </div>
            </div>
            <div class="tab-content">
                <div
                        class="tab-pane fade active show"
                        id="tab-all"
                        role="tabpanel"
                        aria-labelledby="tab-all"
                >
                    <pro:product-list products="${news}"/>
                </div>

            </div>
        </div>
    </section>

    <section class="section-box pt-50">
        <div class="container">
            <div class="row">
                <!-- Block 1: Headphone -->
                <div class="col-lg-3 mb-20">
                    <div class="bg-4 box-bdrd-4 bg-headphone">
                        <span class="font-md color-brand-3"><fmt:message key="home.promo.headphone.tag"/></span>
                        <h4 class="font-32 color-gray-1000 mb-10 mt-5"><fmt:message key="home.promo.headphone.product"/></h4>
                        <p class="color-brand-1 font-sm">
                            <fmt:message key="home.promo.headphone.desc"/>
                        </p>
                        <div class="mt-35">
                            <a class="btn btn-brand-2 btn-arrow-right" href="shop-grid.html"><fmt:message key="home.promo.btn.shop_now"/></a>
                        </div>
                    </div>
                </div>

                <!-- Block 2: Weekly Deal -->
                <div class="col-lg-5 col-md-6 col-sm-6 mb-20">
                    <div class="bg-6 box-bdrd-4 bg-watch text-center">
                        <h4 class="font-33 color-gray-1000"><fmt:message key="home.promo.weekly_deal.title"/></h4>
                        <p class="font-18">
                            <fmt:message key="home.promo.weekly_deal.prefix"/>
                            <strong class="font-24 color-brand-2">$252.00</strong>
                            <fmt:message key="home.promo.weekly_deal.suffix"/>
                        </p>
                    </div>
                </div>

                <!-- Block 3: Surface Pro -->
                <div class="col-lg-4 col-md-6 col-sm-6 mb-20">
                    <div class="bg-5 box-bdrd-4 bg-ipad text-center">
                        <span class="font-sm color-brand-3"><fmt:message key="home.promo.surface.tag"/></span>
                        <h4 class="font-xl color-gray-1000">
                            <fmt:message key="home.promo.surface.title_prefix"/>
                            <br class="d-none d-lg-block">
                            <span class="color-brand-1"><fmt:message key="home.promo.surface.product"/></span>
                            <fmt:message key="home.promo.surface.year"/>
                        </h4>
                        <div class="mt-15">
                            <a class="btn btn-brand-2 btn-arrow-right" href="shop-grid.html"><fmt:message key="home.promo.btn.shop_now"/></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- 5. NEWSLETTER (I18N) -->
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
                            <c:set var="phEmail"><fmt:message key="newsletter.placeholder"/></c:set>
                            <input class="input-newsletter font-xs" value="" placeholder="${phEmail}">
                            <button class="btn btn-brand-2"><fmt:message key="newsletter.btn"/></button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</t:layout>
