package com.github.shop.dao;

import com.github.shop.entity.OrderInfo;
import com.github.shop.generate.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomOrderMapper {
    
    int insertOrderInfo(OrderInfo orderInfo);
    
    int updateExpressInformation(Order order);
    
    int updateOrderStatus(Order order);
    
    int deleteOrder(long orderId);
    
    
}
