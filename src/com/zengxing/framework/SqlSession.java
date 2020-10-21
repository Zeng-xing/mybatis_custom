package com.zengxing.framework;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                PreparedStatement pstm = connection.prepareStatement(sql.replaceAll("#[{]\\w+[}]", "?"));
                //注入参数
                if (args != null && args.length != 0) {
                    //只有一个参数
                    if(args.length == 1){
                        if(checkClass(args[0])){
                            pstm.setObject(1,args[0]);
                        }else if(args[0] instanceof Map){
                            Map map = (Map) args[0];
                            Map<Integer,String> paraNames = getParaNames(sql);
                            Set<Map.Entry<Integer, String>> entries = paraNames.entrySet();
                            for (Map.Entry<Integer, String> entry : entries) {
                                pstm.setObject(entry.getKey(),map.get(entry.getValue()));
                            }
                        }else {
                            //参数为自定义bean对象
                            Class<?> paraClass = args[0].getClass();
                            Map<Integer,String> paraNames = getParaNames(sql);
                            Set<Map.Entry<Integer, String>> entries = paraNames.entrySet();
                            for (Map.Entry<Integer, String> entry : entries) {
                                Method getMethod = paraClass.getDeclaredMethod("get" + toUpperCase(entry.getValue()));
                                if(getMethod != null){
                                    Object value = getMethod.invoke(args[0]);
                                    pstm.setObject(entry.getKey(),value);
                                }
                            }
                        }
                    }
                }
                switch (currentMapper.getTag()){
                    case "select":
                        return execute(pstm,currentMapper.getResultType());
                    case "delete":
                        return execute(pstm);
                    case "update":
                        return execute(pstm);
                    case "insert":
                        return execute(pstm);
                    default:
                        return execute(pstm);
                }
            }
        });
    }
    public Object execute(PreparedStatement pstm,String resultType) throws Exception {
        ResultSet rs = pstm.executeQuery();
        Class dataClass = Class.forName(resultType);
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
    }
    public int execute(PreparedStatement pstm) throws Exception {
        int count = pstm.executeUpdate();
        return count;
    }

    /**
     * 解析sql中参数的参数名#{userName} -> userName
     * @param sql
     * @return
     */
    public Map<Integer,String> getParaNames(String sql){
        Pattern p= Pattern.compile("[{](?<paraName>.*?)}");
        Matcher m=p.matcher(sql);
        Map<Integer,String> paraNames = new HashMap<>();
        int count = 1;
        while (m.find()) {
            paraNames.put(count++, m.group("paraName"));
        }
        return paraNames;
    }

    public Boolean checkClass(Object o){
        Boolean flag = false;
        if(o instanceof Number||o instanceof Character||o instanceof Boolean||o instanceof String){
            flag = true;
        }
        return flag;
    }

    /**
     * 将字符串的首字母转大写
     * @param str 需要转换的字符串
     * @return
     */
    private static String toUpperCase(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs=str.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }
}
