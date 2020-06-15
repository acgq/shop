package com.github.shop.entity;

public class Response<T> {

    private T data;

    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
