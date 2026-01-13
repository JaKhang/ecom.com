<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<t:layout title="Shop Grid">

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

                    <c:if test="${not empty category}">
                        <fmt:message key="routes.category" var="pathCategory"/>
                        <c:set var="baseCatPath" value="${not empty pathCategory ? pathCategory : 'category'}"/>
                        <li>
                            <a class="font-xs color-gray-500" href="<c:url value='/${baseCatPath}/${category.slug}'/>">
                                    ${category.name}
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>



    <!-- 3. MAIN CONTENT AREA -->
    <div class="section-box shop-template mt-30">
        <div class="container">
            <jw:jawire component="com.nlu.store.modules.catalog.components.ShopGridComponent"/>
        </div>
    </div>

    <!-- 4. BOTTOM FEATURES (I18N) -->
    <section class="section-box mt-90 mb-50">
        <div class="container">
            <ul class="list-col-5">
                <!-- Delivery -->
                <li>
                    <div class="item-list">
                        <div class="icon-left"><img src="<c:url value="/static/client/imgs/template/delivery.svg"/>"
                                                    alt="Ecom"></div>
                        <div class="info-right">
                            <h5 class="font-lg-bold color-gray-100"><fmt:message key="feature.delivery.title"/></h5>
                            <p class="font-sm color-gray-500"><fmt:message key="feature.delivery.desc"/></p>
                        </div>
                    </div>
                </li>
                <!-- Support -->
                <li>
                    <div class="item-list">
                        <div class="icon-left"><img src="<c:url value="/static/client/imgs/template/support.svg"/>" alt="Ecom">
                        </div>
                        <div class="info-right">
                            <h5 class="font-lg-bold color-gray-100"><fmt:message key="feature.support.title"/></h5>
                            <p class="font-sm color-gray-500"><fmt:message key="feature.support.desc"/></p>
                        </div>
                    </div>
                </li>
                <!-- Voucher -->
                <li>
                    <div class="item-list">
                        <div class="icon-left"><img src="<c:url value="/static/client/imgs/template/voucher.svg"/>" alt="Ecom">
                        </div>
                        <div class="info-right">
                            <h5 class="font-lg-bold color-gray-100"><fmt:message key="feature.voucher.title"/></h5>
                            <p class="font-sm color-gray-500"><fmt:message key="feature.voucher.desc"/></p>
                        </div>
                    </div>
                </li>
                <!-- Return -->
                <li>
                    <div class="item-list">
                        <div class="icon-left"><img src="<c:url value="/static/client/imgs/template/return.svg"/>" alt="Ecom">
                        </div>
                        <div class="info-right">
                            <h5 class="font-lg-bold color-gray-100"><fmt:message key="feature.return.title"/></h5>
                            <p class="font-sm color-gray-500"><fmt:message key="feature.return.desc"/></p>
                        </div>
                    </div>
                </li>
                <!-- Secure -->
                <li>
                    <div class="item-list">
                        <div class="icon-left"><img src="<c:url value="/static/client/imgs/template/secure.svg"/>" alt="Ecom">
                        </div>
                        <div class="info-right">
                            <h5 class="font-lg-bold color-gray-100"><fmt:message key="feature.secure.title"/></h5>
                            <p class="font-sm color-gray-500"><fmt:message key="feature.secure.desc"/></p>
                        </div>
                    </div>
                </li>
            </ul>
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

    <div class="modal fade" id="ModalQuickview" tabindex="-1" aria-hidden="true" style="display: none;">
        <div class="modal-dialog modal-xl">
            <div class="modal-content apply-job-form">
                <button class="btn-close" type="button" data-bs-dismiss="modal" aria-label="Close"></button>
                <div class="modal-body p-30">
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="gallery-image">
                                <div class="galleries-2">
                                    <div class="detail-gallery">
                                        <div class="product-image-slider-2">
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-1.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-2.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-3.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-4.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-5.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-6.jpg" alt="product image"></figure>
                                            <figure class="border-radius-10"><img src="assets/imgs/page/product/img-gallery-7.jpg" alt="product image"></figure>
                                        </div>
                                    </div>
                                    <div class="slider-nav-thumbnails-2">
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-1.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-2.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-3.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-4.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-5.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-6.jpg" alt="product image"></div>
                                        </div>
                                        <div>
                                            <div class="item-thumb"><img src="assets/imgs/page/product/img-gallery-7.jpg" alt="product image"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="box-tags">
                                <div class="d-inline-block mr-25"><span class="font-sm font-medium color-gray-900">Category:</span><a class="link" href="#">Smartphones</a></div>
                                <div class="d-inline-block"><span class="font-sm font-medium color-gray-900">Tags:</span><a class="link" href="#">Blue</a>,<a class="link" href="#">Smartphone</a></div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="product-info">
                                <h5 class="mb-15">SAMSUNG Galaxy S22 Ultra, 8K Camera & Video, Brightest Display Screen, S Pen Pro</h5>
                                <div class="info-by"><span class="bytext color-gray-500 font-xs font-medium">by</span><a class="byAUthor color-gray-900 font-xs font-medium" href="shop-vendor-list.html"> Ecom Tech</a>
                                    <div class="rating d-inline-block"><img src="assets/imgs/template/icons/star.svg" alt="Ecom"><img src="assets/imgs/template/icons/star.svg" alt="Ecom"><img src="assets/imgs/template/icons/star.svg" alt="Ecom"><img src="assets/imgs/template/icons/star.svg" alt="Ecom"><img src="assets/imgs/template/icons/star.svg" alt="Ecom"><span class="font-xs color-gray-500 font-medium"> (65 reviews)</span></div>
                                </div>
                                <div class="border-bottom pt-10 mb-20"></div>
                                <div class="box-product-price">
                                    <h3 class="color-brand-3 price-main d-inline-block mr-10">$2856.3</h3><span class="color-gray-500 price-line font-xl line-througt">$3225.6</span>
                                </div>
                                <div class="product-description mt-10 color-gray-900">
                                    <ul class="list-dot">
                                        <li>8k super steady video</li>
                                        <li>Nightography plus portait mode</li>
                                        <li>50mp photo resolution plus bright display</li>
                                        <li>Adaptive color contrast</li>
                                        <li>premium design & craftmanship</li>
                                        <li>Long lasting battery plus fast charging</li>
                                    </ul>
                                </div>
                                <div class="box-product-color mt-10">
                                    <p class="font-sm color-gray-900">Color:<span class="color-brand-2 nameColor">Pink Gold</span></p>
                                    <ul class="list-colors">
                                        <li class="disabled"><img src="assets/imgs/page/product/img-gallery-1.jpg" alt="Ecom" title="Pink"></li>
                                        <li><img src="assets/imgs/page/product/img-gallery-2.jpg" alt="Ecom" title="Gold"></li>
                                        <li><img src="assets/imgs/page/product/img-gallery-3.jpg" alt="Ecom" title="Pink Gold"></li>
                                        <li><img src="assets/imgs/page/product/img-gallery-4.jpg" alt="Ecom" title="Silver"></li>
                                        <li class="active"><img src="assets/imgs/page/product/img-gallery-5.jpg" alt="Ecom" title="Pink Gold"></li>
                                        <li class="disabled"><img src="assets/imgs/page/product/img-gallery-6.jpg" alt="Ecom" title="Black"></li>
                                        <li class="disabled"><img src="assets/imgs/page/product/img-gallery-7.jpg" alt="Ecom" title="Red"></li>
                                    </ul>
                                </div>
                                <div class="box-product-style-size mt-10">
                                    <div class="row">
                                        <div class="col-lg-12 mb-10">
                                            <p class="font-sm color-gray-900">Style:<span class="color-brand-2 nameStyle">S22</span></p>
                                            <ul class="list-styles">
                                                <li class="disabled" title="S22 Ultra">S22 Ultra</li>
                                                <li class="active" title="S22">S22</li>
                                                <li title="S22 + Standing Cover">S22 + Standing Cover</li>
                                            </ul>
                                        </div>
                                        <div class="col-lg-12 mb-10">
                                            <p class="font-sm color-gray-900">Size:<span class="color-brand-2 nameSize">512GB</span></p>
                                            <ul class="list-sizes">
                                                <li class="disabled" title="1GB">1GB</li>
                                                <li class="active" title="512 GB">512 GB</li>
                                                <li title="256 GB">256 GB</li>
                                                <li title="128 GB">128 GB</li>
                                                <li class="disabled" title="64GB">64GB</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="buy-product mt-5">
                                    <p class="font-sm mb-10">Quantity</p>
                                    <div class="box-quantity">
                                        <div class="input-quantity">
                                            <input class="font-xl color-brand-3" type="text" value="1"><span class="minus-cart"></span><span class="plus-cart"></span>
                                        </div>
                                        <div class="button-buy"><a class="btn btn-cart" href="shop-cart.html">Add to cart</a><a class="btn btn-buy" href="shop-checkout.html">Buy now</a></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
