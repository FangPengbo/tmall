package com.fang.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.fang.controller.BaseController;
import com.fang.entity.Category;
import com.fang.entity.Product;
import com.fang.entity.User;
import com.fang.service.CategoryService;
import com.fang.service.ProductImageService;
import com.fang.service.ProductService;
import com.fang.service.UserService;
import com.fang.util.OrderUtil;
import com.fang.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前台天猫-主页
 *          方鹏博
 */
@Controller
public class ForeHomeController extends BaseController {
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name="categoryService")
    private CategoryService categoryService;
    @Resource(name="productService")
    private ProductService productService;
    @Resource(name="productImageService")
    private ProductImageService productImageService;

    //转到前台天猫-主页
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String goToPage(HttpSession session, Map<String, Object> map) {
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId != null) {
            logger.info("获取用户信息");
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        logger.info("获取产品分类列表");
        List<Category> categoryList = categoryService.getList(null,null);
        logger.info("获取每个分类下的产品列表");
       for(Category category : categoryList){
           logger.info("获取分类id为{}的产品集合，按产品ID倒序排序", category.getCategory_id());
           List<Product> productList = productService.getList(new Product().setProduct_category(category), new Byte[]{0, 2}, new OrderUtil("product_id", true), new PageUtil(0, 8));
           if (productList != null) {
               for (Product product : productList) {
                   Integer product_id = product.getProduct_id();
                   logger.info("获取产品id为{}的产品预览图片信息", product_id);
                   product.setSingleProductImageList(productImageService.getList(product_id, (byte) 0, new PageUtil(0, 1)));
               }
           }
           category.setProductList(productList);
       }
        map.put("categoryList",categoryList);
        logger.info("获取促销产品列表");
        List<Product> specialProductList = productService.getList(null, new Byte[]{2}, null, new PageUtil(0, 6));
        map.put("specialProductList", specialProductList);

        logger.info("转到前台主页");
        return "fore/homePage";
    }

    //转到前台天猫-错误页
    @RequestMapping(value = "error", method = RequestMethod.GET)
    public String goToErrorPage() {
        return "fore/errorPage";
    }

    //获取主页分类下产品信息-ajax
    @ResponseBody
    @RequestMapping(value = "product/nav/{category_id}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getProductByNav(@PathVariable("category_id") Integer category_id) {
        JSONObject object = new JSONObject();
        if (category_id == null) {
            object.put("success", false);
            return object.toJSONString();
        }
        logger.info("获取分类ID为{}的产品标题数据", category_id);
        List<Product> productList = productService.getTitle(new Product().setProduct_category(new Category().setCategory_id(category_id)), new PageUtil(0, 40));
        List<List<Product>> complexProductList = new ArrayList<>(8);
        List<Product> products = new ArrayList<>(5);
        for (int i = 0; i < productList.size(); i++) {
            //如果临时集合中产品数达到5个，加入到产品二维集合中，并重新实例化临时集合
            if (i % 5 == 0) {
                complexProductList.add(products);
                products = new ArrayList<>(5);
            }
            products.add(productList.get(i));
        }
        complexProductList.add(products);
        Category category = new Category().setCategory_id(category_id).setComplexProductList(complexProductList);
        object.put("success", true);
        object.put("category", category);
        return object.toJSONString();
    }
}
