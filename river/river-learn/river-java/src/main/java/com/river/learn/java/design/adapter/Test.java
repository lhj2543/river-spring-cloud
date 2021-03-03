package com.river.learn.java.design.adapter;

/**
 * @author 17822
 */
public class Test {

    public static void main(String[] args) {

        Logic l =new Logic();
        int number = l.getNumber(new ClassAdapter());
        System.err.println(number);

        int number1 = l.getNumber(new ObjectAdapter(new B()));
        System.err.println(number1);

        l.AbstractAdapterTest();

    }

}
