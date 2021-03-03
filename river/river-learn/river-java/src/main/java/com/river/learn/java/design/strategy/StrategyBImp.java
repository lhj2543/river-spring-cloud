package com.river.learn.java.design.strategy;

/**
 * @author 17822
 */
public class StrategyBImp implements Strategy {

    @Override
    public void opt1() {
        System.err.println("StrategyBImp opt1 ");
    }

    @Override
    public void opt2() {
        System.err.println("StrategyBImp opt2 ");
    }

}
