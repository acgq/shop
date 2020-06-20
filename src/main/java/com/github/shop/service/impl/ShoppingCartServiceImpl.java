package com.github.shop.service.impl;

import com.github.shop.dao.ShoppingCartDao;
import com.github.shop.entity.GoodsWithNumber;
import com.github.shop.entity.PageResponse;
import com.github.shop.entity.ShoppingCartData;
import com.github.shop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private ShoppingCartDao shoppingCartDao;
    
    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartDao shoppingCartDao) {
        this.shoppingCartDao = shoppingCartDao;
    }
    
    //合并购物车中信息
    private static ShoppingCartData merge(List<ShoppingCartData> goodsInSameShop) {
        if (goodsInSameShop.size() == 0) {
            return null;
        }
        ShoppingCartData mergedResult = new ShoppingCartData();
        mergedResult.setGoods(new ArrayList<>());
        mergedResult.setShop(goodsInSameShop.get(0).getShop());
        for (ShoppingCartData shoppingCartData : goodsInSameShop) {
            for (GoodsWithNumber good : shoppingCartData.getGoods()) {
                mergedResult.getGoods().add(good);
            }
        }
        return mergedResult;
    }
    
    @Override
    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long id, int pageNum, int pageSize) {
        int totalShopNumber = shoppingCartDao.countShopNumberByUserId(id);
        int totalPage = totalShopNumber % pageSize == 0 ? totalShopNumber / pageSize : totalShopNumber / pageSize + 1;
        //将查找出的商品按店铺归类。
        List<ShoppingCartData> result = shoppingCartDao.getShoppingCartDataByUserId(id, pageNum, pageSize);
//                        .stream()
//                        .collect(Collectors.groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()))
//                        .values()
//                        .stream()
//                        .map(ShoppingCartServiceImpl::merge)
//                        .collect(Collectors.toList());
        return PageResponse.of(pageSize, pageNum, totalPage, result);
    }
    
    @Override
    public ShoppingCartData deleteShoppingCart(Long goodsId) {
        return null;
    }
}
