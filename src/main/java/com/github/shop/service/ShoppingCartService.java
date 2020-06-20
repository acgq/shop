package com.github.shop.service;

import com.github.shop.entity.PageResponse;
import com.github.shop.entity.ShoppingCartData;

public interface ShoppingCartService {
    PageResponse<ShoppingCartData> getShoppingCartOfUser(Long id, int pageNum, int pageSize);
    
    ShoppingCartData deleteShoppingCart(Long goodsId);
}
