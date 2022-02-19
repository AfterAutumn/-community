package com.js.community.dao;

import com.js.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //查询从offset开始每页limit条数据的帖子信息
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);
    //查询一共有多少条帖子
    //@Param给参数取一个别名  当只有一个参数并且需要在<if>中使用就必须要加注解
    int countDiscussPosts(@Param("userId")  int userId);

}
