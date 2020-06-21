package com.github.shop.dao;

import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.ShoppingCartData;
import com.github.shop.generate.ShoppingCart;
import com.github.shop.generate.ShoppingCartExample;
import com.github.shop.generate.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShoppingCartDao {
    private CustomShoppingCartMapper customShoppingCartMapper;
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    public ShoppingCartDao(CustomShoppingCartMapper customShoppingCartMapper, ShoppingCartMapper shoppingCartMapper) {
        this.customShoppingCartMapper = customShoppingCartMapper;
        this.shoppingCartMapper = shoppingCartMapper;
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
    
    public ShoppingCartData getShoppingCartDataByUserIdAndShopId(long userId,
                                                                 long shopId) {
        return customShoppingCartMapper.selectShoppingCartDataByUserIdAndShopId(userId, shopId);
    }
    
    public List<ShoppingCart> getShoppingCartInfo(long goodsId) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId).andStatusEqualTo(ExistStatus.OK.getName());
        return shoppingCartMapper.selectByExample(example);
        
    }
}
