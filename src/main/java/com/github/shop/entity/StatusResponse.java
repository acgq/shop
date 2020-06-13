package com.github.shop.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
    private boolean login;
    private User user;

    public static StatusResponse loginResponse(User user) {
        return new StatusResponse(true, user);
    }

    public static StatusResponse notLoginResponse() {
        return new StatusResponse(false, null);
    }

    private StatusResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public StatusResponse() {
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
