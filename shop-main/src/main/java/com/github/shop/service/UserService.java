package com.github.shop.service;

import com.github.shop.generate.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User createUser(String tel);

    User createUserIfNotExist(String tel);

    Optional<User> getUserByTel(String tel);
}
