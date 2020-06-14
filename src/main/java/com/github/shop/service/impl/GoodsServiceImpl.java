package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;
import com.github.shop.generate.User;
import com.github.shop.service.GoodsService;
import com.github.shop.service.UserContext;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GoodsServiceImpl implements GoodsService {
    private final GoodsDao goodsDao;
    private final ShopDao shopDao;

    @Autowired
    public GoodsServiceImpl(GoodsDao goodsDao, ShopDao shopDao) {
        this.goodsDao = goodsDao;
        this.shopDao = shopDao;
    }

    @Override
    public Goods createGoods(Goods goods) {
        if (checkOwnership(goods)) {
            return goodsDao.createGoods(goods);
        } else {
            throw new UnauthenticatedException("没有权限在此商店添加商品");
        }
    }

    private boolean checkOwnership(Goods goods) {
        User user = UserContext.getUser();
        Shop shop = shopDao.getShopById(goods.getShopId());
        if (shop != null) {
            return Objects.equals(user.getId(), shop.getOwnerUserId());
        }
        return false;
    }
}
