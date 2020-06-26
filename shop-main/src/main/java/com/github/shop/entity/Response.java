package com.github.shop.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private String message;
    private T data;

    public static Response ofMessage(String message) {
        return new Response(message, null);
    }

    public static <T> Response<T> ofData(T data) {
        return new Response<T>(null, data);
    }

    public Response() {
    }

    private Response(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
