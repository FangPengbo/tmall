package com.fang.controller.fore;

import com.fang.controller.BaseController;
import com.fang.entity.*;
import com.fang.service.*;
import com.fang.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * 评论Controller
 *          方鹏博
 */
@Controller
public class ForeReviewController extends BaseController {

    @Resource(name = "reviewService")
    private ReviewService reviewService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "productOrderItemService")
    private ProductOrderItemService productOrderItemService;
    @Resource(name = "productOrderService")
    private ProductOrderService productOrderService;
    @Resource(name = "productService")
    private ProductService productService;
    @Resource(name = "productImageService")
    private ProductImageService productImageService;


    //添加一条评论
    @RequestMapping(value = "review", method = RequestMethod.POST)
    public String addReview(HttpSession session, Map<String, Object> map,
                            @RequestParam Integer orderItem_id,
                            @RequestParam String review_content) throws UnsupportedEncodingException {
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
        logger.info("获取订单项信息");
        ProductOrderItem orderItem = productOrderItemService.get(orderItem_id);
        if (orderItem == null) {
            logger.warn("订单项不存在，返回订单页");
            return "redirect:/order/0/10";
        }
        if (!orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
            logger.warn("订单项与用户不匹配，返回订单页");
            return "redirect:/order/0/10";
        }
        if (orderItem.getProductOrderItem_order() == null) {
            logger.warn("订单项状态有误，返回订单页");
            return "redirect:/order/0/10";
        }
        ProductOrder order = productOrderService.get(orderItem.getProductOrderItem_order().getProductOrder_id());
        if (order == null || order.getProductOrder_status() != 3) {
            logger.warn("订单项状态有误，返回订单页");
            return "redirect:/order/0/10";
        }
        if (reviewService.getTotalByOrderItemId(orderItem_id) > 0) {
            logger.warn("订单项所属商品已被评价，返回订单页");
            return "redirect:/order/0/10";
        }
        logger.info("整合评论信息");
        Review review = new Review()
                .setReview_product(orderItem.getProductOrderItem_product())
                .setReview_content(new String(review_content.getBytes("ISO-8859-1"), "UTF-8"))
                .setReview_createDate(new Date())
                .setReview_user(user)
                .setReview_orderItem(orderItem);
        logger.info("添加评论");
        Boolean yn = reviewService.add(review);
        if (!yn) {
            throw new RuntimeException();
        }
        return "redirect:/product/" + orderItem.getProductOrderItem_product().getProduct_id();
    }

    /**
     * 转到评论添加页面
     * @param session
     * @param map
     * @param orderItem_id
     * @return
     */
    @RequestMapping(value = "review/{orderItem_id}",method = RequestMethod.GET)
    public String goToPage(HttpSession session, Map<String, Object> map,
                           @PathVariable("orderItem_id") Integer orderItem_id) {

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
        logger.info("获取订单项信息");
        ProductOrderItem orderItem = productOrderItemService.get(orderItem_id);
        if (orderItem == null) {
            logger.warn("订单项不存在，返回订单页");
            return "redirect:/order/0/10";
        }
        if (!orderItem.getProductOrderItem_user().getUser_id().equals(userId)) {
            logger.warn("订单项与用户不匹配，返回订单页");
            return "redirect:/order/0/10";
        }
        if (orderItem.getProductOrderItem_order() == null) {
            logger.warn("订单项状态有误，返回订单页");
            return "redirect:/order/0/10";
        }
        ProductOrder order = productOrderService.get(orderItem.getProductOrderItem_order().getProductOrder_id());
        if (order == null || order.getProductOrder_status() != 3) {
            logger.warn("订单项状态有误，返回订单页");
            return "redirect:/order/0/10";
        }
        if (reviewService.getTotalByOrderItemId(orderItem_id) > 0) {
            logger.warn("订单项所属商品已被评价，返回订单页");
            return "redirect:/order/0/10";
        }
        logger.info("获取订单项所属产品信息和产品评论信息");
        Product product = productService.get(orderItem.getProductOrderItem_product().getProduct_id());
        product.setProduct_review_count(reviewService.getTotalByProductId(product.getProduct_id()));
        product.setSingleProductImageList(productImageService.getList(product.getProduct_id(),(byte)0,new PageUtil(0,1)));
        orderItem.setProductOrderItem_product(product);
        map.put("orderItem", orderItem);

        logger.info("转到前台天猫-评论添加页");
        return "fore/addReview";
    }


}
