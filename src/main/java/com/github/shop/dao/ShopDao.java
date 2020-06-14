package com.github.shop.dao;

import com.github.shop.generate.Shop;
import com.github.shop.generate.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShopDao {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public Shop getShopById(long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }

}
