package com.github.shop.entity;

import java.util.Objects;

public class UserWithContent<T> {
    private Long userId;
    private T content;
    
    public UserWithContent(Long userId, T content) {
        this.userId = userId;
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "UserWithContent{" +
                "userId=" + userId +
                ", content=" + content +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserWithContent<?> that = (UserWithContent<?>) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(content, that.content);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, content);
    }
}
