package com.river.learn.java.design.builder;

/**
 * 具体得建造者，实现抽象接口，或继承抽象建造者
 */
public class ConcreteBuider extends  AbstractBuilder{

    @Override
    public void step1() {
        System.err.println("A产品创建第1步。。。");
        super.getProduct().setName("A产品");
    }

    @Override
    public void step2() {
        System.err.println("A产品创建第2步。。。");
        super.getProduct().setType("A化妆品");
    }

    @Override
    public void step3() {
        System.err.println("A产品创建第3步。。。");
        super.getProduct().setNotes("A-notes");
    }

}

class ConcreteBuider_B extends  AbstractBuilder{

    @Override
    public void step1() {
        System.err.println("B产品创建第1步。。。");
        super.getProduct().setName("B产品");
    }

    @Override
    public void step2() {
        System.err.println("B产品创建第2步。。。");
        super.getProduct().setType("B化妆品");
    }

    @Override
    public void step3() {
        System.err.println("B产品创建第3步。。。");
        super.getProduct().setNotes("B-notes");
    }

}
