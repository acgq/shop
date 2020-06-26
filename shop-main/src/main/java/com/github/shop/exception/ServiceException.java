package com.github.shop.exception;

public class ServiceException extends RuntimeException {
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
