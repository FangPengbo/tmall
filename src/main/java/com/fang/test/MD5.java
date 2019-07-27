package com.fang.test;


import com.fang.dao.UserMapper;
import com.fang.entity.User;
import com.fang.util.PassWordMD5;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)//使用junit4进行测试
@ContextConfiguration(locations = { "classpath:spring/spring-context-dao.xml","classpath:spring/spring-context-service.xml","classpath:spring/spring-servlet.xml" })//加载配置文件
public class MD5 {

    @Autowired
    private UserMapper userMapper;

    //第一次启动项目先运行这个方法对数据库内的用户密码加密保护
    @Test
    public void test(){
        //获取所有用户进行遍历更新密码
        List<User> users = userMapper.select(null, null, null);
        for(User user:users){
            if(user.getUser_password().length()<16){
                userMapper.updateOne(new User().setUser_id(user.getUser_id()).setUser_password(PassWordMD5.PwdMD5(user.getUser_name(),user.getUser_password())));
            }
        }
    }


}
