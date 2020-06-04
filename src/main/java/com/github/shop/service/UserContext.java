package com.github.shop.service;

import com.github.shop.generate.User;

public class UserContext {
    private static final ThreadLocal<User> userInThread = new ThreadLocal<>();

    public static void setUser(User user) {
        userInThread.set(user);
    }

    public static User getUser() {
        return userInThread.get();
    }
}
