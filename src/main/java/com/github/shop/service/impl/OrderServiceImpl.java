package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.OrderDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.dao.UserDao;
import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.OrderInfo;
import com.github.shop.entity.OrderResponse;
import com.github.shop.entity.OrderStatus;
import com.github.shop.exception.BadRequestException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Order;
import com.github.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao;
    private GoodsDao goodsDao;
    private UserDao userDao;
    private ShopDao shopDao;
    
    @Autowired
    public OrderServiceImpl(OrderDao orderDao, GoodsDao goodsDao, UserDao userDao, ShopDao shopDao) {
        this.orderDao = orderDao;
        this.goodsDao = goodsDao;
        this.userDao = userDao;
        this.shopDao = shopDao;
    }
    
    private static BigDecimal calculate(GoodsWithNumber goodsWithNumber) {
        return goodsWithNumber.getPrice().multiply(new BigDecimal(goodsWithNumber.getNumber()));
    }
    
    
    @Override
    public void deductStock(OrderInfo orderInfo) {
        if (orderInfo.getGoods().size() <= 0) {
            throw new BadRequestException("商品数量为需为正数");
        }
        orderDao.deductStock(orderInfo);
    }
    
    @Override
    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        //calculate total price
        //create order goods mapping
        //create order
        List<Long> goodsIdList = orderInfo.getGoods().stream()
                .map(Goods::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsDao.getIdToGoodsMap(goodsIdList);
        //calculate total price
        List<GoodsWithNumber> goodsWithNumberList = orderInfo.getGoods().stream()
                .map(goodsInfo -> {
                    Long goodsId = goodsInfo.getId();
                    Goods goods = idToGoodsMap.get(goodsId);
                    GoodsWithNumber goodsWithNumber = new GoodsWithNumber(goods);
                    goodsWithNumber.setNumber(goodsInfo.getNumber());
                    return goodsWithNumber;
                })
                .collect(Collectors.toList());
        BigDecimal totalPrice = goodsWithNumberList.stream()
                .map(OrderServiceImpl::calculate)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new BadRequestException("无法计算总价"));
        //create new order instance
        Order order = createOrderInstance(userId, idToGoodsMap, totalPrice);
        
        Order orderInserted = orderDao.insertOrder(order);
        //create order goods mapping
        orderInfo.setOrderId(orderInserted.getId());
        orderDao.insertOrderInfo(orderInfo);
        
        //generate response
        OrderResponse orderResponse = generateOrderResponse(goodsWithNumberList, orderInserted);
        
        return orderResponse;
    }
    
    private OrderResponse generateOrderResponse(List<GoodsWithNumber> goodsWithNumberList, Order orderInserted) {
        OrderResponse orderResponse = new OrderResponse(orderInserted);
        orderResponse.setShop(shopDao.getShopById(orderInserted.getShopId()));
        orderResponse.setGoods(goodsWithNumberList);
        return orderResponse;
    }
    
    private Order createOrderInstance(Long userId, Map<Long, Goods> idToGoodsMap, BigDecimal totalPrice) {
        Order order = new Order();
        order.setUserId(userId);
        order.setShopId(idToGoodsMap.values().iterator().next().getShopId());
        order.setAddress(userDao.getUserById(userId).getAddress());
        order.setStatus(OrderStatus.PENDING.getName());
        order.setTotalPrice(totalPrice);
        order.setCreateTime(Instant.now());
        order.setUpdateTime(Instant.now());
        return order;
    }
    
    @Override
    public OrderResponse deleteOrder(long orderId, Long userId) {
        return null;
    }
    
    @Override
    public OrderResponse updateExpressInformation(Order order, Long userId) {
        return null;
    }
    
    @Override
    public OrderResponse updateOrderStatus(Order order, Long userId) {
        return null;
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId, long userId) {
        return null;
    }
}
