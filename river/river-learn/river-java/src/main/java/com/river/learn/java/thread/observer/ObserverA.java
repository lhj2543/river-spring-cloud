package com.river.learn.java.thread.observer;

/**
 * @author 17822
 */
public class ObserverA extends  Observer {

    protected ObserverA(Subject subject) {
        super(subject);
    }

    @Override
    public void update() {
        System.out.println("ObserverA update "+this.subject.getState());
    }

}
