package com.github.shop.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * not authenticated to do something.
 */
public class UnauthenticatedException extends ServiceException {
    public UnauthenticatedException(String message) {
        super(message, HttpServletResponse.SC_FORBIDDEN);
    }
}
