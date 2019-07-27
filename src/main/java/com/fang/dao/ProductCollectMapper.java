package com.fang.dao;

import com.fang.entity.ProductCollect;
import com.fang.util.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCollectMapper {

    Integer selectTotal (@Param("productcollect") ProductCollect productCollect);

    Integer insertOne(@Param("productcollect")ProductCollect productCollect);

    Integer deleteOne(@Param("productcollect")ProductCollect productCollect);

    List<ProductCollect> selectList(@Param("user_id")Integer user_id, @Param("pageUtil")PageUtil pageUtil);

    ProductCollect selectOne(@Param("productcollect_id") Integer productcollect_id);
}
