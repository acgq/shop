package com.github.shop.entity;

import java.util.List;

public class OrderInfo {
    private long orderId;
    private List<GoodsWithNumber> goods;
    
    public OrderInfo() {
    }
    
    public List<GoodsWithNumber> getGoods() {
        return goods;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
