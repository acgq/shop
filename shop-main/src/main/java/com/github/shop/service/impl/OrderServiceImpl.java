package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.OrderDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.dao.UserDao;
import com.github.shop.entity.*;
import com.github.shop.exception.BadRequestException;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Order;
import com.github.shop.generate.OrderGoodsMapping;
import com.github.shop.generate.Shop;
import com.github.shop.rpc.RpcOrderService;
import com.github.shop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao;
    private GoodsDao goodsDao;
    private UserDao userDao;
    private ShopDao shopDao;
    
    
    private RpcOrderService rpcOrderService;
    
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
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
        List<Long> goodsIdList = orderInfo.getGoods().stream()
                .map(GoodsInfo::getId)
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

//        Order orderInserted = rpcOrderService.createOrder(orderInfo, order);
        //generate response
        return generateOrderResponse(goodsWithNumberList, orderInserted);
    }
    
    private OrderResponse generateOrderResponse(List<GoodsWithNumber> goodsWithNumberList, Order orderInserted) {
        OrderResponse orderResponse = new OrderResponse(orderInserted);
        orderResponse.setShop(shopDao.getShopById(orderInserted.getShopId()));
        orderResponse.setGoods(goodsWithNumberList);
        return orderResponse;
    }
    
    private Order createOrderInstance(Long userId, Map<Long, Goods> idToGoodsMap, BigDecimal totalPrice) {
        //get shop id
        long shopId = getShopId(idToGoodsMap);
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
    
    private long getShopId(Map<Long, Goods> idToGoodsMap) {
        Iterator<Goods> iterator = idToGoodsMap.values().iterator();
        long shopId = iterator.next().getShopId();
        while (iterator.hasNext()) {
            if (iterator.next().getShopId() != shopId) {
                throw new BadRequestException("禁止一个订单中包含两个商店商品");
            }
        }
        return shopId;
    }
    
    @Override
    public OrderResponse deleteOrder(long orderId, Long userId) {
        Order orderInDB = orderDao.getOrderById(orderId);
        if (!checkIsOrderOwner(orderInDB, userId)) {
            throw new UnauthenticatedException("不是订单创建者");
        }
        orderDao.deleteOrder(orderId);
        return getOrderById(orderId, userId);
    }
    
    @Override
    public OrderResponse updateExpressInformation(Order order, Long userId) {
        verify(() -> order.getId() == null
                || order.getId() < 0, "订单id不合法:" + order.getId());
        
        Order orderInDB = orderDao.getOrderById(order.getId());
        if (!checkIsShopOwner(orderInDB, userId)) {
            throw new UnauthenticatedException("没有权限修改订单信息");
        }
        //update express information
        order.setStatus(OrderStatus.DELIVERED.getName());
        orderDao.updateExpressInformation(order);
        return getOrderById(order.getId(), userId);
    }
    
    private boolean checkIsOrderOwner(Order orderInDB, Long userId) {
        if (orderInDB == null) {
            throw new ResourceNotFoundException("找不到订单");
        }
        if (orderInDB.getUserId() != userId) {
            return false;
        }
        return true;
    }
    
    
    private boolean checkIsShopOwner(Order orderInDB, Long userId) {
        //判断用户是否是商店主人.
        if (orderInDB == null) {
            throw new ResourceNotFoundException("没有这个订单");
        }
        Shop shopInDB = shopDao.getShopById(orderInDB.getShopId());
        if (shopInDB.getOwnerUserId() != userId) {
            logger.debug("没有权限修改订单 order:{} shop:{} user:{}", orderInDB, shopInDB, userId);
            return false;
        }
        return true;
    }
    
    //校验输入是否合法.
    public void verify(BooleanSupplier booleanSupplier, String message) {
        if (booleanSupplier.getAsBoolean()) {
            logger.debug(message);
            throw new BadRequestException(message);
        }
    }
    
    
    @Override
    public OrderResponse updateOrderStatus(Order order, Long userId) {
        verify(() -> order.getId() == null
                || order.getId() < 0, "订单id不合法:" + order.getId());
        
        Order orderInDB = orderDao.getOrderById(order.getId());
        checkIsOrderOwner(orderInDB, userId);
        
        orderDao.updateOrderStatus(order);
        return getOrderById(order.getId(), userId);
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId, long userId) {
        Order order = orderDao.getOrderById(orderId);
        //get order
        if (!checkIsShopOwner(order, userId) && !checkIsOrderOwner(order, userId)) {
            throw new UnauthenticatedException("没有权限获取订单信息");
        }
        return getOrderResponseByOrderInDB(order);
    }
    
    private OrderResponse getOrderResponseByOrderInDB(Order order) {
        List<OrderGoodsMapping> orderGoodsInfo = orderDao.getOrderInfo(order.getId());
        List<Long> goodsIdList = orderGoodsInfo.stream()
                .map(OrderGoodsMapping::getGoodsId)
                .collect(Collectors.toList());
        
        Map<Long, Goods> idToGoodsMap = goodsDao.getIdToGoodsMap(goodsIdList);
        //get goods
        List<GoodsWithNumber> goodsWithNumbers = orderGoodsInfo.stream()
                .map(orderGoodsMapping -> {
                    Goods goods = idToGoodsMap.get(orderGoodsMapping.getGoodsId());
                    GoodsWithNumber goodsWithNumber = new GoodsWithNumber(goods);
                    goodsWithNumber.setNumber(orderGoodsMapping.getNumber());
                    return goodsWithNumber;
                })
                .collect(Collectors.toList());
        
        return generateOrderResponse(goodsWithNumbers, order);
    }
    
    /**
     * 根据userId分页获取order， 分页根据是订单数，而不是总商品数
     *
     * @param userId   用户id
     * @param pageNum  页码数
     * @param pageSize 每页最大的订单数
     * @param value    订单状态
     * @return
     */
    @Override
    public PageResponse<OrderResponse> getOrder(Long userId, Integer pageNum, Integer pageSize, OrderStatus value) {
        int count = orderDao.countOrderNumberByUserId(userId);
        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        List<Order> orders = orderDao.getOrderInPage(userId, pageSize, pageNum, value);
        List<OrderResponse> pageResponse = orders.stream()
                .map(order -> getOrderResponseByOrderInDB(order))
                .collect(Collectors.toList());
        return PageResponse.of(pageSize, pageNum, totalPage, pageResponse);
    }
}
