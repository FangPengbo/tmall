package com.fang.controller.fore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fang.controller.BaseController;
import com.fang.entity.Category;
import com.fang.entity.Product;
import com.fang.entity.User;
import com.fang.service.*;
import com.fang.util.OrderUtil;
import com.fang.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品列表展示页,
 * 搜索功能,
 * 高级排序,
 *          方鹏博
 */
@Controller
public class ForeProductListController extends BaseController {
    @Resource(name = "productService")
    private ProductService productService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "categoryService")
    private CategoryService categoryService;
    @Resource(name = "productImageService")
    private ProductImageService productImageService;
    @Resource(name = "reviewService")
    private ReviewService reviewService;
    @Resource(name = "productOrderService")
    private ProductOrderService productOrderService;
    @Resource(name = "productOrderItemService")
    private ProductOrderItemService productOrderItemService;


    /**
     * 商品搜索列表页
     * @param session
     * @param map
     * @param category_id 点击搜索框下的链接传过来的分类id
     * @param product_name  前台传过来的商品的名称
     * @return
     */
    @RequestMapping(value = "product",method = {RequestMethod.GET})
    public String goToPage(HttpSession session, Map<String,Object> map,
                           Integer category_id,String product_name) throws UnsupportedEncodingException {
        /*--检查用户是否登录--*/
        logger.info("检查用户是否登录");
        Object userid= checkUser(session);
        if(userid != null){
            logger.info("获取用户的信息");
            User user = userService.get(Integer.parseInt(userid.toString()));
            map.put("user",user);
        }
        /*--判断前台传入数据是否为空--*/
        if(category_id == null && product_name == null){
            //重定向到主页面
            return "redirect:/";
        }
        if(product_name != null && product_name.trim().equals("")){
            //重定向到主页面
            return "redirect:/";
        }
        logger.info("整合搜索信息");
        Product product=new Product();
        OrderUtil orderUtil=null;
        String  searchValue = null;
        Integer searchType = null;
        /*--如果前台传入分类id不为空--*/
        if(category_id !=null){
            product.setProduct_category(new Category().setCategory_id(category_id));
            searchType=category_id;
        }
        //关键词数组
        String[] product_name_split =null;
        //产品列表
        List<Product> productList;
        //产品总数量
        Integer productCount;
        //分页工具
        PageUtil pageUtil=new PageUtil(0,20);
        /**
         * 如果前台传入的关键词不为空
         */
        if(product_name != null){
            //对字符编码防止乱码
            product_name=new String (product_name.getBytes("ISO8859-1"),"UTF-8");
            //对关键词切割
            product_name_split=product_name.split(" ");
            logger.warn("提取出来的关键词有{}", Arrays.toString(product_name_split));
            product.setProduct_name(product_name);
            searchValue=product_name;
        }
        /*--如果关键词是多个的话--*/
        if(product_name_split !=null &&product_name_split.length>1){
            logger.info("获取组合商品列表");
            //商品信息,商品标识,排序,分页,关键词数组
            productList=productService.getMoreList(product,new Byte[]{0,2},null,pageUtil,product_name_split);
            logger.info("按组合条件获取产品的总数量");
            productCount=productService.getMoreListTotal(product,new Byte[]{0,2},product_name_split);
        }else{
            logger.info("获取商品列表");
            productList=productService.getList(product,new Byte[]{0,2},null,pageUtil);
            logger.info("按关键词获取产品总数量");
            productCount=productService.getTotal(product,new Byte[]{0,2});
        }
        logger.info("获取商品列表的对应信息");
        for(Product p : productList){
            //获取商品封面图集合
            p.setSingleProductImageList(productImageService.getList(p.getProduct_id(), (byte) 0,null));
            //获取商品销量
            p.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(p.getProduct_id()));
            //获取商品的评价数
            p.setProduct_review_count(reviewService.getTotalByProductId(p.getProduct_id()));
            //获取商品所属分类信息
            p.setProduct_category(categoryService.get(p.getProduct_category().getCategory_id()));
        }
        logger.info("获取分类列表");
        List<Category> categoryList =categoryService.getList(null,new PageUtil(0,5));
        logger.info("获取分页信息");
        pageUtil.setTotal(productCount);
        /*--视图资源--*/
        map.put("categoryList",categoryList);
        map.put("totalPage",pageUtil.getTotalPage());
        logger.info("共"+pageUtil.getTotalPage()+"页");
        map.put("pageUtil",pageUtil);
        map.put("productList",productList);
        map.put("searchValue",searchValue);
        map.put("searchType",searchType);
        logger.info("转到前台天猫-产品搜索列表页");
        return "fore/productListPage";
    }


    /**
     *  产品的高级查询
     *         此为REST风格传参
     * @param session
     * @param map
     * @param index 页数
     * @param count 条数
     * @param category_id 分类ID
     * @param product_name 商品名称
     * @param orderBy 排序字段
     * @param isDesc 是否倒序
     * @return
     */
    @RequestMapping(value = "product/{index}/{count}",method = {RequestMethod.GET})
    public String searchProduct(HttpSession session, Map<String,Object> map,
                                @PathVariable("index") Integer index,
                                @PathVariable("count") Integer count,
                                @RequestParam(value="category_id",required = false) Integer category_id,
                                @RequestParam(value = "product_name",required = false) String product_name,
                                @RequestParam(required = false) String orderBy,
                                @RequestParam(required = false, defaultValue = "true") Boolean isDesc ) throws UnsupportedEncodingException {
        logger.info("整合搜索信息");
        Product product=new Product();
        OrderUtil orderUtil = null;
        String searchValue = null;
        Integer searchType = null;

        if(category_id !=null){
            product.setProduct_category(new Category().setCategory_id(category_id));
            searchType=category_id;
        }
        if(product_name !=null){
            product.setProduct_name(product_name);
        }
        if(orderBy !=null){
            logger.info("根据{}排序,是否倒序:{}",orderBy,isDesc);
            orderUtil =new OrderUtil(orderBy,isDesc);
        }
        //关键词数组
        String [] product_name_split=null;
        //产品列表
        List<Product> productList;
        //产品总数量
        Integer productCount;
        //分页工具
        PageUtil pageUtil=new PageUtil(0,20);
        //如果传入的商品关键词不为空
        if(product_name!=null){
            product_name=new String(product_name.getBytes("ISO8859-1"),"UTF-8");
            product_name_split=product_name.split(" ");
            logger.warn("提取的关键词有{}",Arrays.toString(product_name_split));
            product.setProduct_name(product_name);
            searchValue=product_name;
        }
        //关键词为多组
        if(product_name_split!=null && product_name_split.length>1){
            logger.info("获取组合商品列表");
            productList=productService.getMoreList(product,new Byte[]{0,2},orderUtil,pageUtil,product_name_split);
            logger.info("按组合条件获取产品总数量");
            productCount=productService.getMoreListTotal(product,new Byte[]{0,2},product_name_split);
        }else{//关键词仅一个
            logger.info("获取商品列表");
            productList=productService.getList(product,new Byte[]{0,2},orderUtil,pageUtil);
            logger.info("按条件获取产品总数量");
            productCount=productService.getTotal(product,new Byte[]{0,2});
        }
        logger.info("获取商品列表的对应信息");
        for(Product p:productList){
            p.setSingleProductImageList(productImageService.getList(p.getProduct_id(), (byte) 0, null));
            p.setProduct_sale_count(productOrderItemService.getSaleCountByProductId(p.getProduct_id()));
            p.setProduct_review_count(reviewService.getTotalByProductId(p.getProduct_id()));
            p.setProduct_category(categoryService.get(p.getProduct_category().getCategory_id()));
        }
        logger.info("获取分类列表");
        List<Category> categoryList=categoryService.getList(null,new PageUtil(0,5));
        logger.info("获取分页信息");
        pageUtil.setTotal(productCount);

        map.put("productCount", productCount);
        map.put("totalPage", pageUtil.getTotalPage());
        map.put("pageUtil", pageUtil);
        map.put("productList", JSONArray.parseArray(JSON.toJSONString(productList)));
        map.put("orderBy", orderBy);
        map.put("isDesc", isDesc);
        map.put("searchValue", searchValue);
        map.put("searchType", searchType);
        map.put("categoryList", categoryList);
        logger.info("转到前台天猫-产品搜索列表页");
        return "fore/productListPage";
    }




}
