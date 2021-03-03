package com.river.learn.java.base;

import lombok.Data;

/**
 *  枚举类的使用：
 *  当需要定义一组常量时，建议使用枚举
 *  枚举类只有一个对象，则可以作为单例模式的实现方式
 *
 *  enum 默认继承 java.lang.Enum
 *  多个对象用逗号分隔
 *
 *  enum 常用方法
 *  values();
 *  valueOF(String str);
 *  toString();
 * @author river
 */
public class EnumDemo {

    public static void main(String[] args) {

        System.out.println(FlowStatus.STOP.getDesc());

        FlowStatus[] values = FlowStatus.values();

        FlowStatus.valueOf("STOP");

    }

}

enum FlowStatus{
    /**
     *
     */
    STOP("停止"),
    /**
     *
     */
    RUN("运行"),
    /**
     *
     */
    ERROR("异常");

    /**
     * 描述
     */
    private final  String desc;

    FlowStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

