package com.zengxing.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.zengxing.framework.Configuration;
import com.zengxing.framework.SqlSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
    连接工厂的思想是包装连接池。
 */
public class MybatisSqlSessionFactoryUtil {
    public static DataSource dataSource;

    static {
        try {
            Configuration configuration = new Configuration();
            Properties properties = new Properties();
            properties.setProperty("driver",configuration.getDriver());
            properties.setProperty("url",configuration.getUrl());
            properties.setProperty("username",configuration.getUsername());
            properties.setProperty("password",configuration.getPassword());
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSqlSession(){
        try {
            Connection connection = dataSource.getConnection();
            return new SqlSession(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(SqlSession sqlSession){
        try {
            if(sqlSession!=null)sqlSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
