package com.river.learn.java.design.builder;

/**
 * 抽象建造类
 */
public abstract class AbstractBuilder {

    //得到产品
    protected Product product = new Product();

    public abstract void step1();
    public abstract void step2();
    public abstract void step3();

    public Product getProduct(){
        return product;
    }

}
