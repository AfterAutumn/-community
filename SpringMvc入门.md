# SpringMvc入门

## HTTP

HTTP手册文档：https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Overview#HTTP_%E6%B5%81

1、浏览器向服务器发送请求；

2、服务器相应请求发送HTML页面；

3、浏览器解析HTML，当需要访问页面中的css，js等时会再次向服务器发送请求，因此会有多次请求。

![](C:\Users\XL\Desktop\SpringMvc\1.png)



## Spring MVC

1、Controlller处理请求 

2、Model 数据封装

3、View渲染返回页面

![image-20220210190109724](C:\Users\XL\Desktop\SpringMvc\2.png)



### 前端控制器流程

​        前端控制器即DispatcherServlet负责处理请求，并且把请求给到相应的Controller，之后Controller处理请求把数据封装到model发送到DispatcherServlet，之后DispatcherServlet在把model发送给视图解析模板进行数据替换，最终返回动态页面。

DispacherServlet检测到代码中的Model后自动创建

![image-20220210190646497](C:\Users\XL\Desktop\SpringMvc\3.png)

## Thymeleaf

官方文档：

https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

![image-20220210191546527](C:\Users\XL\Desktop\SpringMvc\4.png)



# 应用

Sping  .properties配置文件的文档说明： 实质是更改相应properties配置文件中的属性

https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/#appendix



## **1、关闭Thymeleaf缓存**   

开发中一般关闭缓存，防止数据更修出错；项目上线一般开启缓存，减少服务器压力；

![image-20220210192230675](C:\Users\XL\Desktop\SpringMvc\5.png)

在templates文件夹下创建一个模板文件

- 开头添加Thymeleaf的声明
- 属性前添加开头th：    值用$表达式   
- 渲染时会自动把相应的值填入
- **默认情况下  表达式从Model中获取数据**

![image-20220210200753249](C:\Users\XL\Desktop\SpringMvc\6.png)



## **2、获取请求响应对象(底层方法)**

- 常用接口 HttpServlerRequest 和HttpServlerResponse ,声明请求和响应对象 
- 获取请求数据  request.getMethod()获取请求方式...
- request.getHeaderNames获取请求头 遍历输出
- request.getParameter获取请求中的参数    在请求地址栏加上  ？code=1111

![image-20220210193531263](C:\Users\XL\Desktop\SpringMvc\7.png)

返回响应数据：

- setContentType设置响应的格式和字符集
- 通过PrintWriter流来打印输出

![image-20220210193919273](C:\Users\XL\Desktop\SpringMvc\8.png)





## **3、获取请求响应的常用方式**

请求：（方式一  ？后跟参数）

- 以GET请求为例，默认为GET    **GET请求是浏览器向服务器请求数据时的方式**  

- @RequestMapping中指定路径，也可以指定请求的方式，当指定后只能传入相应的请求方式

- 方法传入参数中可以添加注解@RequestParam（name=,required=,defaultValue)  表示请求中相应参数的名字，是否必须提供，默认值

  也可以不写注解，直接写int current 但要注意和参数名一致。

![image-20220210194731942](C:\Users\XL\Desktop\SpringMvc\9.png)



请求：（方式二    路径参数）

- @PathVariable（“id"） 在参数前添加注解

![image-20220210195340134](C:\Users\XL\Desktop\SpringMvc\10.png)



## **4、POST请求**

 **常用于浏览器向服务器提交数据**

不用GET请求的原因：1、GET请求会在明面上把数据显示 ，不安全    2、GET请求有数据长度限制；

**1、静态页面 表格提交数据：**

![image-20220210195946394](C:\Users\XL\Desktop\SpringMvc\11.png)



**POST请求：**

方法传入参数中可以添加注解@RequestParam（name=,required=,defaultValue)  表示请求中相应参数的名字，是否必须提供，默认值

也可以不写注解，**直接写int current 但要注意和参数名一致**。

![image-20220210200153896](C:\Users\XL\Desktop\SpringMvc\12.png)





## 5、响应

**1、方式一：**

- 默认是html页面所以不需要添加@ResponseBody
- 返回类型为  **ModelAndView**
- 创建一个ModelAndView实例，调用其中的方法添加属性
- 设置模板的路径   不需要写templates文件夹

![image-20220210201056700](C:\Users\XL\Desktop\SpringMvc\13.png)



**2、方式二：**（简洁常用）

- 默认是html页面所以不需要添加@ResponseBody
- 返回类型为 String  是模板的路径    不需要写templates文件夹
- 声明Model  对象  调用其中的方法添加数据

![image-20220210201500624](C:\Users\XL\Desktop\SpringMvc\14.png)



**3、响应JSON数据  返回一个对象**

- 返回的JAVA对象通过json可以转化为js对象
- 类型为自定义对象  或者这里暂时的Map集合

![image-20220210202104085](C:\Users\XL\Desktop\SpringMvc\15.png)



**4、响应JSON数据  返回多个对象**

![image-20220210202253752](C:\Users\XL\Desktop\SpringMvc\16.png)