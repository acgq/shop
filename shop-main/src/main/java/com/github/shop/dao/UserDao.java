package com.github.shop.dao;

import com.github.shop.generate.User;
import com.github.shop.generate.UserExample;
import com.github.shop.generate.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDao {
    private final UserMapper userMapper;
    
    @Autowired
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public int insertUser(User user) {
        int insert = userMapper.insert(user);
        return insert;
    }
    
    public User getUserByTel(String tel) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTelEqualTo(tel);
        List<User> users = userMapper.selectByExample(userExample);
        return users.get(0);
    }
    
    public User getUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }
}
