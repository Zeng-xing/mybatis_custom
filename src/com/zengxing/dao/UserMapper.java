package com.zengxing.dao;

import com.zengxing.bean.User;

import java.util.List;

/**
 用户表数据操作接口
 */
public interface UserMapper {
    List<User> findAll();

}
