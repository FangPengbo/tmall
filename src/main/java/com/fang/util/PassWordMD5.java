package com.fang.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class PassWordMD5 {

    //对密码进行MD5加密
    public static String PwdMD5(String username,String password){
        Object salt= ByteSource.Util.bytes(username);
        int i=1024;
        Object result = new SimpleHash("MD5", password, salt, i);
        return result.toString();
    }

}
