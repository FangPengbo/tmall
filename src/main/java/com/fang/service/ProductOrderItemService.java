package com.fang.service;


import com.fang.entity.OrderGroup;
import com.fang.entity.ProductOrderItem;
import com.fang.util.PageUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProductOrderItemService {
    boolean add(ProductOrderItem productOrderItem);
    boolean update(ProductOrderItem productOrderItem);
    boolean deleteList(Integer[] productOrderItem_id_list);

    List<ProductOrderItem> getList(PageUtil pageUtil);
    List<ProductOrderItem> getListByOrderId(Integer order_id, PageUtil pageUtil);
    List<ProductOrderItem> getListByUserId(Integer user_id, PageUtil pageUtil);
    List<ProductOrderItem> getListByProductId(Integer product_id, PageUtil pageUtil);
    ProductOrderItem get(Integer productOrderItem_id);
    Integer getTotal();
    Integer getTotalByOrderId(Integer order_id);
    Integer getTotalByUserId(Integer user_id);

    List<OrderGroup> getTotalByProductId(Integer product_id, Date beginDate, Date endDate);

    Integer getSaleCountByProductId(Integer product_id);
}
