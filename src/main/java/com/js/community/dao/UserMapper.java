package com.js.community.dao;

import com.js.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    //根据id查找用户
    User queryById(int id);
    User queryByName(String username);
    User queryByEmail(String email);
    //插入一个用户
    int InsertUser(User user);
    //更新用户信息
    int UpdateStatus(int id,int status);
    int UpdateHeader(int id,String header_url);
    int UpdatePassword(int id,String password);
}
