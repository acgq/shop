package com.github.shop.entity;

public enum ExistStatus {
    OK, DELETED;

    public String getName() {
        return this.name().toLowerCase();
    }
}
