# MyBatis入门

学习手册

**http://www.mybatis.org/mybatis-3**

**http://www.mybatis.org/spring**

![image-20220215102412587](C:\Users\XL\Desktop\0001.png)

## 1、导入sql，MyBatis依赖

导包可以去这个网址查询，有使用趋势：

https://mvnrepository.com/search?q=mysql



```
  <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>

<!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.2</version>
</dependency>
```

## 2、配置sql和MyBatis

- 在application.properties文件中添加配置
- mapUnder    为了自动实现合并,不区分大小写和下划线    为了实现数据库的字段和实体类相对应  比如 user_name  ==UserName
- classPath就是编译后的target/classes文件夹

```
# DataSourceProperties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/community?characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong
spring.datasource.username=root
spring.datasource.password=200172
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.maximum-pool-size=15
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000

# MybatisProperties
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.nowcoder.community.entity
mybatis.configuration.useGeneratedKeys=true
mybatis.configuration.mapUnderscoreToCamelCase=true

# logger
logging.level.com.nowcoder.community=debug
```



## 3、创建Mapper接口，在里面写方法

```
package com.js.community.dao;

import com.js.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    //根据id查找用户
    User queryById(int id);
    User queryByName(String name);
    User queryByEmail(String email);
    //插入一个用户
    int InsertUser(User user);
    //更新用户信息
    int UpdateStatus(int id,int Status);
    int UpdateHeader(int id,String Header_url);
    int UpdatePassword(int id,String password);
}
```



## 4、创建xml文件配置sql 语句

- namespace写Mapper所在的路径
- 标签中的id值为对应的方法名字，**<select>**标签需要写声明返回类型为对应的实体类，<insert><uodate>标签中如果传入的参数是实体类需要声明，普通参数可以不声明
- 在sql中引用参数使用   #{username}
- sql语句中共同的部分可以提取出来复用   <sql  id="自定义">             引用的时候写<include  refid="自定义的名字即可">

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.js.community.dao.UserMapper">

    <sql id="insertFields">
        username, password, salt, email, type, status, activation_code, header_url, create_time
    </sql>

    <sql id="selectFields">
        id, username, password, salt, email, type, status, activation_code, header_url, create_time
    </sql>

    <select id="queryById" resultType="User">
        select <include refid="selectFields"></include>
        from user
        where id = #{id}
    </select>

    <select id="queryByName" resultType="User">
        select <include refid="selectFields"></include>
        from user
        where username = #{username}
    </select>

    <select id="queryByEmail" resultType="User">
        select <include refid="selectFields"></include>
        from user
        where email = #{email}
    </select>

    <insert id="InsertUser" parameterType="User" keyProperty="id">
        insert into user (<include refid="insertFields"></include>)
        values(#{username}, #{password}, #{salt}, #{email}, #{type}, #{status}, #{activationCode}, #{headerUrl}, #{createTime})
    </insert>

    <update id="UpdateStatus">
        update user set status = #{status} where id = #{id}
    </update>

    <update id="UpdateHeader">
        update user set header_url = #{header_url} where id = #{id}
    </update>

    <update id="UpdatePassword">
        update user set password = #{password} where id = #{id}
    </update>

</mapper>
```



## 5、测试

```
package com.js.community;

import com.js.community.dao.Impl.AlphaImpl;
import com.js.community.dao.UserMapper;
import com.js.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MapperTests {
    //先导入Mapper的Bean

    @Autowired(required=false)   //不加required会提示找不到这个Bean
    private UserMapper userMapper;
    @Test
    void selectTests()
    {
        User user = userMapper.queryById(101);
        System.out.println(user);
        
        
    }
}
```



## 6、调试

为了方便调试发现错误  可以把dao层的日志级别设置为debug

```
# logger
logging.level.com.js.community.dao=debug
```

