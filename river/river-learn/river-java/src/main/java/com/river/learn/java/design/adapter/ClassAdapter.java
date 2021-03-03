package com.river.learn.java.design.adapter;

/**
 * 类适配器
 * Adapter类，通过继承 src类，实现 dst 类接口，完成src->dst的适配。
 * @author 17822
 */
public class ClassAdapter extends B  implements A {

    @Override
    public int getNumber2() {
        int number = super.getNumber()/5;
        return number;
    }

}

