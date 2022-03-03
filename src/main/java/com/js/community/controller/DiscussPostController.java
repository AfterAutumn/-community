package com.js.community.controller;

import com.js.community.entity.Comment;
import com.js.community.entity.DiscussPost;
import com.js.community.entity.Page;
import com.js.community.entity.User;
import com.js.community.service.CommentService;
import com.js.community.service.DiscussPostService;
import com.js.community.service.UserService;
import com.js.community.utils.CommunityConstant;
import com.js.community.utils.CommunityUtil;
import com.js.community.utils.HostHolder;
import com.js.community.utils.SensitiveFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page)
    {
        //不需要向model中添加page对象  因为方法调用前，SpringMvc会自动实例化Model和Page，并将Page注入Model
        // 所以，在Thymeleaf中可以直接访问Page对象中的数据
        //获取总行数
        page.setRows(discussPostService.countDiscussPosts(0));
        page.setPath("/index");

        //获取帖子数据
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(),page.getLimit());
        //创建新集合
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        //遍历list
        if(list!=null)
        {
            for(DiscussPost post:list)
            {
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.queryUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    //发布帖子
    @RequestMapping(value = "/discuss/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content)
    {
        User user = hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"您还没有登录，请先登录！");
        }
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        //报错的情况之后统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");

    }

    //根据id查询帖子
    @RequestMapping(value = "/discuss/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model,Page page)
    {
        //帖子
        DiscussPost discussPost = discussPostService.selectPostById(id);
        model.addAttribute("post",discussPost);

        //通过userid查找对应的用户
        User user = userService.queryUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //显示帖子
        // 评论分页信息
        page.setLimit(5);
        page.setRows(discussPost.getCommentCount());
        page.setPath("/discuss/detail/"+id);

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 获取评论列表
        List<Comment> comments = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, id, page.getOffset(), page.getLimit());

        // 评论VO列表  从评论列表里取出每一条评论的用户名称和内容
        List<Map<String,Object>> commentVoList=new ArrayList<>();
        if(comments!=null) {
            for (Comment comment : comments) {
                //创建map来保存用户名称和内容
                Map<String,Object> commentVo=new HashMap<>();
                //保存评论和用户
                commentVo.put("user",userService.queryUserById(comment.getUserId()));
                commentVo.put("comment",comment);

                //***
                // 每一条评论又可能会有回复列表  跟评论列表操作一样
                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.queryUserById(reply.getUserId()));
                        // 回复目标  ==0表示没有对特定人进行回复  否则对特定人进行回复
                        User target = reply.getTargetId() == 0 ? null : userService.queryUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }

                //把回复列表添加到comments里
                commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                //把每一次遍历的map添加到list里
                commentVoList.add(commentVo);

            }
        }
        //向模板添加集合
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";

    }

}
