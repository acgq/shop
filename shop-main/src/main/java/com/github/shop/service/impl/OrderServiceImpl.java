package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.dao.UserDao;
import com.github.shop.entity.*;
import com.github.shop.exception.BadRequestException;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Order;
import com.github.shop.generate.Shop;
import com.github.shop.rpc.RpcOrderService;
import com.github.shop.service.OrderService;
import org.apache.dubbo.config.annotation.Reference;
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
    private GoodsDao goodsDao;
    private UserDao userDao;
    private ShopDao shopDao;
    
    //    @Reference(version = "1.0.0")
    @Reference(version = "${shop.orderservice.version}")
    private RpcOrderService rpcOrderService;
    
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    @Autowired
    public OrderServiceImpl(GoodsDao goodsDao,
                            UserDao userDao,
                            ShopDao shopDao) {
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
        goodsDao.deductStock(orderInfo);
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
        Order orderInserted = rpcOrderService.createOrder(orderInfo, order);
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
        RpcOrderGoods rpcOrderGoods = rpcOrderService.deleteOrder(orderId, userId);
        return getOrderResponseByRpcOrderGoods(rpcOrderGoods);
    }
    
    @Override
    public OrderResponse updateExpressInformation(Order order, Long userId) {
        verify(() -> order.getId() == null
                || order.getId() < 0, "订单id不合法:" + order.getId());
        
        RpcOrderGoods rpcOrder = rpcOrderService.getOrderById(order.getId());
        
        if (!checkIsShopOwner(rpcOrder.getOrder(), userId)) {
            throw new UnauthenticatedException("没有权限修改订单信息");
        }
        //update express information
        order.setStatus(OrderStatus.DELIVERED.getName());
        //update order by rpc service
        RpcOrderGoods rpcOrderGoods = rpcOrderService.updateExpressInformation(order);
        return getOrderResponseByRpcOrderGoods(rpcOrderGoods);
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
        
        RpcOrderGoods rpcOrderGoods = rpcOrderService.updateOrderStatus(order, userId);
        return getOrderResponseByRpcOrderGoods(rpcOrderGoods);
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId, long userId) {
        RpcOrderGoods orderGoods = rpcOrderService.getOrderById(orderId);
        Order order = orderGoods.getOrder();
        //get order
        if (!checkIsOrderOwner(order, userId)) {
            throw new UnauthenticatedException(String.format("无权修改订单信息，用户:%s不是订单:%s的所有者",
                    userId, order.getId()));
        }
        if (!checkIsShopOwner(order, userId)) {
            throw new UnauthenticatedException("没有权限获取订单信息");
        }
        return getOrderResponseByRpcOrderGoods(orderGoods);
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
    
    //should be deleted.
//    private OrderResponse getOrderResponseByOrderInDB(Order order) {
//        List<OrderGoodsMapping> orderGoodsInfo = orderDao.getOrderInfo(order.getId());
//        List<Long> goodsIdList = orderGoodsInfo.stream()
//                .map(OrderGoodsMapping::getGoodsId)
//                .collect(Collectors.toList());
//
//        Map<Long, Goods> idToGoodsMap = goodsDao.getIdToGoodsMap(goodsIdList);
//        //get goods
//        List<GoodsWithNumber> goodsWithNumbers = orderGoodsInfo.stream()
//                .map(orderGoodsMapping -> {
//                    Goods goods = idToGoodsMap.get(orderGoodsMapping.getGoodsId());
//                    GoodsWithNumber goodsWithNumber = new GoodsWithNumber(goods);
//                    goodsWithNumber.setNumber(orderGoodsMapping.getNumber());
//                    return goodsWithNumber;
//                })
//                .collect(Collectors.toList());
//
//        return generateOrderResponse(goodsWithNumbers, order);
//    }
    
    /**
     * 根据userId分页获取order， 分页根据是订单数，而不是总商品数
     *
     * @param userId   用户id
     * @param pageNum  页码数
     * @param pageSize 每页最大的订单数
     * @param status   订单状态
     * @return
     */
    @Override
    public PageResponse<OrderResponse> getOrder(Long userId, int pageNum, int pageSize, OrderStatus status) {
        PageResponse<RpcOrderGoods> response = rpcOrderService.getOrders(userId, pageSize, pageNum, status);
        
        List<OrderResponse> orderResponses = response.getData().stream()
                .map(this::getOrderResponseByRpcOrderGoods)
                .collect(Collectors.toList());
        return PageResponse.of(pageSize, pageNum, response.getTotalPage(), orderResponses);
    }
    
    private OrderResponse getOrderResponseByRpcOrderGoods(RpcOrderGoods rpcOrderGoods) {
        List<GoodsWithNumber> goodsInfo = getGoodsWithNumberByGoodsInfo(rpcOrderGoods.getGoods());
        OrderResponse response = new OrderResponse(rpcOrderGoods.getOrder());
        response.setGoods(goodsInfo);
        return response;
    }
    
    private List<GoodsWithNumber> getGoodsWithNumberByGoodsInfo(List<GoodsInfo> goodsInfos) {
        List<Long> goodsIdList = goodsInfos.stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsDao.getIdToGoodsMap(goodsIdList);
        return goodsInfos.stream()
                .map(goodsInfo -> {
                    Goods goods = idToGoodsMap.get(goodsInfo.getId());
                    GoodsWithNumber goodsWithNumber = new GoodsWithNumber(goods);
                    goodsWithNumber.setNumber(goodsInfo.getNumber());
                    return goodsWithNumber;
                })
                .collect(Collectors.toList());
    }
}
