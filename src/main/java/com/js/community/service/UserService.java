package com.js.community.service;

import com.js.community.dao.LoginTicketMapper;
import com.js.community.dao.UserMapper;
import com.js.community.entity.LoginTicket;
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

    @Autowired(required = false)
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private  MailClient mailClient;

    public User queryUserById(int userId)
    {
        return userMapper.queryById(userId);
    }

    public User queryUserByName(String username)
    {
        return userMapper.queryByName(username);
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

    //登录业务
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
       //密码为空
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        // 验证账号是否存在
        User user = userMapper.queryByName(username);
        if(user==null)
        {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }


        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码，验证的是用户的密码加上用户加密的随机字符串之后生成的密码是否正确
        String s = CommunityUtil.MD5(password + user.getSalt());
        if(!s.equals(user.getPassword()))
        {
            map.put("passwordMsg","密码错误！");
            return map;
        }

        // 生成登录凭证,存放到数据库中的login_ticket表
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + (expiredSeconds*1000) ));
        loginTicketMapper.insertLoginTicket(loginTicket);
        //debug
        System.out.println("登录时候的Expired"+loginTicket.getExpired());


        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出
    public void logout(String ticket)
    {
        //LoginTicket loginTicket = loginTicketMapper.selectLoginTicket(ticket);
        //System.out.println("退出时候的Expired"+loginTicket.getExpired());


        loginTicketMapper.updateLoginTicket(ticket,1);
        //LoginTicket loginTicket2 = loginTicketMapper.selectLoginTicket(ticket);
        //System.out.println("22222退出时候的Expired"+loginTicket2.getExpired());
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectLoginTicket(ticket);
    }


    //更新头像路径
    public int updateHeader(int id,String header_url)
    {
        return userMapper.UpdateHeader(id,header_url);
    }

    //修改密码  这里传入的password应该为加密后的
    public int updatePassword(int id,String password)
    {
        return userMapper.UpdatePassword(id,password);
    }
}
