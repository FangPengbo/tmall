package com.fang.factory;

import java.util.LinkedHashMap;

/**
 * 工厂类用来创建拦截请求的数据配置
 */
public class FilterChainDefinitionMapBullder {
    public LinkedHashMap<String,String> builderfilterChainDefinitionMap(){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("/**","anon");

        return map;
    }
}
