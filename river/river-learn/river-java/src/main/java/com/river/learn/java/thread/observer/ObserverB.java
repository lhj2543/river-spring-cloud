package com.river.learn.java.thread.observer;

/**
 * @author 17822
 */
public class ObserverB extends  Observer {

    protected ObserverB(Subject subject) {
        super(subject);
    }

    @Override
    public void update() {
        System.out.println("ObserverB update "+this.subject.getState());
    }

}
