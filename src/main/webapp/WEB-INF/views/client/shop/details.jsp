<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>
<%@ taglib prefix="sec" tagdir="/WEB-INF/tags/auth" %>
<jsp:useBean id="product" scope="request" type="com.nlu.store.modules.catalog.model.SimpleProduct"/>
<fmt:message key="reviews.title.list" var="titleList"/>
<fmt:message key="reviews.action.reply" var="textReply"/>
<fmt:message key="fallback.avatar" var="fallbackAvatar"/>
<c:url value="/static/client/imgs/template/icons/star.svg" var="iconActive"/>
<c:url value="/static/client/imgs/template/icons/star-gray.svg" var="iconInactive"/>
<c:set var="productId" scope="request" value="${product.id}"/>
<%--@elvariable id="currentUserReviews" type="java.util.List<com.nlu.store.modules.catalog.model.review.Review>"--%>
<t:layout title="${product.name}">
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
                    <c:if test="${not empty product}">
                        <li>
                            <a class="font-xs color-gray-500" href="#"> ${product.name} </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>

    <section class="section-box shop-template">
        <jw:jawire component="com.nlu.store.modules.catalog.components.ProductDetailsComponent" id="shop-details"/>
    </section>

    <section class="section-box shop-template">
        <div class="container">
            <div class="pt-30 mb-10">
                <ul class="nav nav-tabs nav-tabs-product" role="tablist">
                    <li>
                        <a class="active" href="#tab-description" data-bs-toggle="tab" role="tab"
                           aria-controls="tab-description" aria-selected="true">
                            <fmt:message key="details.tab.description"/>
                        </a>
                    </li>
                    <li>
                        <a href="#tab-specification" data-bs-toggle="tab" role="tab" aria-controls="tab-specification"
                           aria-selected="true">
                            <fmt:message key="details.tab.specification"/>
                        </a>
                    </li>
                    <li>
                        <a href="#tab-reviews" data-bs-toggle="tab" role="tab" aria-controls="tab-reviews"
                           aria-selected="true">
                            <fmt:message key="details.tab.reviews"/>
                            (<c:out value="${product.extras['reviewsCount']}" default="0"/>)
                        </a>
                    </li>
                </ul>

                <div class="tab-content">
                    <div class="tab-pane fade active show" id="tab-description" role="tabpanel"
                         aria-labelledby="tab-description">
                        <div class="display-text-short"> ${product.description} </div>
                        <div class="mt-20 text-center">
                            <a class="btn btn-border font-sm-bold pl-80 pr-80 btn-expand-more">
                                <fmt:message key="details.button.moreDetails"/>
                            </a>
                        </div>
                    </div>

                    <div class="tab-pane fade" id="tab-specification" role="tabpanel"
                         aria-labelledby="tab-specification">
                        <h5 class="mb-25">
                            <fmt:message key="details.label.specification"/>
                        </h5>
                        <table class="table table-striped">
                            <tbody>
                            <jsp:useBean id="productSpecs" scope="request"
                                         type="java.util.List<com.nlu.store.modules.catalog.model.details.ProductSpecs>"/>
                            <c:forEach items="${productSpecs}" var="spec">
                                <tr>
                                    <td class="${spec.highlight ? 'fw-bold' : ''}"> ${spec.attributeLabel} </td>
                                    <td>
                                            ${spec.value}
                                        <c:if test="${not empty spec.unit}">
                                            <span class="text-muted">${spec.unit}</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty productSpecs}">
                                <tr>
                                    <td colspan="2" class="text-center italic">
                                        <fmt:message key="details.message.updatingSpecs"/>
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>

                    <div class="tab-pane fade" id="tab-reviews" role="tabpanel" aria-labelledby="tab-reviews">
                        <div class="comments-area">
                            <div class="row">
                                <div class="col-lg-8">
                                    <!-- --- START: PHẦN ĐÁNH GIÁ CỦA BẠN --- -->
                                    <sec:authenticated>
                                        <!-- Kiểm tra biến currentUserReview (cần truyền từ Controller) -->
                                        <c:if test="${not empty currentUserReviews}">
                                            <div class="mb-40 p-4 border rounded" style="background-color: #f8f9fa;">
                                                <div class="d-flex justify-content-between align-items-center mb-20">
                                                    <h4 class="text-brand mb-0">Đánh giá của bạn</h4>
                                                </div>
                                                <c:forEach items="${currentUserReviews}" var="currentUserReview">
                                                    <div class="single-comment justify-content-between d-flex hover-up mb-0">
                                                        <div class="user justify-content-between d-flex w-100">
                                                            <div class="thumb text-center">
                                                                <img src="${not empty currentUserReview.avatar ? currentUserReview.avatar : fallbackAvatar}"
                                                                     class="d-block mb-2"
                                                                     alt="<c:out value='${currentUserReview.username}'/>"/>
                                                                <a class="font-heading text-brand" href="#">
                                                                    <c:out value="${currentUserReview.username}"/>
                                                                </a>
                                                            </div>
                                                            <div class="desc flex-grow-1">
                                                                <div class="d-flex justify-content-between mb-10">
                                                                    <div class="d-flex align-items-center">
                                <span class="font-xs color-gray-700">
                                    <fmt:formatDate value="${currentUserReview.asDate()}"
                                                    pattern="hh:mm dd/MM/yyyy"/>
                                </span>
                                                                    </div>
                                                                    <div class="product-rate d-inline-block">
                                                                        <div class="product-rating"
                                                                             style="width: ${currentUserReview.rating * 20}%"></div>
                                                                    </div>
                                                                </div>

                                                                <!-- Check đã mua hàng -->
                                                                <c:if test="${currentUserReview.verified}">
                                                                    <div class="my-1">
                                <span class="font-xs text-success fw-bold d-flex align-items-center">
                                    <img src="<c:url value='/static/client/imgs/template/icons/cicle-check.svg'/>"
                                         alt="Verified" class="mr-5"
                                         style="width: 18px; height: 18px;min-width: unset"/>
                                    <fmt:message key="reviews.verified"/>
                                </span>
                                                                    </div>
                                                                </c:if>

                                                                <p class="mb-10 font-sm color-gray-900">
                                                                    <c:out value="${currentUserReview.comment}"/>
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:if>
                                    </sec:authenticated>

                                    <h4 class="mb-30">${titleList}</h4>
                                    <jw:jawire component="com.nlu.store.modules.catalog.components.ReviewsComponent"/>
                                </div>

                                <div class="col-lg-4">
                                    <h4 class="mb-30 title-question">Customer reviews</h4>
                                    <div class="d-flex mb-30">
                                        <div class="product-rate d-inline-block mr-15">
                                            <div class="product-rating" style="width: 90%"></div>
                                        </div>
                                        <h6>4.8 out of 5</h6>
                                    </div>
                                </div>

                                <div class="col-12 comment-form">
                                    <h4 class="mb-15">
                                        <fmt:message key="reviews.form.title"/>
                                    </h4>
                                    <sec:authenticated>
                                        <c:url var="action" value="/reviews">
                                            <c:param name="redirectUrl"
                                                     value="${requestScope['jakarta.servlet.forward.request_uri']}"/>
                                        </c:url>
                                        <form class="form-contact comment_form" action="${action}" method="post"
                                              id="commentForm">
                                            <input type="hidden" name="productId" value="${product.id}"/>
                                            <div class="row">
                                                <div class="col-12">
                                                    <div x-data="{
                                                            rating: 5,
                                                            hoverRating: 0,
                                                            activeIcon: '${iconActive}', /* Ảnh sao vàng */
                                                            inactiveIcon: '${iconInactive}' /* Ảnh sao xám */
                                                            }" class="star-rating-alpine mb-3">
                                                        <input type="hidden" name="rating" :value="rating">
                                                        <div class="d-flex align-items-center"
                                                             @mouseleave="hoverRating = 0">
                                                            <template x-for="i in 5">
                                                                <div class="d-inline-block p-1 cursor-pointer"
                                                                     @click="rating = i" @mouseover="hoverRating = i">
                                                                    <img width="28px"
                                                                         :src="(hoverRating >= i || (hoverRating === 0 && rating >= i)) ? activeIcon : inactiveIcon"
                                                                         alt="star" style="transition: transform 0.1s;"
                                                                         :style="hoverRating === i ? 'transform: scale(1.2)' : ''">
                                                                </div>
                                                            </template>
                                                            <span class="ms-2 font-sm text-muted"
                                                                  x-text="(hoverRating || rating) + '/5'"></span>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-12">
                                                    <div class="form-group">
                                                        <textarea class="form-control w-100" name="comment" id="comment"
                                                                  cols="30" rows="9"
                                                                  placeholder="<fmt:message key='reviews.form.placeholder' />"
                                                                  required></textarea>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <button type="submit" class="btn btn-buy">
                                                    <fmt:message key="reviews.form.submit"/>
                                                </button>
                                            </div>
                                        </form>
                                    </sec:authenticated>
                                    <sec:anonymous>
                                        <fmt:message key="routes.login" var="loginPath"/>
                                        <c:url var="loginUrl" value="/${loginPath}">
                                            <c:param name="redirectUrl"
                                                     value="${requestScope['jakarta.servlet.forward.request_uri']}"/>
                                        </c:url>
                                        <div class="alert alert-warning mt-30 d-flex align-items-center">
                                            <i class="fi-rs-info mr-10"></i>

                                            <span>
                                                <fmt:message key="reviews.login_required"/>
                                                <a href="${loginUrl}"
                                                   class="text-brand fw-bold ms-2">
                                                    <fmt:message key="menu.login"/>
                                                </a>
                                            </span>
                                        </div>
                                    </sec:anonymous>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</t:layout>
