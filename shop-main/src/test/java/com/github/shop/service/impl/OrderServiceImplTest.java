package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.dao.UserDao;
import com.github.shop.entity.GoodsInfo;
import com.github.shop.entity.OrderResponse;
import com.github.shop.entity.RpcOrderGoods;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Order;
import com.github.shop.generate.Shop;
import com.github.shop.rpc.RpcOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
class OrderServiceImplTest {
    @Mock
    private GoodsDao goodsDao;
    
    @Mock
    private UserDao userDao;
    
    @Mock
    private ShopDao shopDao;
    
    @Mock
    private RpcOrderService rpcOrderService;
    
    @InjectMocks
    OrderServiceImpl orderService;
    
    @Test
    public void getOrderById() {
        RpcOrderGoods rpcOrderGoods = new RpcOrderGoods();
        Order order = new Order();
        order.setId(1L);
        order.setShopId(1L);
        order.setUserId(1L);
        order.setTotalPrice(BigDecimal.TEN);
        rpcOrderGoods.setOrder(order);
        rpcOrderGoods.setGoods(Arrays.asList(new GoodsInfo(1L, 1),
                new GoodsInfo(2L, 2)));
        
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setOwnerUserId(3L);
        lenient().when(shopDao.getShopById(1L)).thenReturn(shop);
        lenient().when(rpcOrderService.getOrderById(1L)).thenReturn(rpcOrderGoods);
        Map<Long, Goods> map = new HashMap<>();
        Goods goods1 = new Goods();
        goods1.setId(1L);
        Goods goods2 = new Goods();
        goods2.setId(2L);
        map.put(1L, goods1);
        map.put(2L, goods2);
        lenient().when(goodsDao.getIdToGoodsMap(anyList())).thenReturn(map);
        
        assertThrows(UnauthenticatedException.class,
                () -> orderService.getOrderById(1L, 2));
        
        OrderResponse response = orderService.getOrderById(1L, 1L);
        assertEquals(1L, response.getShop().getId());
        assertEquals(2, response.getGoods().size());
    }
}