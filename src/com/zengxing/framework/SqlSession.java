package com.zengxing.framework;

import com.alibaba.druid.pool.DruidDataSource;
import com.zengxing.dao.UserMapper;

import javax.sql.DataSource;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @AuThor：zengxing
 * @DATE:2020/10/18 17:27  250
 */
public class SqlSession implements AutoCloseable{
    private Connection connection;


    public SqlSession() {

    }

    public SqlSession(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public  <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1.从配置中获取sql语句
                Configuration configuration = new Configuration();
                List<Mapper> mappers = configuration.getMappers();
                Mapper currentMapper = null;
                for (Mapper mapper : mappers) {
                    if (clazz.getName().equals(mapper.getNamespace()) && method.getName().equals(mapper.getId())) {
                        currentMapper = mapper;
                        break;
                    }
                }
                //2.执行sql语句
                String sql = currentMapper.getSql();
                PreparedStatement pstm = connection.prepareStatement(sql);
                switch (currentMapper.getTag()){
                    case "select":
                        ResultSet rs = pstm.executeQuery();
                        Class dataClass = Class.forName(currentMapper.getResultType());
                        List<Object> datas = new ArrayList<>();
                        while (rs.next()){
                            Field[] fields = dataClass.getDeclaredFields();
                            Object data = dataClass.getDeclaredConstructor().newInstance();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                field.set(data,rs.getObject(field.getName()));
                            }
                            datas.add(data);
                        }
                        return datas;
                    case "delete":
                        break;
                    case "update":
                        break;
                    case "insert":
                        break;
                    default:
                        break;
                }
                return null;
            }
        });
    }
}
