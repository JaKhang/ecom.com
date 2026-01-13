<%@ tag description="Main Layout" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="css" fragment="true" required="false" %>
<%@ attribute name="js" fragment="true" required="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>${title} - Ecomark</title>
    <jsp:include page="/WEB-INF/common/admin/head.jsp"/>
    <jsp:invoke fragment="css"/>
</head>
<body>

<div class="screen-overlay"></div>
<!--Nav bar-->
<jsp:include page="/WEB-INF/common/admin/navbar.jsp"/>

<main class="main-wrap">
    <jsp:include page="/WEB-INF/common/admin/header.jsp"/>
    <section class="content-main">
        <jsp:doBody/>
    </section>
    <jsp:include page="/WEB-INF/common/admin/footer.jsp"/>

</main>

<jsp:include page="/WEB-INF/common/admin/scripts.jsp"/>
<jsp:include page="/WEB-INF/common/client/alert.jsp"/>
<jsp:invoke fragment="js"/>
</body>
</html>
