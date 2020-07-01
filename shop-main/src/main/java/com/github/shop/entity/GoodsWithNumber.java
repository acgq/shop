package com.github.shop.entity;

import com.github.shop.generate.Goods;

public class GoodsWithNumber extends Goods {
    private int number;
    
    public GoodsWithNumber(Goods goods) {
        this.setId(goods.getId());
        this.setShopId(goods.getShopId());
        this.setName(goods.getName());
        this.setDescription(goods.getDescription());
        this.setImgUrl(goods.getImgUrl());
        this.
                setPrice(goods.getPrice());
        this.setStock(goods.getStock());
        this.setStatus(goods.getStatus());
        this.setCreateTime(goods.getCreateTime());
        this.setUpdateTime(goods.getUpdateTime());
        this.setDetails(goods.getDetails());
    }
    
    public GoodsWithNumber() {
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
}
