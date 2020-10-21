package com.zengxing.controller;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.zengxing.bean.User;
import com.zengxing.dao.UserMapper;
import com.zengxing.framework.Configuration;
import com.zengxing.framework.SqlSession;
import com.zengxing.util.MybatisSqlSessionFactoryUtil;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @AuThor：zengxing
 * @DATE:2020/10/18 17:46  250
 * 测试类
 */
public class MybatisDemo {
    /**
     * 测试配置文件是否加载成功
     */
    @Test
    public void testConfiguration(){
        Configuration configuration = new Configuration();
        System.out.println(configuration);
    }

    /**
     * 测试是否可以实现查询操作
     */
    @Test
    public void queryAll(){
        SqlSession sqlSession = MybatisSqlSessionFactoryUtil.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.findAll();
        System.out.println(users);
    }
    @Test
    public void queryOne(){
        SqlSession sqlSession = MybatisSqlSessionFactoryUtil.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.findById(1);
        System.out.println(users);
    }
    @Test
    public void query(){
        SqlSession sqlSession = MybatisSqlSessionFactoryUtil.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setLoginName("swk");
        user.setUserName("齐天大圣");
        user.setPassWord("tangtang");
        List<User> users = mapper.save(user);
        System.out.println(users);
    }
    @Test
    public void test(){
        Pattern p= Pattern.compile("[{](?<paraName>.*?)}");
        String sql = "select * from tb_user where loginName = #{loginName} and passWord = #{passWord}";
        Matcher m=p.matcher(sql);
        List<String> paraNames = new ArrayList<>();
        while (m.find()) {
            paraNames.add(m.group("paraName"));
        }

    }
    @Test
    public void test2(){
        int name = 12;
        Object o = name;
        if(o instanceof Number){
            System.out.println("Number");
        }
        if(o instanceof Character){
            System.out.println("Character");
        }
        if(o instanceof Boolean){
            System.out.println("Boolean");
        }
        if(o instanceof String){
            System.out.println("String");
        }
    }
}
