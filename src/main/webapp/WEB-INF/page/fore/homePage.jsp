<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/header.jsp" %>
<head>
    <script src="${pageContext.request.contextPath}/res/js/jquery-color-2.1.2.js"></script>
    <script src="${pageContext.request.contextPath}/res/js/fore/fore_home.js"></script>
    <link href="${pageContext.request.contextPath}/res/css/fore/fore_home.css" rel="stylesheet"/>
    <title>天猫tmall.com--理想生活上天猫</title>
</head>
<body>
<nav>
    <%@ include file="include/navigator.jsp" %>
        <form id="form1" action="${pageContext.request.contextPath}/product" method="get">
        <div id="nva">
            <span style="color: #fff; margin-left: 10%;cursor: pointer; font-size: 18px">TMALL 天猫</span>
            <input type="text" id="nvakuang" name="product_name" placeholder="  搜索 天猫 商品/品牌/店铺" />
            <span style="color: #fff;font-size: 14px;cursor: pointer;" onclick="javascript:$('#form1').submit();">搜 索</span>
        </div>
    </form>
    <div id="leftnva">
        <ul>
            <li style="background-color: #FF0D40; color: #fff; height: 40px; line-height: 40px;">导航</li>
            <li><a href="#1">女装<br>大衣</a></li>
            <li><a href="#2">男装<br>户外</a></li>
            <li><a href="#3">女鞋<br>男鞋</a></li>
            <li><a href="#4">美妆<br>护理</a></li>
            <li ><a href="#5">腕表<br>眼镜</a></li>
            <li ><a href="#6">手机<br>数码</a></li>
            <li ><a href="#8">零食<br>茶酒</a></li>
            <li ><a href="#10">生活<br>电器</a></li>
            <li style="background-color: #ABABAB;" onclick="backtop()"><a href="#1">回到<br>顶部</a></li>
        </ul>
    </div>
    <div class="header">
        <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/HomeLogoA.png">
        <div class="mallSearch">
            <form action="${pageContext.request.contextPath}/product" method="get">
                <div class="mallSearch-input">
                    <input class="header_search_input" type="text" name="product_name" placeholder="搜索 天猫 商品/品牌/店铺"
                           maxlength="50">
                    <input class="header_search_button" type="submit" value="搜索">
                </div>
            </form>
            <ul>
                <c:forEach items="${requestScope.categoryList}" var="category" varStatus="i">
                    <c:if test="${i.index<9}">
                        <li><a href="${pageContext.request.contextPath}/product?category_id=${category.category_id}"
                                <c:if
                                test="${i.index % 2 != 0}"> style="color: #FF0036"</c:if>>${fn:substring(category.category_name,0,fn:indexOf(category.category_name,' /'))}</a>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="home_nav">
        <div class="home_nav_title">
            <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/header_nav_title.png">
            <span>商品分类</span>
        </div>
        <a href="https://chaoshi.tmall.com/" target="_blank"><img
                src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/TB1ztBlaMMPMeJjy1XbXXcwxVXa-200-60.png"></a>
        <a href="https://www.tmall.hk/" target="_blank"><img
                src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/TB1t5ObaBxRMKJjy0FdXXaifFXa-200-60.png"></a>
        <a href="http://vip.tmall.com/" target="_blank">天猫会员</a>
        <a href="https://3c.tmall.com/" target="_blank">电器城</a>
        <a href="https://miao.tmall.com/" target="_blank">喵鲜生</a>
        <a href="http://yao.tmall.com/" target="_blank">医药馆</a>
        <a href="http://wt.tmall.com/" target="_blank">营业厅</a>
        <a href="https://meilihui.tmall.com/" target="_blank">魅力惠</a>
        <a href="https://www.alitrip.com/" target="_blank">飞猪旅行</a>
        <a href="https://suning.tmall.com/" target="_blank">苏宁易购</a>
    </div>
</nav>
<div class="banner">
    <c:forEach var="product" items="${requestScope.specialProductList}" varStatus="i">
        <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/banner/${product.product_id}.jpg"
             name="${product.product_id}" id="banner${i.count}"
             <c:if test="${i.count == 1}">style="display: block;"</c:if> />
    </c:forEach>
</div>
<div class="banner_main">
    <ul class="banner_nav">
        <c:forEach items="${requestScope.categoryList}" var="category">
            <li data-toggle="${category.category_id}" data-status="">
                <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/small/${category.category_id}.png">
                <a href="${pageContext.request.contextPath}/product?category_id=${category.category_id}">${category.category_name}</a>
                <div class="banner_div" name="${category.category_name}">

                </div>
            </li>
        </c:forEach>
    </ul>
    <ul class="banner_slider">
        <li id="slider_1" style="background: rgba(255,255,255,0.4)"></li>
        <li id="slider_2"></li>
        <li id="slider_3"></li>
        <li id="slider_4"></li>
        <li id="slider_5"></li>
        <li id="slider_6"></li>
    </ul>
    <a href="#"></a>
</div>
<div class="banner_do">
    <div class="banner_goods">
        <c:forEach items="${requestScope.categoryList}" var="category">
            <c:if test="${fn:length(category.productList)>0}">
                <div class="banner_goods_type">
                    <div class="banner_goods_title">
                        <span><a name="${category.category_id}"></a></span>
                        <p>${category.category_name}</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/product?category_id=${category.category_id}"><img
                            class="banner_goods_show"
                            src="res/images/fore/WebsiteImage/show/${category.category_id}.jpg"></a>
                    <div class="banner_goods_items">
                        <c:forEach items="${category.productList}" var="product" varStatus="i">
                            <c:if test="${i.index<8}">
                                <div class="banner_goods_item">
                                    <a href="product/${product.product_id}" class="goods_link"></a>
                                    <img src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${product.singleProductImageList[0].productImage_src}">
                                    <a href="product/${product.product_id}"
                                       class="goods_name">${product.product_name}</a>
                                    <span class="goods_price">￥${product.product_sale_price}</span>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
    <div class="endDiv"></div>
</div>
<%@ include file="include/footer_two.jsp" %>
<%@ include file="include/footer.jsp" %>
</body>