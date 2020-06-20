package com.github.shop.dao;

import com.github.shop.entity.ShoppingCartData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShoppingCartDao {
    private CustomShoppingCartMapper customShoppingCartMapper;
    
    @Autowired
    public ShoppingCartDao(CustomShoppingCartMapper customShoppingCartMapper) {
        this.customShoppingCartMapper = customShoppingCartMapper;
    }
    
    public int countShopNumberByUserId(Long userId) {
        return customShoppingCartMapper.countShopNumberByUserId(userId);
    }
    
    public List<ShoppingCartData> getShoppingCartDataByUserId(long userId,
                                                              int pageNum,
                                                              int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return customShoppingCartMapper.selectShoppingCartDataByUserId(userId, offset, pageSize);
    }
}
