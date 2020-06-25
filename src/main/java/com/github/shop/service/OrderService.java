package com.github.shop.service;

import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderResponse;
import com.github.shop.generate.Order;

public interface OrderService {
    void deductStock(OrderInfo orderInfo);
    
    OrderResponse createOrder(OrderInfo orderInfo, Long userId);
    
    OrderResponse deleteOrder(long orderId, Long userId);
    
    OrderResponse updateExpressInformation(Order order, Long userId);
    
    OrderResponse updateOrderStatus(Order order, Long userId);
    
    OrderResponse getOrderById(Long orderId, long userId);
}
