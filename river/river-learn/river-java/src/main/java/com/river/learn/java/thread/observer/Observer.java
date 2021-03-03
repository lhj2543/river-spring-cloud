package com.river.learn.java.thread.observer;

/**
 * 观察者
 * @author 17822
 */
public abstract class Observer {

    protected Subject subject;

    protected Observer(Subject subject){
        this.subject  = subject;
        this.subject.addObserver(this);
    }

    public abstract void  update();

}
