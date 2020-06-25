package com.github.shop.dao;

import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomOrderMapper {
    
    int deductStock(GoodsWithNumber goodsInfo);
    
    int insertOrderInfo(OrderInfo orderInfo);
}
