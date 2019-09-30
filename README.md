

### 介绍
一个基于SSM框架的综合性B2C电商平台，需求设计主要参考天猫商城的购物流程：用户从注册开始，到完成登录，浏览商品，加入购物车，进行下单，确认收货，评价等一系列操作。

所有页面均兼容IE10及以上现代浏览器。

### 开发/部署方式
1. 项目使用IntelliJ IDEA开发，请使用IntelliJ IDEA的版本控制检出功能，输入“<https://github.com/FangPengbo/tmall.git>”拉取项目即可。
2. 项目数据库为MySQL 5.7版本，请在附件上下载SQL文件并导入到数据库中。
3. 使用IDEA打开项目后，在maven面板刷新项目，下载依赖包。
4. 在IDEA中配置tomcat服务器，请确认idea中服务配置的地址如下图，配置完毕后即可启动服务
![tomcat配置](https://raw.githubusercontent.com/FangPengbo/tmall/master/About/AboutImage/tomcat.png)

### Tomcat直接部署方式
+ 链接: <https://pan.baidu.com/s/12Ef4bKLo9BkAbV9fPu0zFw> 提取码: szfv

1. 在上述地址中下载项目war包，并放入tomcat8.0及以上版本的webapps文件夹中。
2. 项目数据库为MySQL 5.7版本，请在附件上下载SQL文件并导入到数据库中。
3. 使用winrar等工具打开war包，将WEB-INF/classes中的jdbc.properties修改为你的数据库信息。
4. 启动项目，使用浏览器打开下列地址。

### 项目运行地址
+ 前台地址：<http://localhost:端口/tmall>


### 注意事项：
1. 该项目同时兼容eclipse，但如有自行扩展代码的意愿，建议使用IDEA。
2. 该项目的是让编程初学者和应届毕业生可以参考一下用较少的代码实现一个完整MVC模式，SSM框架体系的电商项目，相关领域大神们可以给我们建议，让我们做得更好。<br>
3. 原项目更换密码加密之后第一次运行项目请执行以下com.fang.test.MD5类中把原数据库明文密码换成加密后密码 否则无法登陆
![MD5加密](https://raw.githubusercontent.com/FangPengbo/tmall/master/About/AboutImage/MD5%E5%8A%A0%E5%AF%86.png)

### 项目界面
+ ##### 前台界面(部分)---
![登陆界面](https://gitee.com/uploads/images/2018/0526/223030_17b28619_1616166.png "2018-05-26_221715.png")
![首页](https://gitee.com/uploads/images/2018/0526/223018_14e999f1_1616166.png "2018-05-26_221703.png")
![首页悬浮框](https://raw.githubusercontent.com/FangPengbo/tmall/master/About/AboutImage/%E6%82%AC%E6%B5%AE%E6%90%9C%E7%B4%A2%E6%A1%86%E5%B7%A6%E4%BE%A7%E5%AF%BC%E8%88%AA%E6%A0%8F.png)
![产品详情](https://raw.githubusercontent.com/FangPengbo/tmall/master/About/AboutImage/%E5%95%86%E5%93%81%E8%AF%A6%E6%83%85%E9%A1%B5.png)
![下单界面](https://gitee.com/uploads/images/2018/0526/223100_ef6e9612_1616166.png "2018-05-26_221837.png")
![收藏夹](https://raw.githubusercontent.com/FangPengbo/tmall/master/About/AboutImage/%E6%94%B6%E8%97%8F%E9%A1%B5.png)
![订单列表](https://gitee.com/uploads/images/2018/0526/223117_dfd64b43_1616166.png "2018-05-26_221901.png")
![确认收货](https://gitee.com/uploads/images/2018/0526/223220_71e2ee3d_1616166.png "2018-05-26_221911.png")
![产品列表](https://gitee.com/uploads/images/2018/0526/223233_18e131a5_1616166.png "2018-05-26_222006.png")
![购物车](https://gitee.com/uploads/images/2018/0526/223245_3f80d8f4_1616166.png "2018-05-26_223157.png")
