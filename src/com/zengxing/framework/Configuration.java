package com.zengxing.framework;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @AuThor：zengxing
 * @DATE:2020/10/18 16:39  250
 * Configuration类用于封装mybatis配置信息（核心配置文件和映射文件）
 */
public class Configuration {
    private String driver;
    private String url;
    private String username;
    private String password;
    private List<Mapper> mappers = new ArrayList<>();

    public Configuration() {
        try {
            loadConfigXMLDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Configuration对象时加载配置文件信息
     */
    private void loadConfigXMLDatas() throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(Configuration.class.getResourceAsStream("/mybatis-config.xml"));
        Element root = document.getRootElement();
        Element environments = root.element("environments");
        Element environment = environments.element("environment");
        Element dataSource = environment.element("dataSource");
        List<Element> propertyEle = dataSource.elements();
        for (Element property : propertyEle) {
            String name = property.attributeValue("name");
            switch(name){
                case "driver":
                    this.driver = property.attributeValue("value");
                    break;
                case "url":
                    this.url = property.attributeValue("value");
                    break;
                case "username":
                    this.username = property.attributeValue("value");
                    break;
                case "password":
                    this.password = property.attributeValue("value");
                    break;
            }
        }
        Element mappers = root.element("mappers");
        List<Element> mapperEle = mappers.elements();
        for (Element mapper : mapperEle) {
            String resource = mapper.attributeValue("resource");
            loadMapperXMLDatas(resource);
        }

    }

    private void loadMapperXMLDatas(String path) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(Configuration.class.getResourceAsStream("/"+path));
        Element root = document.getRootElement();
        String namespace = root.attributeValue("namespace");
        List<Element> elements = root.elements();
        for (Element element : elements) {
            Mapper mapper = new Mapper();
            mapper.setNamespace(namespace);
            mapper.setId(element.attributeValue("id"));
            mapper.setResultType(element.attributeValue("resultType"));
            mapper.setTag(element.getName());
            mapper.setSql(element.getTextTrim());
            mappers.add(mapper);
        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Mapper> getMappers() {
        return mappers;
    }

    public void setMappers(List<Mapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mappers=" + mappers +
                '}'+"\n";
    }
}
