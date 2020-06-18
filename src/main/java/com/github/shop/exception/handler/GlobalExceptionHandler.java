package com.github.shop.exception.handler;

import com.github.shop.entity.Response;
import com.github.shop.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    ResponseEntity<?> handleUnAuthentication(ServiceException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Response.ofMessage(e.getMessage()));
    }

}
