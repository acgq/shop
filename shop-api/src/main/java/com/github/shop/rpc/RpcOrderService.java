package com.github.shop.rpc;

import com.github.shop.entity.OrderInfo;
import com.github.shop.generate.Order;

public interface RpcOrderService {
    Order createOrder(OrderInfo orderInfo, Order order);
    
    
}
