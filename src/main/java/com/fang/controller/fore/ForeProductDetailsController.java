package com.fang.controller.fore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fang.controller.BaseController;
import com.fang.entity.*;
import com.fang.service.*;
import com.fang.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 商品详情页
 *      方鹏博
 */
@Controller
public class ForeProductDetailsController extends BaseController {

    @Resource(name = "productService")
    private ProductService productService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "productImageService")
    private ProductImageService productImageService;
    @Resource(name = "categoryService")
    private CategoryService categoryService;
    @Resource(name = "propertyValueService")
    private PropertyValueService propertyValueService;
    @Resource(name = "propertyService")
    private PropertyService propertyService;
    @Resource(name = "reviewService")
    private ReviewService reviewService;
    @Resource(name = "productOrderItemService")
    private ProductOrderItemService productOrderItemService;
    @Resource(name = "productCollectService")
    private ProductCollectService productCollectService;


    /**
     * 商品取消收藏
     * @param session
     * @param map
     * @param productcollect_id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "product/offcollect/{productcollect_id}",method = RequestMethod.DELETE)
    public String offproductcollectd(HttpSession session,Map<String,Object> map,
                                    @PathVariable("productcollect_id") Integer productcollect_id){
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId != null) {
            logger.info("获取用户信息");
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        logger.info("检查收藏信息");
        ProductCollect productCollect=productCollectService.getCollect(productcollect_id);
        Product product=productService.get(productCollect.getProductCollect_product().getProduct_id());
        //如果商品不存在或者该商品已下架
        if(product==null || product.getProduct_isEnabled() ==1){
            return "redirect:/404";
        }
        logger.info("检查商品收藏情况");
        Integer count=0;//商品被该用户收藏次数
        count=productCollectService.getTotal(new ProductCollect(product,new User().setUser_id(Integer.valueOf(userId.toString()))));
        if(count==0){
            logger.info("商品未被收藏");
            object.put("success",false);
            object.put("url","/product/"+product.getProduct_id());
            return object.toJSONString();
        }
        logger.info("整合收藏信息");
        ProductCollect productCollect2=new ProductCollect();
        productCollect2.setProductCollect_id(productcollect_id);
        productCollect2.setProductCollect_product(product);
        productCollect2.setProductCollect_user(new User().setUser_id(Integer.valueOf(userId.toString())));

        logger.info("取消收藏商品");
        boolean flag=productCollectService.offcolleanOne(productCollect2);
        if(flag){
            object.put("success",true);
        }else{
            object.put("success",false);
        }
        return object.toJSONString();
    }



    /**
     * 转发至收藏页
     * @param session
     * @param map
     * @return
     */
    @RequestMapping(value = "collect",method = RequestMethod.GET)
    public String goToCollectPage(HttpSession session,Map<String,Object> map){
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
        logger.info("获取用户收藏夹信息");
        List<ProductCollect> productCollectList = productCollectService.getList(Integer.valueOf(userId.toString()), null);
        if(productCollectList.size()>0){
            for (ProductCollect productCollect:productCollectList){
                Integer product_id = productCollect.getProductCollect_product().getProduct_id();
                Product product = productService.get(product_id);
                product.setSingleProductImageList(productImageService.getList(product_id,(byte)0,null));
                productCollect.setProductCollect_product(product);
            }
        }
        logger.info("获取分类列表");
        List<Category> categoryList =categoryService.getList(null,new PageUtil(0,5));

        map.put("categoryList",categoryList);
        map.put("productCollectList",productCollectList);
        logger.info("转到收藏夹页");
        return "fore/productCollectPage";
    }


    /**
     * 商品收藏-ajax
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "product/collect/{product_id}",method = RequestMethod.GET)
    public String productcollect(HttpSession session,Map<String,Object> map,
                                 @PathVariable("product_id") Integer product_id){
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId != null) {
            logger.info("获取用户信息");
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        logger.info("检查商品信息");
        Product product=productService.get(product_id);
        //如果商品不存在或者该商品已下架
        if(product==null || product.getProduct_isEnabled() ==1){
            return "redirect:/404";
        }
        logger.info("检查商品收藏情况");
        Integer count=0;//商品被改用户收藏次数
        count=productCollectService.getTotal(new ProductCollect(product,new User().setUser_id(Integer.valueOf(userId.toString()))));
        if(count!=0){
            if(count==1){
                logger.info("商品已被收藏");
                object.put("success",false);
                object.put("url","/product/"+product_id);
                return object.toJSONString();
            }
           object.put("success",false);
           object.put("url","/product/"+product_id);
            return object.toJSONString();
        }

        logger.info("整合收藏信息");
        ProductCollect productCollect=new ProductCollect();
        productCollect.setProductCollect_product(product);
        productCollect.setProductCollect_user(new User().setUser_id(Integer.valueOf(userId.toString())));

        logger.info("收藏商品");
        boolean flag=productCollectService.colleanOne(productCollect);
        if(flag){
            object.put("success",true);
        }else{
            object.put("success",false);
        }
        return object.toJSONString();
    }
    /**
     * 商品取消收藏-ajax
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "product/offcollect/{product_id}",method = RequestMethod.GET)
    public String offproductcollect(HttpSession session,Map<String,Object> map,
                                 @PathVariable("product_id") Integer product_id){
        JSONObject object = new JSONObject();
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId != null) {
            logger.info("获取用户信息");
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        logger.info("检查商品信息");
        Product product=productService.get(product_id);
        //如果商品不存在或者该商品已下架
        if(product==null || product.getProduct_isEnabled() ==1){
            return "redirect:/404";
        }
        logger.info("检查商品收藏情况");
        Integer count=0;//商品被该用户收藏次数
        count=productCollectService.getTotal(new ProductCollect(product,new User().setUser_id(Integer.valueOf(userId.toString()))));
        if(count==0){
                logger.info("商品未被收藏");
                object.put("success",false);
                object.put("url","/product/"+product_id);
                return object.toJSONString();
        }
        logger.info("整合收藏信息");
        ProductCollect productCollect=new ProductCollect();
        productCollect.setProductCollect_product(product);
        productCollect.setProductCollect_user(new User().setUser_id(Integer.valueOf(userId.toString())));

        logger.info("取消收藏商品");
        boolean flag=productCollectService.offcolleanOne(productCollect);
        if(flag){
            object.put("success",true);
        }else{
            object.put("success",false);
        }
        return object.toJSONString();
    }


    /**
     * 转到商品详情页
     * @param session
     * @param map
     * @param pid 商品Id
     * @return
     */
    @RequestMapping(value = "product/{pid}",method = {RequestMethod.GET})
    public String goToPage(HttpSession session, Map<String,Object> map,
                           @PathVariable("pid") String pid){
        logger.info("检查用户是否登录");
        Object userId = checkUser(session);
        if (userId != null) {
            logger.info("获取用户信息");
            User user = userService.get(Integer.parseInt(userId.toString()));
            map.put("user", user);
        }
        logger.info("获取商品ID");
        Integer product_id=Integer.parseInt(pid);
        logger.info("获取商品信息");
        Product product = productService.get(product_id);
        //如果商品不存在或者该商品已下架
        if(product==null || product.getProduct_isEnabled() ==1){
            return "redirect:/404";
        }
        logger.info("获取产品子信息-分类信息");
        product.setProduct_category(categoryService.get(product.getProduct_category().getCategory_id()));
        logger.info("获取产品子信息-产品图片信息");
        //首先获取商品的所有图片
        List<ProductImage> productImageList = productImageService.getList(product_id, null, null);
        //商品封面图
        List<ProductImage> singleProductImageList = new ArrayList<>(5);
        //商品详情图
        List<ProductImage> detailsProductImageList = new ArrayList<>(8);
        for (ProductImage productImage:productImageList){
            if(productImage.getProductImage_type()==0){
                //分类为0即为商品封面图
                singleProductImageList.add(productImage);
            }else{
                detailsProductImageList.add(productImage);
            }
        }
        product.setSingleProductImageList(singleProductImageList);
        product.setDetailProductImageList(detailsProductImageList);
        logger.info("获取商品子信息-产品属性值信息");
        List<PropertyValue> propertyValueList=propertyValueService.getList(new PropertyValue().setPropertyValue_product(product),null);
        logger.info("获取商品子信息-分类信息对应的属性列表");
        List<Property> propertyList=propertyService.getList(new Property().setProperty_category(product.getProduct_category()),null);
        logger.info("属性列表和属性值列表合并");
        //第一层循环循环每一个属性列表:例如材质成分
        for(Property property:propertyList){
            //第二层循环每一个属性值:例如通勤
            for(PropertyValue propertyValue : propertyValueList){
                //进入判断材质的id=1,而通勤的id=3 不匹配进行下一个
                    //当匹配到棉95.1% 聚氨酯弹性纤维(氨纶)4.9% 的id也为1的时候 进入方法体内
                if(property.getProperty_id().equals(propertyValue.getPropertyValue_property().getProperty_id())){
                    List<PropertyValue>property_value_item=new ArrayList<>(1);
                    property_value_item.add(propertyValue);
                    //给当前属性列表添加属性值列表跳出循环,判断下一个属性列表
                    property.setPropertyValueList(property_value_item);
                    break;
                }
            }
        }
        logger.info("获取产品子信息-产品评论信息");
        product.setReviewList(reviewService.getListByProductId(product_id,null));
        if(product.getReviewList()!=null){
            for(Review review : product.getReviewList()){
               review.setReview_user(userService.get(review.getReview_user().getUser_id()));
            }
        }
        logger.info("获取商品子信息-销量数和评论数");
        product.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(product_id));
        product.setProduct_review_count(reviewService.getTotalByProductId(product_id));
        logger.info("获取猜你喜欢列表");
        Integer category_id = product.getProduct_category().getCategory_id();
        Integer total = productService.getTotal(new Product().setProduct_category(new Category().setCategory_id(category_id)), new Byte[]{0, 2});
        logger.info("分类ID为{}的产品总数为{}条", category_id, total);
        //生成随机数
        //生成一个介于[0,total)的int整数
        int i = new Random().nextInt(total);
        if (i + 2 >= total) {
            i = total - 3;
        }
        if (i < 0) {
            i = 0;
        }
        List<Product> loveProductList = productService.getList(new Product().setProduct_category(new Category().setCategory_id(category_id)),
                new Byte[]{0, 2}, null, new PageUtil().setCount(3).setPageStart(i));
        if (loveProductList!=null){
            logger.info("获取此产品的一张预览图");
            for(Product loveProduct:loveProductList){
                loveProduct.setSingleProductImageList(productImageService.getList(loveProduct.getProduct_id(), (byte) 0,new PageUtil(0,1)));
            }
        }
        logger.info("获取分类列表");
        List<Category> categoryList = categoryService.getList(null, new PageUtil(0, 3));
        boolean isCollect =false;
        logger.info("获取商品收藏情况");
        if(userId!=null){
           isCollect=productCollectService.getTotal(new ProductCollect(product,new User().setUser_id(Integer.valueOf(userId.toString()))))>0? true:false;
        }
        map.put("loveProductList", loveProductList);
        map.put("categoryList", categoryList);
        map.put("propertyList", propertyList);
        map.put("product", product);
        map.put("guessNumber", i);
        map.put("isCollect",isCollect);
        map.put("pageUtil", new PageUtil(0, 10).setTotal(product.getProduct_review_count()));
        logger.info("转到前台-产品详情页");
        return "fore/productDetailsPage";
    }

    /**
     *  加载猜你喜欢列表-ajax
     * @param cid 分类id
     * @param guessNumber 猜你喜欢数量
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "guess/{cid}",method = {RequestMethod.GET},produces = "application/json;charset=utf-8")
    public String guessYouLike (@PathVariable("cid") Integer cid, @RequestParam Integer guessNumber){
        logger.info("获取猜你喜欢列表");
        //获取该分类下的总商品数量
        Integer total = productService.getTotal(new Product().setProduct_category(new Category().setCategory_id(cid)), new Byte[]{0, 2});
        logger.info("分类ID为{}的产品总数为{}条", cid, total);
        //生成随机数
        int i = new Random().nextInt(total);
        if (i + 2 >= total) {
            i = total - 3;
        }
        if (i < 0) {
            i = 0;
        }
        while(i==guessNumber){
            i = new Random().nextInt(total);
            if (i + 2 >= total) {
                i = total - 3;
            }
            if (i < 0) {
                i = 0;
                break;
            }
        }
        logger.info("guessNumber值为{}，新guessNumber值为{}", guessNumber, i);
        //获取三条随机出的商品列表
        List<Product> loveProductList = productService.getList(new Product().setProduct_category(new Category().setCategory_id(cid)), new Byte[]{0, 2}, null, new PageUtil().setCount(3).setPageStart(i));
        if (loveProductList != null) {
            logger.info("获取产品列表的相应的一张预览图片");
            for (Product loveProduct : loveProductList) {
                loveProduct.setSingleProductImageList(productImageService.getList(loveProduct.getProduct_id(), (byte) 0, new PageUtil(0, 1)));
            }
        }
        JSONObject jsonObject = new JSONObject();
        logger.info("获取数据成功！");
        jsonObject.put("success", true);
        jsonObject.put("loveProductList", JSONArray.parseArray(JSON.toJSONString(loveProductList)));
        jsonObject.put("guessNumber", i);
        return jsonObject.toJSONString();
    }













}
