package com.js.community.service;

import com.js.community.dao.UserMapper;
import com.js.community.entity.User;
import com.js.community.utils.CommunityConstant;
import com.js.community.utils.CommunityUtil;
import com.js.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired(required = false)
    private UserMapper userMapper;

    //注入域名和工程路径
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private  MailClient mailClient;

    public User queryUserById(int userId)
    {
        return userMapper.queryById(userId);
    }

    //注册业务
    public Map<String,Object>  register(User user)
    {
        Map<String,Object> map=new HashMap<>();
        if(user==null)
        {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        //验证用户名是否已经存在
        if(userMapper.queryByName(user.getUsername())!=null)
        {
            map.put("usernameMsg","用户名已经存在");
            return map;
        }
        //验证邮箱是否已经存在
        if(userMapper.queryByEmail(user.getEmail())!=null)
        {
            map.put("emailMsg","邮箱已经存在");
            return map;
        }
        //注册用户
        //保存用户随机字符串 截取5个
        user.setSalt(StringUtils.substring(CommunityUtil.generateUUID(),5));
        //保存密码
        String key=user.getPassword()+user.getSalt();
        user.setPassword(CommunityUtil.MD5(key));
        user.setType(0);
        user.setStatus(0);
        //激活码使用随机生成的字符串
        user.setActivationCode(CommunityUtil.generateUUID());
        //使用牛客网的头像库随机生成
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.InsertUser(user);


        //激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    //激活业务
    public int activation(int userId,String code)
    {
        User user = userMapper.queryById(userId);
        if(user.getStatus()==1)
        {
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code))
        {
            userMapper.UpdateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

}
