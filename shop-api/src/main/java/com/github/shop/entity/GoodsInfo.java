package com.github.shop.entity;

public class GoodsInfo {
    
    private long id;
    private int number;
    
    public GoodsInfo(long id, int number) {
        this.id = id;
        this.number = number;
    }
    
    public GoodsInfo() {
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
}