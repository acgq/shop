package com.github.shop.dao;

import com.github.shop.generate.Goods;
import com.github.shop.generate.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GoodsDao {
    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsDao(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public Goods createGoods(Goods goods) {
        goods.setCreateTime(Instant.now());
        goods.setUpdateTime(Instant.now());
        goodsMapper.insert(goods);
        return goods;
    }

    public Goods getGoodsById(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    public Goods updateGoods(Goods goods) {
        goods.setUpdateTime(Instant.now());
        goodsMapper.updateByPrimaryKey(goods);
        return goods;
    }
}
