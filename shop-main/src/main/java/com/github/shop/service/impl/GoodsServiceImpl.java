package com.github.shop.service.impl;

import com.github.shop.dao.GoodsDao;
import com.github.shop.dao.ShopDao;
import com.github.shop.entity.ExistStatus;
import com.github.shop.entity.PageResponse;
import com.github.shop.exception.ResourceNotFoundException;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.Goods;
import com.github.shop.generate.Shop;
import com.github.shop.generate.User;
import com.github.shop.service.GoodsService;
import com.github.shop.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
            goods.setStatus(ExistStatus.OK.getName());
            return goodsDao.createGoods(goods);
        } else {
            throw new UnauthenticatedException("没有权限在此商店添加商品");
        }
    }
    
    @Override
    public Goods deleteGoods(Long goodsId) {
        Goods goods = getGoodsById(goodsId);
        
        if (!checkOwnership(goods)) {
            throw new UnauthenticatedException("没有权限删除商品");
        }
        goods.setStatus(ExistStatus.DELETED.getName());
        return goodsDao.updateGoods(goods);
    }
    
    @Override
    public Goods updateGoodsById(Long id, Goods goods) {
        Goods goodsInDB = getGoodsById(id);
        if (!checkOwnership(goodsInDB)) {
            throw new UnauthenticatedException("没有权限更新商品信息");
        }
        goods.setId(id);
        if (goods.getCreateTime() == null) {
            goods.setCreateTime(goodsInDB.getCreateTime());
        }
        return goodsDao.updateGoods(goods);
    }
    
    
    @Override
    public Goods getGoodsById(Long id) {
        Goods goodsInDB = goodsDao.getGoodsById(id);
        if (goodsInDB == null) {
            throw new ResourceNotFoundException(String.format("找不到 %s 对应商品", id));
        }
        return goodsInDB;
    }
    
    @Override
    public PageResponse<Goods> getGoods(int pageSize, int pageNum, Long shopId) {
        int totalNum = goodsDao.getGoodsCount(shopId);
        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        List<Goods> goods = goodsDao.getGoods(pageSize, pageNum, shopId);
        PageResponse<Goods> res = PageResponse.of(pageSize, pageNum, totalPage, goods);
        return res;
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
