package com.github.shop.service.impl;

import com.github.shop.dao.ShopDao;
import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.PageResponse;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Shop;
import com.github.shop.service.ShopService;
import com.github.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopDao shopDao;
    
    @Autowired
    public ShopServiceImpl(ShopDao shopDao) {
        this.shopDao = shopDao;
    }
    
    @Override
    public Shop createShop(Shop shop) {
        Long ownerId = UserContext.getUser().getId();
        shop.setOwnerUserId(ownerId);
        shop.setStatus(ExistStatus.OK.getName());
        shop.setCreateTime(Instant.now());
        shop.setUpdateTime(Instant.now());
        
        return shopDao.insertShop(shop);
        
    }
    
    @Override
    public Shop updateShop(Long shopId, Shop shop) {
        Shop shopInDB = getShopById(shopId);
        checkOwnership(shopInDB);
        shop.setId(shopId);
        shop.setOwnerUserId(shopInDB.getOwnerUserId());
        if (shop.getCreateTime() == null) {
            shop.setCreateTime(shopInDB.getCreateTime());
        }
        return shopDao.updateShop(shop);
    }
    
    @Override
    public Shop deleteShop(Long shopId) {
        Shop shopInDB = getShopById(shopId);
        checkOwnership(shopInDB);
        shopInDB.setStatus(ExistStatus.DELETED.getName());
        return shopDao.updateShop(shopInDB);
    }
    
    private void checkOwnership(Shop shopInDB) {
        Long userId = UserContext.getUser().getId();
        if (!Objects.equals(userId, shopInDB.getOwnerUserId())) {
            throw new UnauthenticatedException("没有权限编辑店铺信息");
        }
    }
    
    @Override
    public Shop getShopById(Long shopId) {
        Shop shopInDB = shopDao.getShopById(shopId);
        if (shopInDB == null) {
            throw new ResourceNotFoundException(String.format("无法找到id为 %s 的商店", shopId));
        }
        return shopInDB;
    }
    
    @Override
    public PageResponse<Shop> getShopsByPage(int pageSize, int pageNum) {
        Long userId = UserContext.getUser().getId();
        int shopNumbers = shopDao.countShopNumbers(userId);
        int totalPages = shopNumbers % pageSize == 0 ? shopNumbers / pageSize : shopNumbers / pageSize + 1;
        List<Shop> shopLists = shopDao.getShopsByPage(pageSize, pageNum, userId);
        
        return PageResponse.of(pageSize, pageNum, totalPages, shopLists);
    }
}
