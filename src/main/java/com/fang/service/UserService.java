package com.fang.service;


import com.fang.entity.User;
import com.fang.util.OrderUtil;
import com.fang.util.PageUtil;

import java.util.List;

public interface UserService {
    boolean add(User user);
    boolean update(User user);

    List<User> getList(User user, OrderUtil orderUtil, PageUtil pageUtil);
    User get(Integer user_id);
    User login(String user_name, String user_password);
    Integer getTotal(User user);
    //通过名字获取user
    User getByName(String username);
}
