package com.github.shop.entity;

import com.github.shop.exception.ServiceException;

import javax.servlet.http.HttpServletResponse;

/**
 * cannot find resource.
 */
public class ResourceNotFoundException extends ServiceException {

    public ResourceNotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }
}
