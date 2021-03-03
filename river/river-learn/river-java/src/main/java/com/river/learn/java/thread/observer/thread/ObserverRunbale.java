package com.river.learn.java.thread.observer.thread;

/**
 *
 */
public abstract class ObserverRunbale implements Runnable {

    private ThreadLifeCycleListenter listenter;

    public ObserverRunbale(ThreadLifeCycleListenter listenter){
        this.listenter  = listenter;
    }

    public void  notify(EventDto event){
        listenter.onEvent(event);
    }

}
