<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<t:layout title="404">
    <section class="section-box shop-template mt-60">
        <div class="container">
            <div class="text-center mb-150 mt-50">
                <div class="image-404 mb-50"><img src="<c:url value="/static/client/imgs/page/account/404.png"/>" alt="Ecom">
                </div>
                <h3>404 - Page Not Found</h3>
                <p class="font-md-bold color-gray-600">Looks like, page doesn't exist</p>
                <div class="mt-15"><a class="btn btn-buy w-auto arrow-back">Back to Homepage</a></div>
            </div>
        </div>
    </section>
</t:layout>