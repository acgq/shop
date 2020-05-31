package com.github.shop.service;

import com.github.shop.dao.UserDao;
import com.github.shop.generate.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String tel) {
        User user = new User();
        user.setTel(tel);
        userDao.insertUser(user);
        return user;
    }

    public User createUserIfNotExist(String tel) {
        try {

            User user = new User();
            user.setTel(tel);
            user.setName(UUID.randomUUID().toString().replace("-", ""));
            userDao.insertUser(user);
            return user;
        } catch (DuplicateKeyException ex) {
            return userDao.getUserByTel(tel);
        }
    }
}
