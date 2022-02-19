package com.js.community.service;

import com.js.community.dao.UserMapper;
import com.js.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired(required = false)
    private UserMapper userMapper;

    public User queryUserById(int userId)
    {
        return userMapper.queryById(userId);
    }

}
