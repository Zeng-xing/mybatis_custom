package com.zengxing.controller;

import com.zengxing.bean.User;
import com.zengxing.dao.UserMapper;
import com.zengxing.framework.Configuration;
import com.zengxing.framework.SqlSession;
import com.zengxing.util.MybatisSqlSessionFactoryUtil;
import org.junit.Test;

import java.util.List;

/**
 * @AuThor：zengxing
 * @DATE:2020/10/18 17:46  250
 */
public class MybatisDemo {
    @Test
    public void testConfiguration(){
        Configuration configuration = new Configuration();
        System.out.println(configuration);
    }

    @Test
    public void queryAll(){
        SqlSession sqlSession = MybatisSqlSessionFactoryUtil.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.findAll();
        System.out.println(users);
    }
}
