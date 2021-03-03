package com.river.learn.java.design.adapter;

/**
 * @author 17822
 */
public class Logic {

    public int getNumber(A a){
        return  a.getNumber2();
    }

    public void AbstractAdapterTest(){

        //匿名内部类
        AbstractAdapter abstractAdapter = new AbstractAdapter() {
            @Override
            public void  method1(){
                System.err.println("重写方法1");
            }
        };

        abstractAdapter.method1();

    }

}
