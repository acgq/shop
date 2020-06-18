package com.github.shop.service;

import com.github.shop.entity.PageResponse;
import com.github.shop.generate.Shop;

public interface ShopService {
    
    Shop createShop(Shop shop);
    
    Shop updateShop(Long shopId, Shop shop);
    
    Shop deleteShop(Long ShopId);
    
    Shop getShopById(Long shopId);
    
    PageResponse<Shop> getShopsByPage(int pageSize, int pageNum);
    
}
