package com.river.learn.java.reflex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * 反射小例子:
 * 通过配置文件，获取任意类对象，及类对象任意所有方法
 * @author 17822
 */
public class ReflexDemo {


    public static void main(String[] args) throws IOException {

        ClassLoader classLoader = ReflexDemo.class.getClassLoader();
        URL resource = classLoader.getResource("./test.properties");
        System.out.println(resource);
        InputStream resourceAsStream = classLoader.getResourceAsStream("test.properties");

        Properties properties = new Properties();
        properties.load(resourceAsStream);

        String className = properties.getProperty("className", "没有值");
        System.out.println(className);

        String methodName = properties.getProperty("methodName", "没有值");

        System.out.println(methodName);


    }


}
