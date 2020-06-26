package com.github.shop.exception;

import java.io.Serializable;

public class ServiceException extends RuntimeException implements Serializable {
    private int statusCode;
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
