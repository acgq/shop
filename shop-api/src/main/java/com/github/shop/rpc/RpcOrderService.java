package com.github.shop.rpc;

import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderStatus;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.RpcOrderGoods;
import com.github.shop.generate.Order;

public interface RpcOrderService {
    Order createOrder(OrderInfo orderInfo, Order order);
    
    RpcOrderGoods deleteOrder(long orderId, Long userId);
    
    RpcOrderGoods updateOrderStatus(Order order, Long userId);
    
    RpcOrderGoods updateExpressInformation(Order order);
    
    RpcOrderGoods getOrderById(long orderId);
    
    PageResponse<RpcOrderGoods> getOrders(Long userId, int pageSize, int pageNum, OrderStatus status);
    
}
