<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Trang chủ">

    <!-- Section 1: Banner Hero -->
    <section class="section-box">
        <div class="banner-hero banner-1">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8">
                        <div class="box-swiper">
                            <div class="swiper-container swiper-group-1">
                                <div class="swiper-wrapper">
                                    <div class="swiper-slide">
                                        <!-- Banner Image -->
                                        <div class="banner-big bg-11" style="background-image: url(<c:url value='/static/imgs/page/homepage1/banner.png'/>)">
                                            <span class="font-sm text-uppercase">Hot Right Now</span>
                                            <h2 class="mt-10">Sale Up to 50% Off</h2>
                                            <h1>Mobile Devices</h1>
                                            <div class="mt-30"><a class="btn btn-brand-2" href="shop-grid.html">Shop now</a></div>
                                        </div>
                                    </div>
                                    <!-- Slide 2 -->
                                    <div class="swiper-slide">
                                        <div class="banner-big bg-11-2" style="background-image: url(<c:url value='/static/imgs/page/homepage1/banner-hero-2.png'/>)">
                                            <span class="font-sm text-uppercase">Trending Now</span>
                                            <h2 class="mt-10">Big Sale 25%</h2>
                                            <h1>Laptop & PC</h1>
                                            <div class="mt-30"><a class="btn btn-brand-2" href="shop-grid.html">Shop now</a></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="swiper-pagination swiper-pagination-1"></div>
                            </div>
                        </div>
                    </div>
                    <!-- Banner nhỏ bên phải -->
                    <div class="col-lg-4">
                        <div class="row">
                            <div class="col-lg-12 col-md-6 col-sm-12">
                                <div class="banner-small banner-small-1 bg-13">
                                    <span class="color-danger text-uppercase font-sm-lh32">10%<span class="color-brand-3">Sale Off</span></span>
                                    <h4 class="mb-10">Apple Watch Serial 7</h4>
                                    <div class="mt-20"><a class="btn btn-brand-3 btn-arrow-right" href="shop-grid.html">Shop now</a></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Section 2: Featured Categories -->
    <section class="section-box">
        <div class="container">
            <div class="row">
                <div class="col-lg-5">
                    <h3>Featured Categories</h3>
                </div>
                <div class="col-lg-7">
                    <div class="list-brands">
                        <div class="box-swiper">
                            <div class="swiper-container swiper-group-7">
                                <div class="swiper-wrapper">
                                    <div class="swiper-slide">
                                        <a href="#"><img src="<c:url value='/static/imgs/slider/logo/acer.svg'/>" alt="Ecom"></a>
                                    </div>
                                    <div class="swiper-slide">
                                        <a href="#"><img src="<c:url value='/static/imgs/slider/logo/nokia.svg'/>" alt="Ecom"></a>
                                    </div>
                                    <!-- Thêm các logo khác -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Grid Categories -->
            <div class="mt-50">
                <div class="row">
                    <div class="col-xl-3 col-lg-6 col-md-6 col-sm-12 col-12">
                        <div class="card-grid-style-2 card-grid-style-2-small hover-up">
                            <div class="image-box">
                                <a href="#">
                                    <img src="<c:url value='/static/imgs/page/homepage1/smartphone.png'/>" alt="Ecom">
                                </a>
                            </div>
                            <div class="info-right">
                                <a class="color-brand-3 font-sm-bold" href="#"><h6>Smart Phone</h6></a>
                                <ul class="list-links-disc">
                                    <li><a class="font-sm" href="#">Phone Accessories</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <!-- Thêm các item category khác -->
                </div>
            </div>
        </div>
    </section>

    <!-- Section: Product List (Ví dụ 1 item) -->
    <section class="section-box pt-50">
        <div class="container">
            <div class="head-main">
                <h3 class="mb-5">Best Sellers</h3>
            </div>
            <div class="tab-content">
                <div class="tab-pane fade active show" id="tab-all">
                    <div class="list-products-5">
                        <!-- Product Item -->
                        <div class="card-grid-style-3">
                            <div class="card-grid-inner">
                                <div class="image-box">
                                    <span class="label bg-brand-2">-17%</span>
                                    <a href="#">
                                        <img src="<c:url value='/static/imgs/page/homepage1/imgsp3.png'/>" alt="Ecom">
                                    </a>
                                </div>
                                <div class="info-right">
                                    <a class="font-xs color-gray-500" href="#">Apple</a><br>
                                    <a class="color-brand-3 font-sm-bold" href="#">2022 Apple iMac</a>
                                    <div class="price-info">
                                        <strong class="font-lg-bold color-brand-3 price-main">$2856.3</strong>
                                    </div>
                                    <div class="mt-20 box-btn-cart">
                                        <a class="btn btn-cart" href="#">Add To Cart</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- End Product Item -->
                    </div>
                </div>
            </div>
        </div>
    </section>

</t:layout>
