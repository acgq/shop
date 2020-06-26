package com.github.shop.entity;

import java.io.Serializable;

public class GoodsInfo implements Serializable {
    
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
