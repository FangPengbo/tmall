package com.fang.service;

import com.fang.entity.ProductCollect;
import com.fang.util.PageUtil;

import java.util.List;


public interface ProductCollectService {
    Integer getTotal(ProductCollect productCollect);

    boolean colleanOne(ProductCollect productCollect);

    boolean offcolleanOne(ProductCollect productCollect);

    List<ProductCollect> getList(Integer user_Id, PageUtil pageUtil);

    ProductCollect getCollect(Integer productcollect_id);
}
