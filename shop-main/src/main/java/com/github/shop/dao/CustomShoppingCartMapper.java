package com.github.shop.dao;

import com.github.shop.entity.ShoppingCartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomShoppingCartMapper {
    int countShopNumberByUserId(@Param("userId") long userId);
    
    List<ShoppingCartData> selectShoppingCartDataByUserId(@Param("userId") long userId,
                                                          @Param("offset") int offset,
                                                          @Param("pageSize") int pageSize);
    
    ShoppingCartData selectShoppingCartDataByUserIdAndShopId(@Param("userId") long userId,
                                                             @Param("shopId") long shopId);
}
