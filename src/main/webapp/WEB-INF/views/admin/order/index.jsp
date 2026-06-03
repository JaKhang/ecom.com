<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="jw" uri="/WEB-INF/jwire.tld" %>

<t:layout title="Order" active="orders">
    <div class="content-header">
        <div>
            <h2 class="content-title card-title">Order List</h2>
            <p>Lorem ipsum dolor sit amet.</p>
        </div>

    </div>
    <jw:jawire component="com.nlu.store.modules.order.admin.controller.AdminOrderComponent" id="order-table"/>

</t:layout>
