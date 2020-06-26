package com.github.shop.entity;

import java.util.List;

public class OrderInfo {
    private long orderId;
    private List<GoodsInfo> goods;
    
    public OrderInfo() {
    }
    
    public List<GoodsInfo> getGoods() {
        return goods;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
