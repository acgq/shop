package com.github.shop.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * cannot find resource.
 */
public class ResourceNotFoundException extends ServiceException {
    
    public ResourceNotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }
}
