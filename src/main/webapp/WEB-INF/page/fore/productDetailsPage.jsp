<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/header.jsp" %>
<head>
    <script src="${pageContext.request.contextPath}/res/js/fore/fore_login.js"></script>
    <script src="${pageContext.request.contextPath}/res/js/fore/fore_productDetails.js"></script>
    <link href="${pageContext.request.contextPath}/res/css/fore/fore_productDetails.css" rel="stylesheet">
    <title>${requestScope.product.product_name}-tmall.com天猫</title>
</head>
<body>
<nav>
    <%@ include file="include/navigator.jsp" %>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img
                src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/tmallLogoB.png"></a>
        <span class="shopNameHeader">贤趣${requestScope.product.product_category.category_name}官方旗舰店</span>
        <input id="tid" type="hidden" value="${requestScope.product.product_category.category_id}"/>
        <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/detailsHeaderA.png"
             class="shopAssessHeader">
        <div class="shopSearchHeader">
            <form action="${pageContext.request.contextPath}/product" method="get">
                <div class="shopSearchInput">
                    <input type="text" class="searchInput" name="product_name" placeholder="搜索 天猫 商品/品牌/店铺"
                           maxlength="50">
                    <input type="submit" value="搜天猫" class="searchTmall">
                </div>
                <input type="submit" value="搜本店" class="searchShop">
            </form>
            <ul>
                <c:forEach items="${requestScope.categoryList}" var="category" varStatus="i">
                    <li>
                        <a href="${pageContext.request.contextPath}/product?category_id=${category.category_id}">${category.category_name}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</nav>
<div class="loginModel"></div>
<div class="loginDiv">
    <div class="loginDivHeader">
        <a href="javascript:void(0)" class="closeLoginDiv"></a>
    </div>
    <div class="loginSwitch" id="loginSwitch"></div>
    <div class="loginMessage">
        <div class="loginMessageMain">
            <div class="poptip-arrow"><em></em><span></span></div>
            <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/scan-safe.png"/><span>扫码登录更安全</span>
        </div>
    </div>
    <div class="pwdLogin">
        <span class="loginTitle">密码登录</span>
        <form method="post" class="loginForm">
            <div class="loginInputDiv">
                <label for="name" class="loginLabel"><img
                        src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/2018-04-27_235518.png"
                        width="38px" height="39px" title="会员名"/></label>
                <input type="text" name="name" id="name" class="loginInput" placeholder="会员名/邮箱/手机号">
            </div>
            <div class="loginInputDiv">
                <label for="password" class="loginLabel"><img
                        src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/2018-04-27_235533.png"
                        width="38px" height="39px" title="登录密码"/></label>
                <input type="password" name="password" id="password" class="loginInput">
            </div>
            <input type="submit" class="loginButton" value="登 录">
        </form>
        <div class="loginLinks">
            <a href="#">忘记密码</a>
            <a href="#">忘记会员名</a>
            <a href="${pageContext.request.contextPath}/register" target="_blank">免费注册</a>
        </div>
        <div class="error_message">
            <p id="error_message_p"></p>
        </div>
    </div>
    <div class="qrcodeLogin">
        <span class="loginTitle">手机扫码，安全登录</span>
        <div class="qrcodeMain">
            <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/login_qrcode.png"
                 id="qrcodeA"/>
            <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/login_qrcodeB.png"
                 id="qrcodeB"/>
        </div>
        <div class="qrcodeFooter">
            <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/scan_icon2.png">
            <p>打开 <a href="https://www.tmall.com/wow/portal/act/app-download">手机天猫</a> | <a
                    href="https://www.taobao.com/m">手机淘宝</a>扫一扫登录</p>
        </div>
        <div class="loginLinks">
            <a href="JavaScript:void(0)" id="pwdLogin">密码登录</a>
            <a href="${pageContext.request.contextPath}/register" target="_blank">免费注册</a>
        </div>
    </div>
</div>
<div class="shopImg">
    <img src="${pageContext.request.contextPath}/res/images/item/categoryPicture/${requestScope.product.product_category.category_image_src}">
