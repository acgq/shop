package com.github.shop.entity;

public enum OrderStatus {
    PENDING,
    PAID,
    DELIVERED,
    RECEIVED;
    
    public String getName() {
        return this.name().toLowerCase();
    }
}
