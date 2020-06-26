package com.github.shop.dao;

import com.github.shop.entity.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {
    
    int deductStock(GoodsInfo goodsInfo);
}
