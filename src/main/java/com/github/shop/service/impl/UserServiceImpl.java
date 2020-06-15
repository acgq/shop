package com.github.shop.service.impl;import com.github.shop.dao.UserDao;import com.github.shop.generate.User;import com.github.shop.service.UserService;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.dao.DuplicateKeyException;import org.springframework.stereotype.Service;import java.time.Instant;import java.util.Optional;import java.util.UUID;@Servicepublic class UserServiceImpl implements UserService {    private final UserDao userDao;    @Autowired    public UserServiceImpl(UserDao userDao) {        this.userDao = userDao;    }    public User createUser(String tel) {        User user = new User();        user.setTel(tel);        user.setCreateTime(Instant.now());        user.setUpdateTime(Instant.now());        userDao.insertUser(user);        return user;    }    public User createUserIfNotExist(String tel) {        try {            User user = new User();            user.setTel(tel);            user.setName(UUID.randomUUID().toString().replace("-", ""));            user.setCreateTime(Instant.now());            user.setUpdateTime(Instant.now());            userDao.insertUser(user);            return user;        } catch (DuplicateKeyException ex) {            return userDao.getUserByTel(tel);        }    }    @Override    public Optional<User> getUserByTel(String tel) {        return Optional.ofNullable(userDao.getUserByTel(tel));    }}