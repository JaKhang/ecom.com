<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>

<t:layout title="Reivew" active="reviews">
    <section class="content-main">
    <div class="content-header">
        <div>
            <h2 class="content-title card-title">Reviews</h2>
        </div>

    </div>
    <jw:jawire component="com.nlu.store.modules.catalog.admin.AdminReviewComponent" id="review-table"/>

</t:layout>