</div>
<div class="context">
    <div class="context_left">
        <div class="context_img_ks">
            <img src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${requestScope.product.singleProductImageList[0].productImage_src}"
                 width="800px" height="800px">
        </div>
        <div class="context_img">
            <img src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${requestScope.product.singleProductImageList[0].productImage_src}"
                 class="context_img_main" width="400px" height="400px"/>
            <div class="context_img_winSelector"></div>
        </div>
        <ul class="context_img_ul">
            <c:forEach var="img" items="${requestScope.product.singleProductImageList}">
                <li class="context_img_li"><img
                        src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${img.productImage_src}"/>
                </li>
            </c:forEach>
        </ul>
        <a href="javascript:void(0)" style="text-decoration: none;color: #888888;font-size: 12px;">∈分享</a>

        <c:if test="${requestScope.isCollect}">
            <a href="javascript:void(0)" onclick="offproductcollect()" style="margin-left: 40px;text-decoration: none;color: #888888;font-size: 12px;">★取消收藏(${requestScope.product.product_sale_count}人气)</a>
        </c:if>
        <c:if test="${!requestScope.isCollect}">
            <a href="javascript:void(0)" onclick="productcollect()" style="margin-left: 40px;text-decoration: none;color: #888888;font-size: 12px;">☆收藏商品(${requestScope.product.product_sale_count}人气)</a>
        </c:if>

        <a href="javascript:void(0)" style="margin-left: 140px;text-decoration: none;color: #888888;font-size: 12px;">×举报</a>
    </div>
    <div class="context_info">
        <div class="context_info_name_div">
            <p class="context_info_name">${requestScope.product.product_name}</p>
            <span class="context_info_title">${requestScope.product.product_title}</span>
        </div>
        <div class="context_info_main">
            <div class="context_info_main_ad">
                <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/context_ad.png">
                <span>全天猫实物商品通用</span>
                <a href="#">去刮券<img
                        src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/tmallItemContentB.png"></a>
            </div>
            <dl class="context_price_panel">
                <dt>价格</dt>
                <dd><em>¥</em><span>${requestScope.product.product_price}0</span></dd>
            </dl>
            <dl class="context_promotePrice_panel">
                <dt>促销价</dt>
                <dd><em>¥</em><span>${requestScope.product.product_sale_price}0</span></dd>
            </dl>
        </div>
        <ul class="context_other_panel">
            <li>总销量<span><c:choose><c:when
                    test="${requestScope.product.product_sale_count != null}">${requestScope.product.product_sale_count}</c:when><c:otherwise>0</c:otherwise></c:choose></span>
            </li>
            <li>累计评价<span>${requestScope.product.product_review_count}</span></li>
            <li class="tmall_points">送天猫积分<span><fmt:formatNumber type="number"
                                                                  value="${requestScope.product.product_sale_price/10}"
                                                                  maxFractionDigits="0"/></span></li>
        </ul>
        <dl class="context_info_member">
            <dt>数量</dt>
            <dd>
                <input type="text" value="1" maxlength="8" title="请输入购买量" class="context_buymember">
                <input type="hidden" id="stock" value="1000">
                <span class="amount-btn">
                    <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/up.png"
                         class="amount_value_up">
                    <img src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/down.png"
                         class="amount_value-down">
                </span>
                <span class="amount_unit">件</span>
                <em>库存1000件</em>
            </dd>
        </dl>
        <div class="context_buy">
            <script>
                $(function () {
                    //点击购买按钮时
                    $(".context_buy_form").submit(function () {
                        if ('${sessionScope.userId}' === "") {
                            $(".loginModel").show();
                            $(".loginDiv").show();
                            return false;
                        }
                        var number = isNaN($.trim($(".context_buymember").val()));
                        if (number) {
                            location.reload();
                        } else {
                            location.href = "${pageContext.request.contextPath}/order/create/${requestScope.product.product_id}?product_number=" + $.trim($(".context_buymember").val());
                        }
                        return false;
                    });
                    //点击加入购物车按钮时
                    $(".context_buyCar_form").submit(function () {
                        if ('${sessionScope.userId}' === "") {
                            $(".loginModel").show();
                            $(".loginDiv").show();
                            return false;
                        }
                        var number = isNaN($.trim($(".context_buymember").val()));
                        if (number) {
                            location.reload();
                        } else {
                            $.ajax({
                                url: "${pageContext.request.contextPath}/orderItem/create/${requestScope.product.product_id}?product_number=" + $.trim($(".context_buymember").val()),
                                type: "POST",
                                data: {"product_number": number},
                                dataType: "json",
                                success: function (data) {
                                    if (data.success) {
                                        $(".msg").stop(true, true).animate({
                                            opacity: 1
                                        }, 550, function () {
                                            $(".msg").animate({
                                                opacity: 0
                                            }, 1500);
                                        });
                                    } else {
                                        if (data.url != null) {
                                            location.href = "/tmall" + data.url;
                                        } else {
                                            alert("加入购物车失败，请稍后再试！");
                                        }
                                    }
                                },
                                beforeSend: function () {

                                },
                                error: function () {
                                    alert("加入购物车失败，请稍后再试！");
                                }
                            });
                            return false;
                        }
                    });
                });
                //点击收藏按钮时
                function productcollect() {
                    if ('${sessionScope.userId}' === "") {
                        $(".loginModel").show();
                        $(".loginDiv").show();
                        return false;
                    }
                    $.ajax({
                        url: "${pageContext.request.contextPath}/product/collect/${requestScope.product.product_id}",
                        type: "GET",
                        data: null,
                        dataType: "json",
                        success: function (data) {
                            if (data.success) {
                                $(".msgs").stop(true, true).animate({
                                    opacity: 1
                                }, 550, function () {
                                    $(".msgs").animate({
                                        opacity: 0
                                    }, 1500,function () {
                                        location.reload();
                                    });
                                });
                            } else {
                                if (data.url != null) {
                                    location.href = "/tmall" + data.url;
                                } else {
                                    alert("收藏失败，请稍后再试！");
                                }
                            }
                        },
                        beforeSend: function () {
                        },
                        error: function () {
                            alert("收藏失败，请稍后再试！");
                        }
                    });
                    return false;
                }
                //点击取消收藏按钮时
                function offproductcollect() {
                    if ('${sessionScope.userId}' === "") {
                        $(".loginModel").show();
                        $(".loginDiv").show();
                        return false;
                    }
                    $.ajax({
                        url: "${pageContext.request.contextPath}/product/offcollect/${requestScope.product.product_id}",
                        type: "GET",
                        data: null,
                        dataType: "json",
                        success: function (data) {
                            if (data.success) {
                                $(".offmsg").stop(true, true).animate({
                                    opacity: 1
                                }, 550, function () {
                                    $(".offmsg").animate({
                                        opacity: 0
                                    }, 1500,function () {
                                        location.reload();
                                    });
                                });

                            } else {
                                if (data.url != null) {
                                    location.href = "/tmall" + data.url;
                                } else {
                                    alert("取消收藏失败，请稍后再试！");
                                }
                            }
                        },
                        beforeSend: function () {
                        },
                        error: function () {
                            alert("取消收藏失败，请稍后再试！");
                        }
                    });
                    return false;
                }
            </script>
            <form method="get" class="context_buy_form">
                <input class="context_buyNow" type="submit" value="立即购买"/>
            </form>
            <form method="get" class="context_buyCar_form">
                <input class="context_addBuyCar" type="submit" value="加入购物车"/>
            </form>
        </div>
        <div class="context_clear">
            <span>服务承诺</span>
            <a href="#">正品保证</a>
            <a href="#">极速退款</a>
            <a href="#">七天无理由退换</a>
        </div>
    </div>
    <div class="context_ul">
        <div class="context_ul_head">
            <s></s>
            <span>看了又看</span>
        </div>
        <div class="context_ul_goodsList">
            <ul>
                <c:forEach items="${requestScope.loveProductList}" var="product">
                    <li class="context_ul_main">
                        <div class="context_ul_img">
                            <a href="/tmall/product/${product.product_id}">
                                <img src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${product.singleProductImageList[0].productImage_src}">
                            </a>
                            <p>¥${product.product_sale_price}0</p>
                        </div>
                    </li>
                </c:forEach>
            </ul>
            <input type="hidden" id="guessNumber" value="${requestScope.guessNumber}">
        </div>
        <ul class="context_ul_trigger">
            <li class="ul_trigger_up"><a href="#"></a></li>
            <li class="ul_trigger_down"><a href="#"></a></li>
        </ul>
    </div>
</div>
<div class="mainwrap">
    <div class="J_TabBarBox">
        <ul>
            <li class="J_GoodsDetails">
                <a href="javascript:void(0)" class="detailsClick" onclick="getDetailsPage(this,'J_details')">商品详情</a>
            </li>
            <li class="J_GoodsReviews">
                <a href="javascript:void(0)"
                   onclick="getDetailsPage(this,'J_reviews')">累计评价<span>${requestScope.product.product_review_count}</span></a>
            </li>
        </ul>
    </div>
    <div class="J_choose">
        <%@include file="include/J_details.jsp" %>
        <%@include file="include/J_review.jsp" %>
    </div>
    <div class="J_img">
        <c:forEach items="${requestScope.product.detailProductImageList}" var="image">
            <img src="${pageContext.request.contextPath}/res/images/item/productDetailsPicture/${image.productImage_src}"/>
        </c:forEach>
    </div>
</div>
<div class="msg">
    <span>商品已添加</span>
</div>
<div class="msgs">
    <span>商品已收藏</span>
</div>
<div class="offmsg">
    <span>商品已取消收藏</span>
</div>
<%@ include file="include/footer_two.jsp" %>
<%@ include file="include/footer.jsp" %>
</body>