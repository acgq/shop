package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.entity.GoodsStatus;
import com.github.shop.entity.ResourceNotFoundException;
import com.github.shop.exception.UnAuthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;
import com.github.shop.generate.User;
import com.github.shop.service.GoodsService;
import com.github.shop.service.UserContext;
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
            throw new UnAuthenticatedException("没有权限在此商店添加商品");
        }
    }

    @Override
    public Goods deleteGoods(Long goodsId) {
        Goods goods = getGoodsById(goodsId);

        if (!checkOwnership(goods)) {
            throw new UnAuthenticatedException("没有权限删除商品");
        }
        goods.setStatus(GoodsStatus.DELETED);
        return goodsDao.updateGoods(goods);
    }

    @Override
    public Goods updateGoodsById(Long id, Goods goods) {
        Goods goodsInDB = getGoodsById(id);
        if (!checkOwnership(goodsInDB)) {
            throw new UnAuthenticatedException("没有权限更新商品信息");
        }
        Goods goodsToBeUpdated = updateGoods(goodsInDB, goods);

        return goodsDao.updateGoods(goodsToBeUpdated);
    }

    private Goods updateGoods(Goods oldGoods, Goods goods) {
        oldGoods.setName(goods.getName());
        oldGoods.setDetails(goods.getDetails());
        oldGoods.setDescription(goods.getDescription());
        oldGoods.setImgUrl(goods.getImgUrl());
        oldGoods.setPrice(goods.getPrice());
        oldGoods.setShopId(goods.getShopId());
        oldGoods.setStock(goods.getStock());
        return oldGoods;
    }

    @Override
    public Goods getGoodsById(Long id) {
        Goods goodsInDB = goodsDao.getGoodsById(id);
        if (goodsInDB == null) {
            throw new ResourceNotFoundException(String.format("找不到 %s 对应商品", id));
        }
        return goodsInDB;
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
