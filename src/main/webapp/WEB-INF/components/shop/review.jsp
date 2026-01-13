<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="pro" tagdir="/WEB-INF/tags/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<fmt:message key="reviews.action.reply" var="textReply"/>
<fmt:message key="fallback.avatar" var="fallbackAvatar"/>

<jsp:useBean id="component" scope="request" type="com.nlu.store.modules.catalog.components.ReviewsComponent"/>
<c:set var="reviews" scope="request" value="${component.data}"/>
<div class="comment-list">
  <c:choose>
    <c:when test="${not empty reviews}">
      <c:forEach items="${reviews}" var="review">
        <c:set var="userAvatar"
               value="${not empty review.avatar ? review.avatar : fallbackAvatar}"/>
        <div class="single-comment justify-content-between d-flex mb-30 hover-up">
          <div class="user justify-content-between d-flex w-100">
            <div class="thumb text-center">
              <img src="${userAvatar}" class="d-block mb-2"
                   alt="<c:out value='${review.username}'/>"/>
              <a class="font-heading text-brand" href="#">
                <c:out value="${review.username}"/>
              </a>
            </div>
            <div class="desc flex-grow-1">
              <div class="d-flex justify-content-between mb-10">
                <div class="d-flex align-items-center">
                                                                        <span class="font-xs color-gray-700">
                                                                            <fmt:formatDate value="${review.asDate()}"
                                                                                            pattern="hh:mm dd/MM/yyyy"/>
                                                                        </span>
                </div>
                <div class="product-rate d-inline-block">
                  <div class="product-rating"
                       style="width: ${review.rating * 20}%"></div>
                </div>
              </div>
              <c:if test="${review.verified}">
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
                <c:out value="${review.comment}"/>
                <a class="reply" href="#"> ${textReply}</a>
              </p>
            </div>
          </div>
        </div>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <p class="text-muted">
        <fmt:message key="reviews.empty_message"/>
      </p>
    </c:otherwise>
  </c:choose>
</div>
<t:pagination pagination="${component}"/>
