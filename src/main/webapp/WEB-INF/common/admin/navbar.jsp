<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="navbar-aside" id="offcanvas_aside">
    <div class="aside-top">
        <a class="brand-wrap" href="index-2.html"
        ><img
                class="logo"
                src="<c:url value="/static/admin/imgs/theme/logo.svg"/>"
                alt="Evara Dashboard"
        /></a>
        <div>
            <button class="btn btn-icon btn-aside-minimize">
                <i class="text-muted material-icons md-menu_open"></i>
            </button>
        </div>
    </div>
    <nav>
        <ul class="menu-aside">
            <li class="menu-item ${active == 'dashboard' ? 'active' : ''}">
                <a class="menu-link" href="index-2.html"
                ><i class="icon material-icons md-home"></i
                ><span class="text">Dashboard</span></a
                >
            </li>
            <li class="menu-item ${active == 'orders' ? 'active' : ''} has-submenu">
                <a class="menu-link" href="page-orders-1.html"
                ><i class="icon material-icons md-shopping_cart"></i
                ><span class="text">Orders</span></a
                >
                <div class="submenu">
                    <a href="${pageContext.request.contextPath}/admin/orders">Order list</a>
                </div>
            </li>
            <li class="menu-item ${active == 'reviews' ? 'active' : ''}">
                <a class="menu-link" href="<c:url value="/admin/reviews"/>"
                ><i class="icon material-icons md-comment"></i
                ><span class="text">Reviews</span></a
                >
            </li>

        </ul>
    </nav>
</aside>