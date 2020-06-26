package com.github.shop.dao;

import com.github.shop.generate.Shop;
import com.github.shop.generate.ShopExample;
import com.github.shop.generate.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ShopDao {
    private final ShopMapper shopMapper;
    
    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }
    
    private Shop createShop(Shop shop) {
        shop.setCreateTime(Instant.now());
        shop.setUpdateTime(Instant.now());
        shopMapper.insert(shop);
        return shop;
    }
    
    public Shop getShopById(Long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }
    
    public Shop updateShop(Shop shop) {
        shop.setUpdateTime(Instant.now());
        ShopExample example = new ShopExample();
        example.createCriteria().andIdEqualTo(shop.getId());
        shopMapper.updateByExample(shop, example);
        return shop;
    }
    
    public List<Shop> getShopsByPage(int pageSize, int pageNum, Long ownerId) {
        assert ownerId != null;
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andOwnerUserIdEqualTo(ownerId);
        shopExample.setLimit(pageSize);
        shopExample.setOffset((pageNum - 1) * pageSize);
        List<Shop> shops = shopMapper.selectByExample(shopExample);
        return shops;
    }
    
    public Shop insertShop(Shop shop) {
        shopMapper.insert(shop);
        return shop;
    }
    
    public int countShopNumbers(Long userId) {
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andOwnerUserIdEqualTo(userId);
        //应该不会超过int的范围
        return (int) shopMapper.countByExample(shopExample);
    }
}
