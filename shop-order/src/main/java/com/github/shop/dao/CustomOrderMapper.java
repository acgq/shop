package com.github.shop.dao;

import com.github.shop.entity.OrderInfo;
import com.github.shop.generate.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomOrderMapper {
    
    int insertOrderInfo(OrderInfo orderInfo);
    
    void updateExpressInformation(Order order);
    
    void updateOrderStatus(Order order);
    
    void deleteOrder(long orderId);
    
    
}
