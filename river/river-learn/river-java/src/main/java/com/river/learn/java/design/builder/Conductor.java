package com.river.learn.java.design.builder;

/**
 * 指挥者
 * 构建一个使用Builder接口的对象。它主要是用于创建一个 复杂的对象。
 * 它主要有两个作用，
 * 一是：隔离了客户与对象的生产过程，
 * 二是： 负责控制产品对象的生产过程
 */
public class Conductor {

    private AbstractBuilder abstractBuilder;

    public Product getProduct(){

        abstractBuilder.step1();
        abstractBuilder.step2();
        abstractBuilder.step3();

        return  abstractBuilder.getProduct();
    }

    public Conductor(AbstractBuilder abstractBuilder){
        this.abstractBuilder = abstractBuilder;
    }

    public AbstractBuilder getAbstractBuilder() {
        return abstractBuilder;
    }

    public void setAbstractBuilder(AbstractBuilder abstractBuilder) {
        this.abstractBuilder = abstractBuilder;
    }

}
