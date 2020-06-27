package com.github.shop.impl;

import com.github.shop.dao.OrderDao;
import com.github.shop.entity.*;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Order;
import com.github.shop.generate.OrderGoodsMapping;
import com.github.shop.rpc.RpcOrderService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service(version = "${shop.orderservice.version}")
public class RpcOrderServiceImpl implements RpcOrderService {
    private OrderDao orderDao;
    
    @Autowired
    public RpcOrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
    
    private static GoodsInfo getGoodsInfo(OrderGoodsMapping mapping) {
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setId(mapping.getGoodsId());
        goodsInfo.setNumber(mapping.getNumber());
        return goodsInfo;
    }
    
    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        Order orderInserted = orderDao.insertOrder(order);
        orderInfo.setOrderId(orderInserted.getId());
        orderDao.insertOrderInfo(orderInfo);
        return order;
    }
    
    @Override
    public RpcOrderGoods deleteOrder(long orderId, Long userId) {
        Order orderInDB = orderDao.getOrderById(orderId);
        if (!checkIsOrderOwner(orderInDB, userId)) {
            throw new UnauthenticatedException("不是订单所有者");
        }
        orderDao.deleteOrder(orderId);
        
        return getOrderById(orderId);
    }
    
    @Override
    public RpcOrderGoods updateOrderStatus(Order order, Long userId) {
        Order orderById = orderDao.getOrderById(order.getId());
        if (!checkIsOrderOwner(orderById, userId)) {
            throw new UnauthenticatedException(String.format("无权修改订单信息，用户: %s 不是订单 %s的所有者",
                    userId, order.getId()));
        }
        orderDao.updateOrderStatus(order);
        return getOrderById(order.getId());
    }
    
    private boolean checkIsOrderOwner(Order order, long userId) {
        if (order == null) {
            throw new ResourceNotFoundException("找不到订单");
        }
        if (order.getUserId() != userId) {
            return false;
        }
        return true;
    }
    
    @Override
    public RpcOrderGoods updateExpressInformation(Order order) {
        orderDao.updateExpressInformation(order);
        return getOrderById(order.getId());
    }
    
    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        return getRpcOrderGoodsByOrderId(orderId);
    }
    
    @Override
    public PageResponse<RpcOrderGoods> getOrders(Long userId, int pageSize, int pageNum, OrderStatus status) {
        int count = orderDao.countOrderNumberByUserId(userId);
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        List<Order> orders = orderDao.getOrderInPage(userId, pageSize, pageNum, status);
        List<RpcOrderGoods> orderList = orders.stream()
                .map(order -> getRpcOrderGoodsByOrder(order))
                .collect(Collectors.toList());
        return PageResponse.of(pageSize, pageNum, totalPage, orderList);
    }
    
    private List<GoodsInfo> getGoodsInfoListByOrderId(Long orderId) {
        List<OrderGoodsMapping> orderInfo = orderDao.getOrderInfo(orderId);
        return orderInfo.stream()
                .map(RpcOrderServiceImpl::getGoodsInfo)
                .collect(Collectors.toList());
    }
    
    private RpcOrderGoods getRpcOrderGoodsByOrderId(Long orderId) {
        Order order = orderDao.getOrderById(orderId);
        return getRpcOrderGoodsByOrder(order);
    }
    
    private RpcOrderGoods getRpcOrderGoodsByOrder(Order order) {
        List<GoodsInfo> goodsInfoList = getGoodsInfoListByOrderId(order.getId());
        RpcOrderGoods res = new RpcOrderGoods();
        res.setOrder(order);
        res.setGoods(goodsInfoList);
        return res;
    }
    
}
