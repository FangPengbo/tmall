package com.fang.realm;

import com.fang.entity.User;
import com.fang.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * Shiro认证
 */
public class LoginRealm extends AuthenticatingRealm {

    @Resource(name = "userService")
    private UserService userService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //1.把AuthenticationToken转换成UsernamePasswordToken
        UsernamePasswordToken UPtoken= (UsernamePasswordToken) token;
        //2.从UsernamePasswordToken中获取username
        String username = UPtoken.getUsername();
        //3.调用数据库的方法,从数据库中查询username对应用户记录
        User user=userService.getByName(username);
        //4若用户不存在则抛出UnknownAccountException
        if(user==null){
            throw new UnknownAccountException("用户不存在");
        }
        //5.根据用户信息的情况,决定是否抛出其他异常
        //6返回AuthenticationInfo对象.
        //6.1用户名
        //6.2密码
        //6.3盐值
        ByteSource salt=ByteSource.Util.bytes(user.getUser_name());
        //6.4realmname
        SimpleAuthenticationInfo authenticationInfo;//=new SimpleAuthenticationInfo(user.getUsername(),user.getPassword(),getName());
        //在这里进行密码比对
        authenticationInfo=new SimpleAuthenticationInfo(user.getUser_name(),user.getUser_password(),salt,getName());
        return authenticationInfo;
    }
}
