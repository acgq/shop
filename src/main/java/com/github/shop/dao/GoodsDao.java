package com.github.shop.dao;

import com.github.shop.generate.Goods;
import com.github.shop.generate.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsDao {
    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsDao(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public Goods createGoods(Goods goods) {
        int id = goodsMapper.insert(goods);
        goods.setId((long) id);
        return goods;
    }
}
