package com.fang.controller.fore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fang.controller.BaseController;
import com.fang.entity.*;
import com.fang.service.*;
import com.fang.util.OrderUtil;
import com.fang.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单与购物车
 * 购买
 * 加入购物车-->付款
 * 已买宝贝
 *                      方鹏博
 */
@Controller
public class ForeOrderController extends BaseController {


    @Resource(name = "productService")
    private ProductService productService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "productOrderItemService")
    private ProductOrderItemService productOrderItemService;
    @Resource(name = "addressService")
    private AddressService addressService;
    @Resource(name = "categoryService")
    private CategoryService categoryService;
    @Resource(name = "productImageService")
    private ProductImageService productImageService;
    @Resource(name = "productOrderService")
    private ProductOrderService productOrderService;
    @Resource(name = "reviewService")
    private ReviewService reviewService;
    @Resource(name = "lastIDService")
    private LastIDService lastIDService;


    /**
     * 购物车内删除订单项-ajax
     * @param orderItem_id 订单项号
     * @param session
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderItem/{orderItem_id}", method = RequestMethod.DELETE, produces = "application/json;charset=utf-8")
    public String deleteOrderItem(@PathVariable("orderItem_id") Integer orderItem_id,
                                  HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success",false);
            object.put("url","/login");
            return object.toJSONString();
        }
        logger.info("检查用户的购物车项");
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        //是否存在此订单项
        boolean isMine = false;
        for (ProductOrderItem orderItem : orderItemList) {
            logger.info("找到匹配的购物车项");
            if (orderItem.getProductOrderItem_id().equals(orderItem_id)) {
                isMine = true;
                break;
            }
        }
        if (isMine) {
            logger.info("删除订单项信息");
            boolean yn = productOrderItemService.deleteList(new Integer[]{orderItem_id});
            if (yn) {
                object.put("success", true);
            } else {
                object.put("success", false);
            }
        } else {
            object.put("success", false);
        }
        return object.toJSONString();
    }

    /**
     * 清空购物车-ajax
     * @param session
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "removeAllorderItem", method = RequestMethod.DELETE, produces = "application/json;charset=utf-8")
    public String deleteOrderItems(HttpSession session,
                                  HttpServletRequest request) {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success",false);
            object.put("url","/login");
            return object.toJSONString();
        }
        logger.info("检查用户的购物车项");
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        //是否存在订单
        boolean isMine = false;
        //订单项id集合
        List<Integer> orderItem_ids=new ArrayList<>(orderItemList.size());
        if(orderItemList !=null && orderItemList.size()>0){
            isMine=true;
            for (ProductOrderItem orderItem:orderItemList){
                orderItem_ids.add(orderItem.getProductOrderItem_id());
            }
        }
        if (isMine) {
            logger.info("清空购物车");
            boolean yn = productOrderItemService.deleteList(orderItem_ids.toArray(new Integer[orderItem_ids.size()]));
            if (yn) {
                object.put("success", true);
            } else {
                object.put("success", false);
            }
        } else {
            object.put("success", false);
        }
        return object.toJSONString();
    }


    //购物车为空时-->转到订单列表页
    @RequestMapping(value = "order", method = RequestMethod.GET)
    public String goToPageSimple() {
        return "redirect:/order/0/10";
    }

    /**
     *  转到订单收货完成页
     * @param map
     * @param session
     * @param order_code 订单号
     * @return
     */
    @RequestMapping(value = "order/success/{order_code}", method = RequestMethod.GET)
    public String goToOrderSuccessPage(Map<String, Object> map, HttpSession session,
                                       @PathVariable("order_code") String order_code) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 3) {
            logger.warn("订单状态不正确，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("获取订单中订单项数量");
        Integer count = productOrderItemService.getTotalByOrderId(order.getProductOrder_id());
        Product product = null;
        if (count == 1) {
            logger.info("获取订单中的唯一订单项");
            ProductOrderItem productOrderItem = productOrderItemService.getListByOrderId(order.getProductOrder_id(), new PageUtil(0, 1)).get(0);
            if (productOrderItem != null) {
                logger.info("获取订单项评论数量");
                count = reviewService.getTotalByOrderItemId(productOrderItem.getProductOrderItem_id());
                if (count == 0) {
                    logger.info("获取订单项产品信息");
                    product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
                    if (product != null) {
                        product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
                    }
                }
            }
            map.put("orderItem", productOrderItem);
        }

        map.put("product", product);

        logger.info("转到前台天猫-订单完成页");
        return "fore/orderSuccessPage";
    }



    /**
     * 更新订单信息为交易成功-ajax
     * 确认收货
     * @param session
     * @param order_code
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "order/success/{order_code}", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    public String orderSuccess(HttpSession session, @PathVariable("order_code") String order_code) {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success", false);
            object.put("url", "/login");
            return object.toJSONString();
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 2) {
            logger.warn("订单状态不正确，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        logger.info("更新订单信息");
        ProductOrder productOrder = new ProductOrder()
                .setProductOrder_id(order.getProductOrder_id())
                .setProductOrder_status((byte) 3)
                .setProductOrder_confirm_date(new Date());

        boolean yn = productOrderService.update(productOrder);
        if (yn) {
            object.put("success", true);
        } else {
            object.put("success", false);
        }
        return object.toJSONString();
    }


    /**
     * 转发页面
     * 转发到确认收货页面
     * @param map
     * @param session
     * @param order_code
     * @return
     */
    @RequestMapping(value = "order/confirm/{order_code}",method = RequestMethod.GET)
    public String goToOrderConfirmPage(Map<String, Object> map, HttpSession session,
                                       @PathVariable("order_code") String order_code) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 2) {
            logger.warn("订单状态不正确，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("封装订单信息");
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));
        //总金额
        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            logger.info("获取单订单项的产品信息");
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
            Product product = productService.get(product_id);
            product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
            productOrderItem.setProductOrderItem_product(product);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            logger.info("获取多订单项的产品信息");
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
                productOrderItem.setProductOrderItem_product(product);
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        logger.info("订单总金额为：{}元", orderTotalPrice);
        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        logger.info("转到前台天猫-订单确认页");
        return "fore/orderConfirmPage";
    }



    /**
     * 更新订单信息
     * 从提醒发货变更为已发货，待确认
     * @param session
     * @param order_code
     * @return
     */
    @RequestMapping(value = "order/delivery/{order_code}",method = RequestMethod.GET)
    public String orderDelivery(HttpSession session, @PathVariable("order_code") String order_code) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            return "redirect:/order/0/10";
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 1) {
            logger.warn("订单状态不正确，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("更新订单信息");
        logger.info("更新订单信息");
        ProductOrder productOrder=new ProductOrder()
                .setProductOrder_id(order.getProductOrder_id())
                .setProductOrder_delivery_date(new Date())//更新发货时间
                .setProductOrder_status((byte)2);//更新订单状态
        productOrderService.update(productOrder);
        return "redirect:/order/0/10";
    }


    /**
     * 我购买的宝贝-->转到订单列表页
     * @param session
     * @param map
     * @param status
     * @param index
     * @param count
     * @return
     */
    @RequestMapping(value = "order/{index}/{count}",method = RequestMethod.GET)
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @RequestParam(required = false) Byte status,
                           @PathVariable("index") Integer index/* 页数 */,
                           @PathVariable("count") Integer count/* 行数*/) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        //订单的状态
        Byte[] status_array = null;
        if (status != null) {
            status_array = new Byte[]{status};
        }
        //分页工具
        PageUtil pageUtil = new PageUtil(index, count);
        logger.info("根据用户ID:{}获取订单列表", userId);
        List<ProductOrder> productOrderList = productOrderService.getList(new ProductOrder().setProductOrder_user(new User().setUser_id(Integer.valueOf(userId.toString()))),
                            status_array,
                            new OrderUtil("productOrder_id",true),
                            pageUtil);
        //订单的总数量
        Integer orderCount = 0;
        if (productOrderList.size() > 0) {
            //获取订单的总数量
            orderCount = productOrderService.getTotal(new ProductOrder().setProductOrder_user(new User().setUser_id(Integer.valueOf(userId.toString()))), status_array);
            logger.info("获取订单项信息及对应的产品信息");
            for(ProductOrder order:productOrderList){
                //获取该订单下的订单项列表
                List<ProductOrderItem> productOrderItemList = productOrderItemService.getListByOrderId(order.getProductOrder_id(), null);
                if(productOrderItemList !=null){
                    for(ProductOrderItem productOrderItem : productOrderItemList){
                        //获取商品id
                        Integer product_id = productOrderItem.getProductOrderItem_product().getProduct_id();
                        //获取商品信息
                        Product product = productService.get(product_id);
                        //获取商品的预览图
                        product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
                        //封装商品订单项
                        productOrderItem.setProductOrderItem_product(product);
                        //如果该商品已完成
                        if (order.getProductOrder_status() == 3) {
                            //是否已评价
                            productOrderItem.setIsReview(reviewService.getTotalByOrderItemId(productOrderItem.getProductOrderItem_id()) > 0);
                        }
                    }
                }
                //封装订单
                order.setProductOrderItemList(productOrderItemList);
            }
        }
        //分页工具
        pageUtil.setTotal(orderCount);
        //为了前台搜索框
        logger.info("获取产品分类列表信息");
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 5));
        map.put("pageUtil", pageUtil);
        map.put("productOrderList", productOrderList);
        map.put("categoryList", categoryList);
        map.put("status", status);

        logger.info("转到前台天猫-订单列表页");
        return "fore/orderListPage";
    }








    /**
     * 创建新订单-多订单项-ajax
     * @param session
     * @param map
     * @param response
     * @param addressId 省
     * @param cityAddressId 市
     * @param districtAddressId 区
     * @param productOrder_detail_address 详细地址
     * @param productOrder_post 邮编
     * @param productOrder_receiver 收件人
     * @param productOrder_mobile 手机号
     * @param orderItemJSON 订单项id列表和备注信息列表
     * @return
     * @throws UnsupportedEncodingException
     */
    @ResponseBody
    @RequestMapping(value = "order/list",method = RequestMethod.POST)
    public String createOrderByList(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                    @RequestParam String addressId,
                                    @RequestParam String cityAddressId,
                                    @RequestParam String districtAddressId,
                                    @RequestParam String productOrder_detail_address,
                                    @RequestParam String productOrder_post,
                                    @RequestParam String productOrder_receiver,
                                    @RequestParam String productOrder_mobile,
                                    @RequestParam String orderItemJSON) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success", false);
            object.put("url", "/login");
            return object.toJSONString();
        }
        //把String字符串转成json串
        JSONObject orderItemMap = JSONObject.parseObject(orderItemJSON);
        //获取订单项id列表
        Set<String> orderItem_id = orderItemMap.keySet();
        //把订单项整合到订单
        List<ProductOrderItem> productOrderItemList = new ArrayList<>();
        if (orderItem_id.size() > 0) {
            for (String id : orderItem_id) {
                ProductOrderItem orderItem = productOrderItemService.get(Integer.valueOf(id));
                if (orderItem == null || !orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
                    logger.warn("订单项为空或用户状态不一致！");
                    object.put("success", false);
                    object.put("url", "/cart");
                    return object.toJSONString();
                }
                if (orderItem.getProductOrderItem_order() != null) {
                    logger.warn("用户订单项不属于购物车，回到购物车页");
                    object.put("success", false);
                    object.put("url", "/cart");
                    return object.toJSONString();
                }
                boolean flag = productOrderItemService.update(new ProductOrderItem().setProductOrderItem_id(Integer.valueOf(id)).setProductOrderItem_userMessage(orderItemMap.getString(id)));
                if (!flag) {
                    throw new RuntimeException();
                }
                orderItem.setProductOrderItem_product(productService.get(orderItem.getProductOrderItem_product().getProduct_id()));
                productOrderItemList.add(orderItem);
            }
        } else {
            object.put("success", false);
            object.put("url", "/cart");
            return object.toJSONString();
        }
        logger.info("将收货地址等相关信息存入Cookie中");
        Cookie cookie1 = new Cookie("addressId", addressId);
        Cookie cookie2 = new Cookie("cityAddressId", cityAddressId);
        Cookie cookie3 = new Cookie("districtAddressId", districtAddressId);
        Cookie cookie4 = new Cookie("order_post", URLEncoder.encode(productOrder_post, "UTF-8"));
        Cookie cookie5 = new Cookie("order_receiver", URLEncoder.encode(productOrder_receiver, "UTF-8"));
        Cookie cookie6 = new Cookie("order_phone", URLEncoder.encode(productOrder_mobile, "UTF-8"));
        Cookie cookie7 = new Cookie("detailsAddress", URLEncoder.encode(productOrder_detail_address, "UTF-8"));
        int maxAge = 60 * 60 * 24 * 365;  //设置过期时间为一年
        cookie1.setMaxAge(maxAge);
        cookie2.setMaxAge(maxAge);
        cookie3.setMaxAge(maxAge);
        cookie4.setMaxAge(maxAge);
        cookie5.setMaxAge(maxAge);
        cookie6.setMaxAge(maxAge);
        cookie7.setMaxAge(maxAge);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);
        response.addCookie(cookie4);
        response.addCookie(cookie5);
        response.addCookie(cookie6);
        response.addCookie(cookie7);
        StringBuffer productOrder_code = new StringBuffer()
                .append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .append(0)
                .append(userId);
        logger.info("生成的订单号为：{}", productOrder_code);
        logger.info("整合订单对象");
        ProductOrder productOrder = new ProductOrder()
                .setProductOrder_status((byte) 0)
                .setProductOrder_address(new Address().setAddress_areaId(districtAddressId))
                .setProductOrder_post(productOrder_post)
                .setProductOrder_user(new User().setUser_id(Integer.valueOf(userId.toString())))
                .setProductOrder_mobile(productOrder_mobile)
                .setProductOrder_receiver(productOrder_receiver)
                .setProductOrder_detail_address(productOrder_detail_address)
                .setProductOrder_pay_date(new Date())
                .setProductOrder_code(productOrder_code.toString());
        Boolean yn = productOrderService.add(productOrder);
        if (!yn) {
            throw new RuntimeException();
        }
        //主键自增返回
        Integer order_id = lastIDService.selectLastID();
        logger.info("整合订单项对象");
        for (ProductOrderItem orderItem : productOrderItemList) {
            orderItem.setProductOrderItem_order(new ProductOrder().setProductOrder_id(order_id));
            yn = productOrderItemService.update(orderItem);
        }
        if (!yn) {
            throw new RuntimeException();
        }
        object.put("success", true);
        object.put("url", "/order/pay/" + productOrder.getProductOrder_code());
        return object.toJSONString();
    }


    /**
     * 从购物车创建订单转至购物车订单创建页
     * @param session
     * @param request
     * @param map
     * @param order_item_list   购物车商品订单项id
     * @return
     */
    @RequestMapping(value = "order/create/byCart",method = RequestMethod.GET)
    public String goToOrderConfirmPageByCart(HttpSession session, HttpServletRequest request,Map<String, Object> map,
                                             @RequestParam(required = false) Integer[] order_item_list) throws UnsupportedEncodingException {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        if (order_item_list == null || order_item_list.length == 0) {
            logger.warn("用户订单项数组不存在，回到购物车页");
            return "redirect:/cart";
        }
        logger.info("通过订单项ID数组获取订单信息");
        List<ProductOrderItem> orderItemList = new ArrayList<>(order_item_list.length);
        for(Integer orderItem_id:order_item_list){
            orderItemList.add(productOrderItemService.get(orderItem_id));
        }
        logger.info("------检查订单项合法性------");
        if (orderItemList.size() == 0) {
            logger.warn("用户订单项获取失败，回到购物车页");
            return "redirect:/cart";
        }
        for (ProductOrderItem orderItem : orderItemList) {
            if (orderItem.getProductOrderItem_user().getUser_id() != userId) {
                logger.warn("用户订单项与用户不匹配，回到购物车页");
                return "redirect:/cart";
            }
            if (orderItem.getProductOrderItem_order() != null) {
                logger.warn("用户订单项不属于购物车，回到购物车页");
                return "redirect:/cart";
            }
        }
        logger.info("验证通过，获取订单项的产品信息");
        double orderTotalPrice = 0.0;
        for (ProductOrderItem orderItem : orderItemList) {
            Product product = productService.get(orderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            product.setSingleProductImageList(productImageService.getList(product.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
            orderItem.setProductOrderItem_product(product);
            orderTotalPrice += orderItem.getProductOrderItem_price();
        }
        logger.info("整合收货信息");
        String addressId = "110000";//默认
        String cityAddressId = "110100";//默认
        String districtAddressId = "110101";//默认
        String detailsAddress = null;//详细地址
        String order_post = null;//邮编
        String order_receiver = null;//收货人
        String order_phone = null;//手机号
        Cookie[] cookies = request.getCookies();//cookies
        if (cookies != null) {//循环cookies拿到历史收货信息
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                switch (cookieName) {
                    case "addressId":
                        addressId = cookieValue;
                        break;
                    case "cityAddressId":
                        cityAddressId = cookieValue;
                        break;
                    case "districtAddressId":
                        districtAddressId = cookieValue;
                        break;
                    case "order_post":
                        order_post = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "order_receiver":
                        order_receiver = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "order_phone":
                        order_phone = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "detailsAddress":
                        detailsAddress = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                }
            }
        }
        logger.info("获取省份信息");
        List<Address> addressList = addressService.getRoot();
        logger.info("获取addressId为{}的市级地址信息", addressId);
        List<Address> cityAddress = addressService.getList(null, addressId);
        logger.info("获取cityAddressId为{}的区级地址信息", cityAddressId);
        List<Address> districtAddress = addressService.getList(null, cityAddressId);

        map.put("orderItemList", orderItemList);
        map.put("addressList", addressList);
        map.put("cityList", cityAddress);
        map.put("districtList", districtAddress);
        map.put("orderTotalPrice", orderTotalPrice);

        map.put("addressId", addressId);
        map.put("cityAddressId", cityAddressId);
        map.put("districtAddressId", districtAddressId);
        map.put("order_post", order_post);
        map.put("order_receiver", order_receiver);
        map.put("order_phone", order_phone);
        map.put("detailsAddress", detailsAddress);

        logger.info("转到前台天猫-订单建立页");
        return "fore/productBuyPage";
    }


    /**
     * 更新购物车订单项数量-ajax
     * @param session
     * @param map
     * @param response
     * @param orderItemMap 订单项id与数量的json串
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderItem",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    public String updateOrderItem(HttpSession session, Map<String, Object> map,HttpServletResponse response,
                                  @RequestParam String orderItemMap){
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success", false);
            return object.toJSONString();
        }
        JSONObject orderItemString = JSON.parseObject(orderItemMap);
        //表示将orderitemString里边的所有key值以set集合返回
        Set<String> orderItemIDSet = orderItemString.keySet();
        if(orderItemIDSet.size()>0){
            logger.info("更新产品订单项数量");
            for(String key:orderItemIDSet){
                ProductOrderItem productOrderItem = productOrderItemService.get(Integer.valueOf(key));
                //如果订单不存在或者用户不一致
                if(productOrderItem == null || !productOrderItem.getProductOrderItem_user().getUser_id().equals(userId)){
                    logger.warn("订单项为空或用户状态不一致！");
                    object.put("success", false);
                    return object.toJSONString();
                }
                //如果此订单项已有订单
                if(productOrderItem.getProductOrderItem_order() !=null){
                    logger.warn("用户订单项不属于购物车，回到购物车页");
                    return "redirect:/cart";
                }
                //获取订单数量
                Short number= Short.valueOf(orderItemString.getString(key.toString()));
                if(number <=0 || number >500){
                    logger.warn("订单项产品数量不合法！");
                    object.put("success", false);
                    return object.toJSONString();
                }
                //获取商品的单价
                double price= productOrderItem.getProductOrderItem_price()/productOrderItem.getProductOrderItem_number();
                logger.info("更新订单信息");
                ProductOrderItem productOrderItem1 = new ProductOrderItem().setProductOrderItem_id(Integer.valueOf(key))
                        .setProductOrderItem_number(number)
                        .setProductOrderItem_price(number * price);
                boolean flag = productOrderItemService.update(productOrderItem1);
                if(!flag){
                    throw new RuntimeException();
                }
                //订单项id集合
                Object[] orderItemIDArray = orderItemIDSet.toArray();
                object.put("success", true);
                object.put("orderItemIDArray", orderItemIDArray);
                return object.toJSONString();
            }
        }else{
            logger.warn("无订单项可以处理");
            object.put("success", false);
        }
        return object.toJSONString();
    }

    /**
     * 转发至购物车页面
     * @param session
     * @param map
     * @return
     */
    @RequestMapping(value = "cart",method = RequestMethod.GET)
    public String goToCartPage(HttpSession session,Map<String,Object> map){
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("获取用户购物车信息");
        List<ProductOrderItem> orderItemList =productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()),null);
        Integer orderItemTotal = 0;
        if(orderItemList.size()>0){
            logger.info("获取用户购物车的商品总数");
            orderItemTotal=productOrderItemService.getTotalByUserId(Integer.valueOf(userId.toString()));
            logger.info("获取用户购物车内的商品信息");
            for (ProductOrderItem orderItem:orderItemList){
                Integer product_id = orderItem.getProductOrderItem_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id,(byte)0,null));
                product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
                orderItem.setProductOrderItem_product(product);
            }
        }
        logger.info("获取分类列表");
        List<Category> categoryList =categoryService.getList(null,new PageUtil(0,5));

        map.put("categoryList",categoryList);
        map.put("orderItemList", orderItemList);
        map.put("orderItemTotal", orderItemTotal);
        logger.info("转到前台天猫-购物车页");
        return "fore/productBuyCarPage";
    }


    /**
     *  商品加入购物车-ajax
     * @param session
     * @param request
     * @param product_id 商品ID
     * @param product_number   商品数量
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "orderItem/create/{product_id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String createOrderItem(HttpSession session,HttpServletRequest request,
                                  @PathVariable("product_id") Integer product_id,
                                  @RequestParam(required = false,defaultValue = "1") Short product_number){
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("url", "/login");
            object.put("success", false);
            return object.toJSONString();
        }
        logger.info("通过产品ID获取产品信息:{}",product_id);
        Product product = productService.get(product_id);
        if (product == null) {
            object.put("url", "/login");
            object.put("success", false);
            return object.toJSONString();
        }
        //要创建一个订单项
        ProductOrderItem productOrderItem=new ProductOrderItem();
        logger.info("检查用户的购物车项");
        //获取此用户的所有订单项(通过查询表中没有订单号的数据)
        List<ProductOrderItem> orderItemList = productOrderItemService.getListByUserId(Integer.valueOf(userId.toString()), null);
        for(ProductOrderItem orderItem : orderItemList){
            if(orderItem.getProductOrderItem_product().getProduct_id().equals(product_id)){
                logger.info("找到已有产品,进行数量追加");
                int number =orderItem.getProductOrderItem_number();
                number += 1;
                productOrderItem.setProductOrderItem_id(orderItem.getProductOrderItem_id());
                productOrderItem.setProductOrderItem_number((short) number);
                productOrderItem.setProductOrderItem_price(number * product.getProduct_sale_price());
                boolean flag = productOrderItemService.update(productOrderItem);
                if(flag){
                    object.put("success",true);
                }else{
                    object.put("success",false);
                }
                return object.toJSONString();
            }
        }
        logger.info("封装订单项对象");
        productOrderItem.setProductOrderItem_product(product);
        productOrderItem.setProductOrderItem_number(product_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price()* product_number);
        productOrderItem.setProductOrderItem_user(new User().setUser_id(Integer.valueOf(userId.toString())));
        boolean flag = productOrderItemService.add(productOrderItem);
        if(flag){
            object.put("success",true);
        }else{
            object.put("success",false);
        }
        return object.toJSONString();
    }



    /**
     *  点击购买按钮-->创建订单
     * @param session
     * @param map
     * @param request
     * @param product_id 商品id
     * @param product_number 商品数量
     * @return
     */
    @RequestMapping(value = "order/create/{product_id}",method = {RequestMethod.GET})
    public String goToOrderConfirmPage(HttpSession session, Map<String,Object> map,
                                       HttpServletRequest request,
                                       @PathVariable("product_id") Integer product_id,
                                       @RequestParam(required = false,defaultValue = "1") Short product_number) throws UnsupportedEncodingException {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("通过产品ID获取产品信息：{}", product_id);
        Product product = productService.get(product_id);
        if(product == null){
            return "redirect:/";
        }
        logger.info("获取产品的详细信息");
        product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
        product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
        logger.info("封装订单项对象");
        ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setProductOrderItem_product(product);
        productOrderItem.setProductOrderItem_number(product_number);
        productOrderItem.setProductOrderItem_price(product.getProduct_sale_price() * product_number);
        productOrderItem.setProductOrderItem_user(new User().setUser_id(Integer.valueOf(userId.toString())));
        //封装订单地址
        String addressId = "110000";//默认
        String cityAddressId = "110100";//默认
        String districtAddressId = "110101";//默认
        String detailsAddress = null;//详细地址
        String order_post = null;//邮编
        String order_receiver = null;//收件人
        String order_phone = null;//手机号
        Cookie[] cookies = request.getCookies();
        //遍历cookie拿到记忆的地址
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                switch (cookieName) {
                    case "addressId":
                        addressId = cookieValue;
                        break;
                    case "cityAddressId":
                        cityAddressId = cookieValue;
                        break;
                    case "districtAddressId":
                        districtAddressId = cookieValue;
                        break;
                    case "order_post":
                        order_post = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "order_receiver":
                        order_receiver = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "order_phone":
                        order_phone = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                    case "detailsAddress":
                        detailsAddress = URLDecoder.decode(cookieValue, "UTF-8");
                        break;
                }
            }
        }
        logger.info("获取省份信息");
        List<Address> addressList = addressService.getRoot();
        logger.info("获取addressId为{}的市级地址信息", addressId);
        List<Address> cityAddress = addressService.getList(null, addressId);
        logger.info("获取cityAddressId为{}的区级地址信息", cityAddressId);
        List<Address> districtAddress = addressService.getList(null, cityAddressId);

        List<ProductOrderItem> productOrderItemList = new ArrayList<>();
        productOrderItemList.add(productOrderItem);

        map.put("orderItemList", productOrderItemList);//订单项
        map.put("addressList", addressList);//省
        map.put("cityList", cityAddress);//市
        map.put("districtList", districtAddress);//区
        map.put("orderTotalPrice", productOrderItem.getProductOrderItem_price());//总价格

        map.put("addressId", addressId);//省id
        map.put("cityAddressId", cityAddressId);//市id
        map.put("districtAddressId", districtAddressId);//区id
        map.put("order_post", order_post);//邮编
        map.put("order_receiver", order_receiver);//收件人
        map.put("order_phone", order_phone);//手机
        map.put("detailsAddress", detailsAddress);//详细地址

        logger.info("转到订单建立页");
        return "fore/productBuyPage";
    }


    /**
     *  创建新订单-单订单项-ajax
     * @param session
     * @param map
     * @param response
     * @param addressId 省id
     * @param cityAddressId 市id
     * @param districtAddressId 区id
     * @param productOrder_detail_address 详细地址
     * @param productOrder_post 邮编
     * @param productOrder_receiver 收货人姓名
     * @param productOrder_mobile 手机
     * @param userMessage   备注
     * @param orderItem_product_id  商品id
     * @param orderItem_number 商品数量
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "order", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String createOrderByOne(HttpSession session, Map<String, Object> map, HttpServletResponse response,
                                   @RequestParam String addressId,
                                   @RequestParam String cityAddressId,
                                   @RequestParam String districtAddressId,
                                   @RequestParam String productOrder_detail_address,
                                   @RequestParam String productOrder_post,
                                   @RequestParam String productOrder_receiver,
                                   @RequestParam String productOrder_mobile,
                                   @RequestParam String userMessage,
                                   @RequestParam Integer orderItem_product_id,
                                   @RequestParam Short orderItem_number) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success", false);
            object.put("url", "/login");
            return object.toJSONString();
        }
        logger.info("获取商品");
        Product product = productService.get(orderItem_product_id);
        //如果未找到此商品
        if (product == null) {
            object.put("success", false);
            object.put("url", "/");
            return object.toJSONString();
        }
        logger.info("将收货地址等相关信息存入Cookie中");
        Cookie cookie1 = new Cookie("addressId", addressId);
        Cookie cookie2 = new Cookie("cityAddressId", cityAddressId);
        Cookie cookie3 = new Cookie("districtAddressId", districtAddressId);
        Cookie cookie4 = new Cookie("order_post", URLEncoder.encode(productOrder_post, "UTF-8"));
        Cookie cookie5 = new Cookie("order_receiver", URLEncoder.encode(productOrder_receiver, "UTF-8"));
        Cookie cookie6 = new Cookie("order_phone", URLEncoder.encode(productOrder_mobile, "UTF-8"));
        Cookie cookie7 = new Cookie("detailsAddress", URLEncoder.encode(productOrder_detail_address, "UTF-8"));
        int maxAge = 60 * 60 * 24 * 365;  //设置过期时间为一年
        cookie1.setMaxAge(maxAge);
        cookie2.setMaxAge(maxAge);
        cookie3.setMaxAge(maxAge);
        cookie4.setMaxAge(maxAge);
        cookie5.setMaxAge(maxAge);
        cookie6.setMaxAge(maxAge);
        cookie7.setMaxAge(maxAge);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);
        response.addCookie(cookie4);
        response.addCookie(cookie5);
        response.addCookie(cookie6);
        response.addCookie(cookie7);

        //生成订单号
        StringBuffer productOrder_code=new StringBuffer()
                .append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .append(0)
                .append(userId);
        logger.info("生成的订单号为：{}", productOrder_code);

        logger.info("整合订单对象");
        ProductOrder productOrder=new ProductOrder()
                .setProductOrder_status((byte) 0)//订单状态
                .setProductOrder_address(new Address().setAddress_areaId(districtAddressId))//省id
                .setProductOrder_post(productOrder_post)//邮编
                .setProductOrder_user(new User().setUser_id(Integer.valueOf(userId.toString())))//userid
                .setProductOrder_mobile(productOrder_mobile)//手机号
                .setProductOrder_receiver(productOrder_receiver)//收件人
                .setProductOrder_detail_address(productOrder_detail_address)//详细地址
                .setProductOrder_pay_date(new Date())//下单时间
                .setProductOrder_code(productOrder_code.toString());//订单号
        boolean flag = productOrderService.add(productOrder);
        if(!flag){
            throw new RuntimeException();
        }
        //获取自增返回Id
        Integer order_id = lastIDService.selectLastID();
        logger.info("整合订单项对象");
        ProductOrderItem productOrderItem = new ProductOrderItem()
                .setProductOrderItem_user(new User().setUser_id(Integer.valueOf(userId.toString())))
                .setProductOrderItem_product(productService.get(orderItem_product_id))
                .setProductOrderItem_number(orderItem_number)
                .setProductOrderItem_price(product.getProduct_sale_price() * orderItem_number)
                .setProductOrderItem_userMessage(userMessage)
                .setProductOrderItem_order(new ProductOrder().setProductOrder_id(order_id));
        boolean flag1 = productOrderItemService.add(productOrderItem);
        if(!flag1){
            throw new RuntimeException();
        }
        object.put("success",true);
        object.put("url","/order/pay/" + productOrder.getProductOrder_code());
        return object.toJSONString();
    }

    /**
     * 转到订单支付页
     * @param map
     * @param session
     * @param order_code 订单号
     * @return
     */
    @RequestMapping(value = "order/pay/{order_code}", method = RequestMethod.GET)
    public String goToOrderPayPage(Map<String, Object> map, HttpSession session,
                                   @PathVariable("order_code") String order_code) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 0) {
            logger.warn("订单状态不正确，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            return "redirect:/order/0/10";
        }
        //订单项装订单子项
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));
        double orderTotalPrice = 0.00;//默认总金额
        if (order.getProductOrderItemList().size() == 1) {
            logger.info("获取单订单项的产品信息");
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Product product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            productOrderItem.setProductOrderItem_product(product);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        logger.info("订单总金额为：{}元", orderTotalPrice);

        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        logger.info("转到订单支付页");
        return "fore/productPayPage";
    }

    /**
     *  点击确认支付把订单信息更新为待发货
     * @param session
     * @param order_code 订单号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "order/pay/{order_code}", method = RequestMethod.PUT)
    public String orderPay(HttpSession session, @PathVariable("order_code") String order_code) {
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId == null) {
            object.put("success", false);
            object.put("url", "/login");
            return object.toJSONString();
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 0) {
            logger.warn("订单状态不正确，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            object.put("success", false);
            object.put("url", "/order/0/10");
            return object.toJSONString();
        }
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));
        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            logger.info("获取单订单项的产品信息");
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            Product product = productService.get(productOrderItem.getProductOrderItem_product().getProduct_id());
            product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
            productOrderItem.setProductOrderItem_product(product);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        logger.info("总共支付金额为：{}元", orderTotalPrice);
        logger.info("更新订单信息");
        ProductOrder productOrder = new ProductOrder()
                .setProductOrder_id(order.getProductOrder_id())
                .setProductOrder_pay_date(new Date())
                .setProductOrder_status((byte) 1);
        boolean flag = productOrderService.update(productOrder);
        if(flag){
            object.put("success", true);
            object.put("url", "/order/pay/success/" + order_code);
        } else {
            object.put("success", false);
            object.put("url", "/order/0/10");
        }
        return object.toJSONString();
    }

    /**
     * 订单更新为代发货跳转至订单支付成功页面
     * @param map
     * @param session
     * @param order_code 订单号
     * @return
     */
    @RequestMapping(value = "order/pay/success/{order_code}", method = RequestMethod.GET)
    public String goToOrderPaySuccessPage(Map<String, Object> map, HttpSession session,
                                          @PathVariable("order_code") String order_code) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        User user;
        if (userId != null) {
            logger.info("获取用户信息");
            user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        } else {
            return "redirect:/login";
        }
        logger.info("------验证订单信息------");
        logger.info("查询订单是否存在");
        ProductOrder order = productOrderService.getByCode(order_code);
        if (order == null) {
            logger.warn("订单不存在，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证订单状态");
        if (order.getProductOrder_status() != 1) {
            logger.warn("订单状态不正确，返回订单列表页");
            return "redirect:/order/0/10";
        }
        logger.info("验证用户与订单是否一致");
        if (order.getProductOrder_user().getUser_id() != Integer.parseInt(userId.toString())) {
            logger.warn("用户与订单信息不一致，返回订单列表页");
            return "redirect:/order/0/10";
        }
        order.setProductOrderItemList(productOrderItemService.getListByOrderId(order.getProductOrder_id(), null));

        double orderTotalPrice = 0.00;
        if (order.getProductOrderItemList().size() == 1) {
            logger.info("获取单订单项的产品信息");
            ProductOrderItem productOrderItem = order.getProductOrderItemList().get(0);
            orderTotalPrice = productOrderItem.getProductOrderItem_price();
        } else {
            for (ProductOrderItem productOrderItem : order.getProductOrderItemList()) {
                orderTotalPrice += productOrderItem.getProductOrderItem_price();
            }
        }
        logger.info("订单总金额为：{}元", orderTotalPrice);
        logger.info("获取订单详情-地址信息");
        Address address = addressService.get(order.getProductOrder_address().getAddress_areaId());
        Stack<String> addressStack = new Stack<>();
        //详细地址
        addressStack.push(order.getProductOrder_detail_address());
        //最后一级地址
        addressStack.push(address.getAddress_name() + " ");
        //如果不是第一级地址
        while (!address.getAddress_areaId().equals(address.getAddress_regionId().getAddress_areaId())) {
            address = addressService.get(address.getAddress_regionId().getAddress_areaId());
            addressStack.push(address.getAddress_name() + " ");
        }
        StringBuilder builder = new StringBuilder();
        while (!addressStack.empty()) {
            builder.append(addressStack.pop());
        }
        logger.info("订单地址字符串：{}", builder);
        order.setProductOrder_detail_address(builder.toString());

        map.put("productOrder", order);
        map.put("orderTotalPrice", orderTotalPrice);

        logger.info("转到前台天猫-订单支付成功页");
        return "fore/productPaySuccessPage";
    }





}
