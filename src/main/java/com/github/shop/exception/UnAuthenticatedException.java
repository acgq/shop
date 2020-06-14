package com.github.shop.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * not authenticated to do something.
 */
public class UnAuthenticatedException extends ServiceException {
    public UnAuthenticatedException(String message) {
        super(message, HttpServletResponse.SC_FORBIDDEN);
    }
}
