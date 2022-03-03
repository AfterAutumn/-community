package com.js.community.service;

import com.js.community.dao.DiscussPostMapper;
import com.js.community.entity.DiscussPost;
import com.js.community.utils.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    //增加帖子
    public int addDiscussPost(DiscussPost discussPost)
    {
        if(discussPost==null)
        {
            throw new IllegalArgumentException("帖子参数不能为空");
        }
        //转义帖子中的标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    //根据id查询帖子
    public DiscussPost selectPostById(int id)
    {
         return discussPostMapper.selectPostById(id);
    }

    //增加帖子数量
    public int updateCommentCount(int id, int commentCount)
    {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

}

