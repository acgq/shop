package com.github.shop.entity;

import com.github.shop.generate.Order;
import com.github.shop.generate.Shop;

import java.util.List;

public class OrderResponse extends Order {
    private long id;
    private String expressCompany;
    private String expressId;
    private String status;
    private String address;
    private Shop shop;
    private List<GoodsWithNumber> goods;
    
    public OrderResponse(Order order) {
        setId(order.getId());
        setExpressCompany(order.getExpressCompany());
        setExpressId(order.getExpressId());
        setStatus(order.getStatus());
        setAddress(order.getAddress());
        setShopId(order.getShopId());
        setTotalPrice(order.getTotalPrice());
        setCreateTime(order.getCreateTime());
        setUpdateTime(order.getUpdateTime());
    }
    
    public OrderResponse() {
    }
    
    public Shop getShop() {
        return shop;
    }
    
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    
    public List<GoodsWithNumber> getGoods() {
        return goods;
    }
    
    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
