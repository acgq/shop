package com.github.shop.entity;

public enum OrderStatus {
    PENDING,
    PAID,
    DELIVERED,
    RECEIVED,
    DELETED;
    
    public String getName() {
        return this.name().toLowerCase();
    }
    
    public static OrderStatus value(String val) {
        try {
            if (val == null) {
                return null;
            } else {
                return OrderStatus.valueOf(val.toUpperCase());
            }
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
