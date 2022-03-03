#  Redis入门

   NoSQL非关系型数据库

• Redis是一款基于**键值对**的**NoSQL**数据库，它的值支持多种数据结构：字符串(strings)、哈希(hashes)、列表(lists)、集合(sets)、有序集合(sorted sets)等。

• Redis将所有的数据都存放在**内存**中，所以它的读写性能十分惊人。同时，Redis还可以将内存中的数据以**快照或日志**的形式保存到硬盘上，以保证数据的安全性。

​     快照：把内存中的所有数据存放到硬盘上 比较耗时间   一般作为备份使用

​     日志：  存取执行Redis命令，实时，速度快

• Redis典型的应用场景包括：缓存、排行榜、计数器、社交网络、消息队列等。



官网  ：  https://redis.io/

gitHub    https://github.com/microsoftarchive/redis

## 配置Redis环境变量

## 常用命令

### 存取字符串(strings)

```
redis-cli   //连接Redis
127.0.0.1:6379[2]> select 1   //选择数据库  一个16个 0-15
127.0.0.1:6379[1]> flushdb   //清除数据库
//存取数据  以key-value形式 存取字符串
127.0.0.1:6379[1]> set test:count 1
OK
127.0.0.1:6379[1]> get test:count
"1"

//增加减少
127.0.0.1:6379[1]> incr test:count
(integer) 2
127.0.0.1:6379[1]> decr test:count
(integer) 1

```



### 存取哈希(hashes)

```
127.0.0.1:6379[1]> hset test:user id 1
(integer) 1
127.0.0.1:6379[1]> hset test:user username wangwu
(integer) 1

//获取相应值

127.0.0.1:6379[1]> hget test:user id
"1"
127.0.0.1:6379[1]> hget test:user username
"wangwu"
```



### 存取列表(lists)

```
127.0.0.1:6379[1]> lpush test:ids 101 102 103   //左端方式存入数据   最终结果为  103 102 101
(integer) 3
127.0.0.1:6379[1]> llen test:ids       //取列表长度
(integer) 3
127.0.0.1:6379[1]> lindex test:ids 0
"103"
127.0.0.1:6379[1]> l range test:ids 0 2
(error) ERR unknown command 'l'
127.0.0.1:6379[1]> lrange test:ids 0 2

1) "103"
2) "102"
3) "101"
127.0.0.1:6379>  rpop test:ids    //从右端弹出一个数据 列表长度减一
"101"
127.0.0.1:6379>  rpop test:ids
"102"
127.0.0.1:6379>
```



### 存取集合(sets)   无序

```
127.0.0.1:6379> sadd test:teachers aaa bbb ccc   //存入数据 aaa bbb ccc
(integer) 3
127.0.0.1:6379> scard test:teachers    //获取集长度
(integer) 3
127.0.0.1:6379> spop test:teachers         //随机弹出一个数据
"aaa"
127.0.0.1:6379> smembers test:teachers     //查询集合数据

1) "ccc"
2) "bbb"
   127.0.0.1:6379>
```



### 存取有序集合(sorted sets)

1. ```
   127.0.0.1:6379> zadd test:students 10 aaa 20 bbb 30 ccc    //分数和姓名
   (integer) 3
   127.0.0.1:6379> zcard test:students       //统计有多少个数据
   (integer) 3
   127.0.0.1:6379> zscore test:students ccc
   "30"
   127.0.0.1:6379> zrank test:students ccc  //返回某一个值的排名 由小到大
   (integer) 2
   127.0.0.1:6379> zrank test:students aaa
   (integer) 0
   127.0.0.1:6379> zrange test:students 0 2
   
   1) "aaa"
   2) "bbb"
   3) "ccc"
   ```

   

### 常用全局命令

```
127.0.0.1:6379> keys *    //查看所有的key
1) "test:ids"
2) "test:students"
3) "test:teachers"

127.0.0.1:6379> type test:ids    //看某一个key的类型
list
127.0.0.1:6379> exists test:user /是否存在某一个key
(integer) 0
127.0.0.1:6379> expire test:ids 10      //给key设置过期时间 /秒
(integer) 1
127.0.0.1:6379> keys *
1) "test:ids"
2) "test:students"
3) "test:teachers"
127.0.0.1:6379> keys *
1) "test:students"
2) "test:teachers"
127.0.0.1:6379>



```





