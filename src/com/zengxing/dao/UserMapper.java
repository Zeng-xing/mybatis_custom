package com.zengxing.dao;

import com.zengxing.bean.User;

import java.util.List;
import java.util.Map;

/**
 用户表数据操作接口
 */
public interface UserMapper {
    List<User> findAll();

    List<User> findById(int id);
    

    List<User> findByUser(User user);

    List<User> saveMap(Map map);
}
