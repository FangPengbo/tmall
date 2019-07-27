<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/header.jsp" %>
<head>
    <script src="${pageContext.request.contextPath}/res/js/fore/fore_productBuyCar.js"></script>
    <link href="${pageContext.request.contextPath}/res/css/fore/fore_productBuyCarPage.css" rel="stylesheet"/>
    <title>Tmall.com天猫 - 收藏夹</title>
    <script>
        $(function () {
            $('#btn-ok').click(function () {
                $.ajax({
                    url: "${pageContext.request.contextPath}/product/offcollect/" + $("#order_id_hidden").val(),
                    type: "DELETE",
                    data: null,
                    dataType: "json",
                    success: function (data) {
                        if (data.success !== true) {
                            alert("购物车商品删除异常，请稍候再试！");
                        }
                        location.href = "/tmall/collect";
                    },
                    beforeSend: function () {

                    },
                    error: function () {
                        alert("购物车产品删除异常，请稍后再试！");
                        location.href = "/tmall/collect";
                    }
                });
            });
        });
        function removeItem(orderItem_id) {
            if (isNaN(orderItem_id) || orderItem_id === null) {
                return;
            }
            $("#order_id_hidden").val(orderItem_id);
            $('#modalDiv').modal();
        }
    </script>
</head>
<body>
<nav>
    <%@ include file="include/navigator.jsp" %>
    <div class="header">
        <div id="mallLogo">
            <a href="${pageContext.request.contextPath}"><img
                    src="${pageContext.request.contextPath}/res/images/fore/WebsiteImage/tmallLogoA.png"><span
                    class="span_tmallBuyCar">收藏夹</span></a>
        </div>
        <div class="shopSearchHeader">
            <form action="${pageContext.request.contextPath}/product" method="get">
                <div class="shopSearchInput">
                    <input type="text" class="searchInput" name="product_name" placeholder="搜索 天猫 商品/品牌/店铺"
                           value="${requestScope.searchValue}" maxlength="50">
                    <input type="submit" value="搜 索" class="searchBtn">
                </div>
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
<div class="content">
    <c:choose>
        <c:when test="${fn:length(requestScope.productCollectList)<=0}">
            <div id="crumbs">

            </div>
            <div id="empty">
                <h2>您的收藏夹还是空的，赶紧行动吧！您可以：</h2>
                <ul>
                    <li><a href="${pageContext.request.contextPath}">去浏览宝贝</a></li>
                </ul>
            </div>
        </c:when>
        <c:otherwise>
            <div id="J_FilterBar">
                <ul id="J_CartSwitch">
                    <li>
                        <a href="${pageContext.request.contextPath}/collect" class="J_MakePoint">
                            <em>全部商品</em>
                            <span class="number"></span>
                        </a>
                    </li>
                </ul>
                <div class="cart-sum">

                </div>
                <div class="wrap-line">
                    <div class="floater"></div>
                </div>
            </div>
            <table id="J_CartMain">
                <thead>
                <tr>
                    <th width="474px" height="50px" class="productInfo_th"><span>商品信息</span></th>
                    <th width="120px"><span>单价</span></th>
                    <th width="84px"><span>操作</span></th>
                    <th hidden>ID</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.productCollectList}" var="collect">
                    <tr class="orderItem_info">
                        <td><img class="orderItem_product_image"
                                 src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${collect.productCollect_product.singleProductImageList[0].productImage_src}"
                                 style="width: 300px;height: 300px; margin-left: 50px"/><span class="orderItem_product_name"><a
                                href="${pageContext.request.contextPath}/product/${collect.productCollect_product.product_id}">${collect.productCollect_product.product_name}</a></span>
                        </td>
                        <td><span
                                class="orderItem_product_price">￥${collect.productCollect_product.product_price}</span>
                        </td>
                        <td><a href="javascript:void(0)" onclick="removeItem('${collect.productCollect_id}')"
                               class="remove_order">取消收藏</a></td>
                        <td>
                            <input type="hidden" class="input_orderItem" name="${collect.productCollect_id}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%-- 模态框 --%>
<div class="modal fade" id="modalDiv" tabindex="-1" role="dialog" aria-labelledby="modalDiv" aria-hidden="true"
     data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">您确定要取消收藏该宝贝吗？</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" id="btn-ok">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal" id="btn-close">关闭</button>
                <input type="hidden" id="order_id_hidden">
            </div>
        </div>
        <%-- /.modal-content --%>
    </div>
    <%-- /.modal --%>
</div>
<%@include file="include/footer_two.jsp" %>
<%@include file="include/footer.jsp" %>
</body>