# Spring整合Redis

## 1、引入依赖 - spring-boot-starter-data-redis 

```
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```



## 2、配置Redis - 配置数据库参数 - 编写配置类，构造RedisTemplate

```
#RedisProperties
spring.redis.database=1        //选择的库  一共16个  从0-15
spring.redis.host=localhost         //地址ip
spring.redis.port=6379       //端口
```





##  3、 访问Redis

### 1、编写RedisConfig

```
@Configuration
public class RedisConfig {

    //定义第三方Bean
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        //连接由Redis连接工厂创建
        //实例化Bean
        RedisTemplate<String, Object> template=new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //配置序列化方式存放到Redis
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式  设置为json 方便读取转换
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        //设置hash的value序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //使设置生效
        template.afterPropertiesSet();
        return template;
        
    }

}
```



### 2、通过redisTemplate访问Redis

- #### redisTemplate.opsForValue() 

- ```
  @Test
  public void testStrings() {
      String redisKey = "test:count";
      redisTemplate.opsForValue().set(redisKey, 1);
      System.out.println(redisTemplate.opsForValue().get(redisKey));
      System.out.println(redisTemplate.opsForValue().increment(redisKey));
      System.out.println(redisTemplate.opsForValue().decrement(redisKey));
  }
  ```

- #### redisTemplate.opsForHash() 

- ```
  @Test
  public void testHashes() {
      String redisKey = "test:user";
  
      redisTemplate.opsForHash().put(redisKey, "id", 1);
      redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
      redisTemplate.opsForHash().put(redisKey,"age",18);
  
      System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
      System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
      System.out.println(redisTemplate.opsForHash().get(redisKey,"age"));
  }
  ```

- #### redisTemplate.opsForList()

- ```
  @Test
  public void testLists() {
      String redisKey = "test:ids";
      redisTemplate.opsForList().rightPush(redisKey,104);
      redisTemplate.opsForList().rightPush(redisKey,105);
      redisTemplate.opsForList().rightPush(redisKey,106);
  
      System.out.println(redisTemplate.opsForList().size(redisKey));
      System.out.println(redisTemplate.opsForList().index(redisKey, 0));
      System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
      System.out.println(redisTemplate.opsForList().rightPop(redisKey));
  
  }
  ```

- #### redisTemplate.opsForSet()

- ```
  @Test
  public void testSets() {
      String redisKey = "test:teachers";
  
      redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");
  
      System.out.println(redisTemplate.opsForSet().size(redisKey));
      System.out.println(redisTemplate.opsForSet().pop(redisKey));
      System.out.println(redisTemplate.opsForSet().members(redisKey));
  }
  ```

- #### redisTemplate.opsForZSet()

- ```
  @Test
  public void testSortedSets() {
      String redisKey = "test:students";
  
      redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
      redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
      redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
      redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
      redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);
  
      System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
      System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
      System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));
      System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
  }
  ```

  

#### 把key绑定到一个对象，不用多次传入key

```
// 绑定key的对象
@Test
public void testBoundOperations() {
    String redisKey = "test:count";
    //利用绑定的对象操作
    BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
    operations.increment();
    operations.increment();
    operations.increment();
    System.out.println(operations.get());
}
```



#### 编程式事务

Redis事务  把操作命令放到一个队列，提交时候一起提交所有命令

因此一般不在事务里做查询   编程式事务

```
// 编程式事务
@Test
public void testTransaction() {
    //匿名实现类
    Object result = redisTemplate.execute(new SessionCallback() {
        @Override
        public Object execute(RedisOperations redisOperations) throws DataAccessException {
            String redisKey = "text:tx";

            // 启用事务
            redisOperations.multi();
            redisOperations.opsForSet().add(redisKey, "zhangsan");
            redisOperations.opsForSet().add(redisKey, "lisi");
            redisOperations.opsForSet().add(redisKey, "wangwu");

            System.out.println(redisOperations.opsForSet().members(redisKey));

            // 提交事务
            return redisOperations.exec();
        }
    });
    System.out.println(result);
}
```