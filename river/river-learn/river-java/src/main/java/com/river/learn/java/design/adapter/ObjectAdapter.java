package com.river.learn.java.design.adapter;

/**
 * 对象适配器
 * @author 17822
 */
public class ObjectAdapter implements A {

    private  B b;

    @Override
    public int getNumber2() {
        if(b== null){
            return 0;
        }
        return b.getNumber()/2;
    }

    public ObjectAdapter(B b){
        this.b = b;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
