package com.github.shop.controller;

import com.github.shop.entity.Response;
import com.github.shop.generate.Goods;
import com.github.shop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping()
    public Response<Goods> createGoods(@RequestBody Goods goods) {
        clean(goods);
        Goods goodsFromDB = goodsService.createGoods(goods);
        return Response.of(goodsFromDB);
    }

    /**
     * clean goods info.
     *
     * @param goods
     */
    private void clean(Goods goods) {
        goods.setId(null);
        goods.setCreateTime(null);
        goods.setUpdateTime(null);
    }
}
