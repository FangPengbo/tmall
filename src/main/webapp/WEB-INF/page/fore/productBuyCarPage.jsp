<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/header.jsp" %>
<head>
    <script src="${pageContext.request.contextPath}/res/js/fore/fore_productBuyCar.js"></script>
    <link href="${pageContext.request.contextPath}/res/css/fore/fore_productBuyCarPage.css" rel="stylesheet"/>
    <title>Tmall.com天猫 - 购物车</title>
    <script>
        $(function () {
            $('#btn-ok').click(function () {
                $.ajax({
                    url: "${pageContext.request.contextPath}/orderItem/" + $("#order_id_hidden").val(),
                    type: "DELETE",
                    data: null,
                    dataType: "json",
                    success: function (data) {
                        if (data.success !== true) {
                            alert("购物车商品删除异常，请稍候再试！");
                        }
                        location.href = "/tmall/cart";
                    },
                    beforeSend: function () {

                    },
                    error: function () {
                        alert("购物车产品删除异常，请稍后再试！");
                        location.href = "/tmall/cart";
                    }
                });
            });
            $('#btn-ok2').click(function () {
                $.ajax({
                    url: "${pageContext.request.contextPath}/removeAllorderItem",
                    type: "DELETE",
                    data: null,
                    dataType: "json",
                    success: function (data) {
                        if (data.success !== true) {
                            alert("购物车商品删除异常，请稍候再试！");
                        }
                        location.href = "/tmall/cart";
                    },
                    beforeSend: function () {

                    },
                    error: function () {
                        alert("购物车产品删除异常，请稍后再试！");
                        location.href = "/tmall/cart";
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
        function removeAll() {
            $('#modalDiv2').modal();
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
                    class="span_tmallBuyCar">购物车</span></a>
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
        <c:when test="${fn:length(requestScope.orderItemList)<=0}">
            <div id="crumbs">
                <span class="cart-tip">购物车帮您一次性完成批量购买与付款，下单更便捷，付款更简单！<a
                        href="http://service.taobao.com/support/help-11746.htm?spm=a1z0d.1.0.0.ogEwpx" target="_blank">如何使用购物车</a></span>
            </div>
            <div id="empty">
                <h2>您的购物车还是空的，赶紧行动吧！您可以：</h2>
                <ul>
                    <li>看看<a href="${pageContext.request.contextPath}/order">已买到的宝贝</a></li>
                </ul>
            </div>
        </c:when>
        <c:otherwise>
            <div id="J_FilterBar">
                <ul id="J_CartSwitch">
                    <li>
                        <a href="${pageContext.request.contextPath}/cart" class="J_MakePoint">
                            <em>全部商品</em>
                            <span class="number">${requestScope.orderItemTotal}</span>
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
                    <th class="selectAll_th"><input type="checkbox" class="cbx_select" id="cbx_select_all"><label
                            for="cbx_select_all">全选</label></th>
                    <th width="474px" class="productInfo_th"><span>商品信息</span></th>
                    <th width="120px"><span>单价</span></th>
                    <th width="120px"><span>数量</span></th>
                    <th width="120px"><span>金额</span></th>
                    <th width="84px"><span>操作</span></th>
                    <th hidden>ID</th>
                    <th width="120px"><div class="shopSearchInput"><input type="button" class="searchBtn" value="清空购物车" id="removeAll" onclick="removeAll()"></div></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.orderItemList}" var="orderItem">
                    <tr class="orderItem_category">
                        <td colspan="6"><span class="shop_logo"></span><span
                                class="category_shop">店铺：贤趣${orderItem.productOrderItem_product.product_category.category_name}旗舰店</span>
                        </td>
                    </tr>
                    <tr class="orderItem_info">
                        <td class="tbody_checkbox"><input type="checkbox" class="cbx_select"
                                                          id="cbx_orderItem_select_${orderItem.productOrderItem_id}"
                                                          name="orderItem_id"><label
                                for="cbx_orderItem_select_${orderItem.productOrderItem_id}"></label></td>
                        <td><img class="orderItem_product_image"
                                 src="${pageContext.request.contextPath}/res/images/item/productSinglePicture/${orderItem.productOrderItem_product.singleProductImageList[0].productImage_src}"
                                 style="width: 80px;height: 80px;"/><span class="orderItem_product_name"><a
                                href="${pageContext.request.contextPath}/product/${orderItem.productOrderItem_product.product_id}">${orderItem.productOrderItem_product.product_name}</a></span>
                        </td>
                        <td><span
                                class="orderItem_product_price">￥${orderItem.productOrderItem_price/orderItem.productOrderItem_number}</span>
                        </td>
                        <td>
                            <div class="item_amount">
                                <a href="javascript:void(0)" onclick="up(this)"
                                   class="J_Minus <c:if test="${orderItem.productOrderItem_number<=1}">no_minus</c:if>">-</a>
                                <input type="text" value="${orderItem.productOrderItem_number}"/>
                                <a href="javascript:void(0)" onclick="down(this)" class="J_Plus">+</a>
                            </div>
                        </td>
                        <td>
                            <span class="orderItem_product_realPrice">￥${orderItem.productOrderItem_price}</span>
                        </td>
                        <td><a href="javascript:void(0)" onclick="removeItem('${orderItem.productOrderItem_id}')"
                               class="remove_order">删除</a></td>
                        <td>
                            <input type="hidden" class="input_orderItem" name="${orderItem.productOrderItem_id}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div id="J_FloatBar">
                <div class="float-bar-right">
                    <div id="J_ShowSelectedItems">
                        <span class="txt">已选商品</span>
                        <em id="J_SelectedItemsCount">0</em>
                        <span class="txt">件</span>
                    </div>
                    <div class="price_sum">
                        <span class="txt">合计（不含运费）:</span>
                        <strong class="price">
                            <em id="J_Total">
                                <span class="total_symbol">&nbsp;  ￥</span>
                                <span class="total_value"> 0.00</span>
                            </em>
                        </strong>
                    </div>
                    <div class="btn_area">
                        <a href="javascript:void(0)" id="J_Go" onclick="create(this)">
                            <span>结&nbsp;算</span>
                        </a>
                    </div>
                </div>
            </div>
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
            <div class="modal-body">您确定要取消该宝贝吗？</div>
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
<%-- 模态框2 --%>
<div class="modal fade" id="modalDiv2" tabindex="-1" role="dialog" aria-labelledby="modalDiv" aria-hidden="true"
     data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel2">提示</h4>
            </div>
            <div class="modal-body">您确定要清空购物车吗？</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" id="btn-ok2">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal" id="btn-close2">关闭</button>
            </div>
        </div>
        <%-- /.modal-content --%>
    </div>
    <%-- /.modal --%>
</div>
<%@include file="include/footer_two.jsp" %>
<%@include file="include/footer.jsp" %>
</body>
