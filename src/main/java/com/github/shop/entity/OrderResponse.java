package com.github.shop.entity;

import com.github.shop.generate.Order;
import com.github.shop.generate.Shop;

import java.util.List;

public class OrderResponse {
    private Long id;
    private String expressCompany;
    private String expressId;
    private String status;
    private String address;
    private Shop shop;
    private Long shopId;
    private List<GoodsWithNumber> goods;
    
    public OrderResponse(Order order) {
        setId(order.getId());
        setExpressCompany(order.getExpressCompany());
        setExpressId(order.getExpressId());
        setStatus(order.getStatus());
        setAddress(order.getAddress());
        setShopId(order.getShopId());
    }
    
    public OrderResponse() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExpressCompany() {
        return expressCompany;
    }
    
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }
    
    public String getExpressId() {
        return expressId;
    }
    
    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Shop getShop() {
        return shop;
    }
    
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    
    public Long getShopId() {
        return shopId;
    }
    
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    
    public List<GoodsWithNumber> getGoods() {
        return goods;
    }
    
    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
