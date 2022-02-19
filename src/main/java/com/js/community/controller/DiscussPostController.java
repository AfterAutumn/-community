package com.js.community.controller;

import com.js.community.entity.DiscussPost;
import com.js.community.entity.Page;
import com.js.community.entity.User;
import com.js.community.service.DiscussPostService;
import com.js.community.service.UserService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

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
}
