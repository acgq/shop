package com.github.shop.controller;

import com.github.shop.entity.PageResponse;
import com.github.shop.entity.Response;
import com.github.shop.generate.Goods;
import com.github.shop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody Goods goods, HttpServletResponse response) {
        clean(goods);
        Goods goodsFromDB = goodsService.createGoods(goods);
        response.setStatus(HttpServletResponse.SC_CREATED);
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

    @DeleteMapping("/goods/{id}")
    public Response<Goods> deleteGoods(@PathVariable("id") Long goodsId, HttpServletResponse response) {
        Goods goods = goodsService.deleteGoods(goodsId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return Response.of(goods);
    }

    @RequestMapping(value = "/goods/{id}", method = {RequestMethod.POST, RequestMethod.PATCH})
    @ResponseBody
    public Response<Goods> updateGoods(@PathVariable("id") Long id, @RequestBody Goods goods) {
        Goods updatedGoods = goodsService.updateGoodsById(id, goods);
        return Response.of(updatedGoods);
    }

    @GetMapping("/goods/{id}")
    public Response<Goods> getGoodsById(@PathVariable("id") Long goodsId) {
        return Response.of(goodsService.getGoodsById(goodsId));
    }

    // todo 获取所有商品
    @GetMapping("/goods")
    public PageResponse<Goods> getGoodsInPage() {
        return null;
    }
}
