package com.river.learn.java.design.strategy;

/**
 * @author 17822
 */
public abstract class Context {

    protected Strategy strategy;

    protected Strategy2 strategy2;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public Context(Strategy2 strategy2) {
        this.strategy2 = strategy2;
    }

    public Context(Strategy strategy, Strategy2 strategy2) {
        this.strategy = strategy;
        this.strategy2 = strategy2;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy2 getStrategy2() {
        return strategy2;
    }

    public void setStrategy2(Strategy2 strategy2) {
        this.strategy2 = strategy2;
    }
}
