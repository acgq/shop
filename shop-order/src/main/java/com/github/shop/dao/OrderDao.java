package com.github.shop.dao;

import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderStatus;
import com.github.shop.generate.Order;
import com.github.shop.generate.OrderExample;
import com.github.shop.generate.OrderGoodsMapping;
import com.github.shop.generate.OrderGoodsMappingExample;
import com.github.shop.generate.mapper.OrderGoodsMappingMapper;
import com.github.shop.generate.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDao {
    private CustomOrderMapper customOrderMapper;
    private OrderMapper orderMapper;
    private OrderGoodsMappingMapper orderGoodsMappingMapper;
    
    private static Logger logger = LoggerFactory.getLogger(OrderDao.class);
    
    public OrderDao(CustomOrderMapper customOrderMapper,
                    OrderMapper orderMapper,
                    OrderGoodsMappingMapper orderGoodsMappingMapper) {
        this.customOrderMapper = customOrderMapper;
        this.orderMapper = orderMapper;
        this.orderGoodsMappingMapper = orderGoodsMappingMapper;
    }
    
    public Order insertOrder(Order order) {
        orderMapper.insert(order);
        return order;
    }
    
    
    public void insertOrderInfo(OrderInfo orderInfo) {
        customOrderMapper.insertOrderInfo(orderInfo);
    }
    
    public Order getOrderById(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        return order;
    }
    
    public List<OrderGoodsMapping> getOrderInfo(Long orderId) {
        OrderGoodsMappingExample example = new OrderGoodsMappingExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OrderGoodsMapping> orderGoodsMappings = orderGoodsMappingMapper.selectByExample(example);
        return orderGoodsMappings;
    }
    
    public void updateExpressInformation(Order order) {
        customOrderMapper.updateExpressInformation(order);
    }
    
    public void updateOrderStatus(Order order) {
        customOrderMapper.updateOrderStatus(order);
    }
    
    public void deleteOrder(long orderId) {
        customOrderMapper.deleteOrder(orderId);
    }
    
    public int countOrderNumberByUserId(Long userId) {
        OrderExample example = new OrderExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return (int) orderMapper.countByExample(example);
    }
    
    public List<Order> getOrderInPage(Long userId, Integer pageSize, Integer pageNum, OrderStatus value) {
        OrderExample example = new OrderExample();
        int offset = (pageNum - 1) * pageSize;
        if (value == null) {
            example.createCriteria().andUserIdEqualTo(userId);
        } else {
            example.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo(value.getName());
        }
        example.setOffset(offset);
        example.setLimit(pageSize);
        return orderMapper.selectByExample(example);
    }
}
