package com.github.shop.service;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.entity.PageResponse;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;
import com.github.shop.generate.User;
import com.github.shop.service.impl.GoodsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsServiceImplTest {
    @Mock
    private GoodsDao goodsDao;
    @Mock
    private ShopDao shopDao;
    @InjectMocks
    private GoodsServiceImpl goodsService;
    
    @Mock
    Goods goods;
    @Mock
    Shop shop;
    
    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        UserContext.setUser(user);
        lenient().when(shopDao.getShopById(anyLong())).thenReturn(shop);
    }
    
    @AfterEach
    public void clean() {
        UserContext.clearUser();
    }
    
    @Test
    void createGoodsIfUserIsOwner() {
        //assert
        when(shop.getOwnerUserId()).thenReturn(1L);
        goodsService.createGoods(goods);
        
        verify(goodsDao, times(1)).createGoods(goods);
    }
    
    @Test
    void createGoodsFailedWhenUserIsNotOwner() {
        when(shop.getOwnerUserId()).thenReturn(2L);
        when(goods.getShopId()).thenReturn(100L);
        UnauthenticatedException exception = Assertions.assertThrows(UnauthenticatedException.class,
                () -> goodsService.createGoods(goods));
        assertEquals(403, exception.getStatusCode());
    }
    
    @Test
    void createGoodsFailedWhenShopNotExist() {
        when(shopDao.getShopById(anyLong())).thenReturn(null);
        when(goods.getShopId()).thenReturn(100L);
        UnauthenticatedException exception = Assertions.assertThrows(UnauthenticatedException.class,
                () -> goodsService.createGoods(goods));
        assertEquals(403, exception.getStatusCode());
    }
    
    @Test
    void deleteGoodsSuccessful() {
        when(goodsDao.getGoodsById(anyLong())).thenReturn(goods);
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goods.getShopId()).thenReturn(100L);
        goodsService.deleteGoods(1L);
        
        verify(goodsDao, times(1)).updateGoods(goods);
    }
    
    @Test
    void deleteGoodsWhenGoodsNotExist() {
        when(goodsDao.getGoodsById(anyLong())).thenReturn(null);
        
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> goodsService.deleteGoods(1L));
    }
    
    @Test
    void updateGoodsByIdSuccessful() {
        when(goodsDao.getGoodsById(100L)).thenReturn(goods);
        when(goods.getShopId()).thenReturn(1L);
        when(goods.getCreateTime()).thenReturn(Instant.now());
        when(shop.getOwnerUserId()).thenReturn(1L);
        
        //act
        Goods insertGoods = new Goods();
        goodsService.updateGoodsById(100L, insertGoods);
        
        //assert
        assertEquals(insertGoods.getId(), 100L);
        verify(goodsDao, times(1)).updateGoods(insertGoods);
    }
    
    @Test
    void updateGoodsFailed() {
        when(goodsDao.getGoodsById(100L)).thenReturn(goods);
        when(goods.getShopId()).thenReturn(1L);
        when(shop.getOwnerUserId()).thenReturn(2L);
        
        assertThrows(UnauthenticatedException.class,
                () -> goodsService.updateGoodsById(100L, new Goods()));
    }
    
    
    @Test
    void getGoods() {
        //pageSize = 10 ,pageNum = 9, totalNum = 91.
        when(goodsDao.getGoodsCount(anyLong())).thenReturn(91);
        when(goodsDao.getGoods(10, 10, 1L))
                .thenReturn(Arrays.asList(goods));
        
        //act
        PageResponse<Goods> res = goodsService.getGoods(10, 10, 1L);
        
        assertEquals(10, res.getTotalPage());
        assertEquals(goods, res.getData().get(0));
    }
}