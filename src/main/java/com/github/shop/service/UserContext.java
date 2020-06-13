package com.github.shop.service;

public class UserContext {
    private static final ThreadLocal<User> userInThread = new ThreadLocal<>();

    public static void setUser(User user) {
        userInThread.set(user);
    }

    public static User getUser() {
        return userInThread.get();
    }

    public static void clearUser() {
        userInThread.remove();
    }
}
