package com.fang.dao;

import com.fang.entity.OrderGroup;
import com.fang.entity.ProductOrderItem;
import com.fang.util.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProductOrderItemMapper {
    Integer insertOne(@Param("productOrderItem") ProductOrderItem productOrderItem);
    Integer updateOne(@Param("productOrderItem") ProductOrderItem productOrderItem);
    Integer deleteList(@Param("productOrderItem_id_list") Integer[] productOrderItem_id_list);

    List<ProductOrderItem> select(@Param("pageUtil") PageUtil pageUtil);
    List<ProductOrderItem> selectByOrderId(@Param("order_id") Integer order_id, @Param("pageUtil") PageUtil pageUtil);
    List<ProductOrderItem> selectByUserId(@Param("user_id") Integer user_id, @Param("pageUtil") PageUtil pageUtil);
    List<ProductOrderItem> selectByProductId(@Param("product_id") Integer product_id, @Param("pageUtil") PageUtil pageUtil);
    ProductOrderItem selectOne(@Param("productOrderItem_id") Integer productOrderItem_id);
    Integer selectTotal();
    Integer selectTotalByOrderId(@Param("order_id") Integer order_id);
    Integer selectTotalByUserId(@Param("user_id") Integer user_id);
    Integer selectSaleCount(@Param("product_id") Integer product_id);

    List<OrderGroup> getTotalByProductId(@Param("product_id") Integer product_id, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}
