package com.river.learn.java.design.builder;

/**
 * 测试
 * @author 17822
 */
public class Test {
    public static void main(String[] args) {

        Conductor cd = new Conductor(new ConcreteBuider());

        Product product = cd.getProduct();

        System.err.println(product.toString());

        cd.setAbstractBuilder(new ConcreteBuider_B());
        System.err.println(cd.getProduct().toString());

    }
}
