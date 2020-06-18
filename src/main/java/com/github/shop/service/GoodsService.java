package com.github.shop.service;

import com.github.shop.entity.PageResponse;
import com.github.shop.generate.Goods;

public interface GoodsService {

    Goods createGoods(Goods goods);

    Goods deleteGoods(Long goodsId);

    Goods updateGoodsById(Long id, Goods goods);

    Goods getGoodsById(Long id);

    PageResponse<Goods> getGoods(int pageSize, int pageNum, Long shopId);
}
