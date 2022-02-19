package com.js.community.service;

import com.js.community.dao.DiscussPostMapper;
import com.js.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    //查询从offset开始每页limit条数据的帖子信息
    public List<DiscussPost> findDiscussPosts(int userId, int offset,  int limit)
    {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    //查询一共有多少条帖子
    //@Param给参数取一个别名  当只有一个参数并且需要在<if>中使用就必须要加注解
    public int countDiscussPosts(@Param("userId")  int userId)
    {
        return discussPostMapper.countDiscussPosts(userId);
    }
}
