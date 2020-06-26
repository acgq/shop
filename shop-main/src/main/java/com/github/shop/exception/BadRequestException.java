package com.github.shop.exception;

import javax.servlet.http.HttpServletResponse;

public class BadRequestException extends ServiceException {
    public BadRequestException(String message) {
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }
}
