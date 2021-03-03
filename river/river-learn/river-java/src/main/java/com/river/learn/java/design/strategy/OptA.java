package com.river.learn.java.design.strategy;

/**
 * @author 17822
 */
public class OptA extends Context {

    public OptA(Strategy strategy) {
        super(strategy);
    }

    public void opt1(){
        super.strategy.opt1();
    };

}
