<%@ tag description="Main Layout" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="css" fragment="true" required="false" %>
<%@ attribute name="js" fragment="true" required="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>${title} - Ecomark</title>
    <jsp:include page="/WEB-INF/common/client/head.jsp"/>
    <jsp:invoke fragment="css"/>
</head>
<body>
<jsp:include page="/WEB-INF/common/client/header.jsp"/>

<main class="main">
    <jsp:doBody/>
</main>

<jsp:include page="/WEB-INF/common/client/footer.jsp"/>
<jsp:include page="/WEB-INF/common/client/scripts.jsp"/>
<jsp:invoke fragment="js"/>
</body>
</html>
