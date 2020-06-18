package com.github.shop.service;

import com.github.shop.dao.ShopDao;
import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.PageResponse;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Shop;
import com.github.shop.generate.User;
import com.github.shop.service.impl.ShopServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {
    @Mock
    private ShopDao shopDao;
    @InjectMocks
    private ShopServiceImpl shopService;
    
    @Mock
    private Shop shop;
    
    
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
    public void createShopSuccessful() {
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(shopDao.insertShop(shop)).thenReturn(shop);
        
        Shop shopReturn = shopService.createShop(this.shop);
        
        verify(shopDao, only()).insertShop(shop);
        assertEquals(1L, shopReturn.getOwnerUserId());
    }
    
    @Test
    public void updateShopSuccessful() {
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(shopDao.getShopById(100L)).thenReturn(shop);
        Shop updateShop = new Shop();
        
        shopService.updateShop(100L, updateShop);
        
        verify(shopDao, times(1)).updateShop(updateShop);
        assertEquals(100L, updateShop.getId());
        assertEquals(1L, updateShop.getOwnerUserId());
        
    }
    
    @Test
    public void updateShopFailed() {
        when(shop.getOwnerUserId()).thenReturn(2L);
        when(shopDao.getShopById(100L)).thenReturn(shop);
        Shop updateShop = new Shop();
        
        UnauthenticatedException exception = Assertions.assertThrows(UnauthenticatedException.class,
                () -> shopService.updateShop(100L, updateShop));
        assertEquals(403, exception.getStatusCode());
    }
    
    @Test
    public void deleteShopSuccessful() {
        when(shopDao.getShopById(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(1L);
        
        shopService.deleteShop(1L);
        
        verify(shop, times(1)).setStatus(ExistStatus.DELETED.getName());
    }
    
    @Test
    public void deleteShopFailed() {
        when(shop.getOwnerUserId()).thenReturn(2L);
        when(shopDao.getShopById(anyLong())).thenReturn(shop);
        
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class,
                () -> shopService.deleteShop(1L));
        
        assertEquals(403, exception.getStatusCode());
    }
    
    @Test
    public void testGetShops() {
        when(shopDao.countShopNumbers(anyLong())).thenReturn(91);
        lenient().when(shopDao.getShopsByPage(10, 9, 1L))
                .thenReturn(Arrays.asList(shop));
        
        PageResponse<Shop> response = shopService.getShopsByPage(10, 9);
        
        assertEquals(10, response.getTotalPage());
        assertEquals(1, response.getData().size());
    }
}