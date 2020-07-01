package com.github.shop.mock;


import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderStatus;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.RpcOrderGoods;
import com.github.shop.generate.Order;
import com.github.shop.rpc.RpcOrderService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${shop.orderservice.version}")
public class MockRpcOrderService implements RpcOrderService {
    @Mock
    public RpcOrderService orderService;
    
    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return orderService.createOrder(orderInfo, order);
    }
    
    @Override
    public RpcOrderGoods deleteOrder(long orderId, Long userId) {
        return orderService.deleteOrder(orderId, userId);
    }
    
    @Override
    public RpcOrderGoods updateOrderStatus(Order order, Long userId) {
        return orderService.updateOrderStatus(order, userId);
    }
    
    @Override
    public RpcOrderGoods updateExpressInformation(Order order) {
        return orderService.updateExpressInformation(order);
    }
    
    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        return orderService.getOrderById(orderId);
    }
    
    @Override
    public PageResponse<RpcOrderGoods> getOrders(Long userId, int pageSize, int pageNum, OrderStatus status) {
        return orderService.getOrders(userId, pageSize, pageNum, status);
    }
}
