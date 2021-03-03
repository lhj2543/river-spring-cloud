package com.river.learn.java.thread.future;

/**
 * Future 设计模式
 * 异步做一些事，做完之后在回调回去
 * @author 17822
 */
public class FutureImp<T> implements Future<T> {

    private volatile boolean isFinish = false;

    private T result;

    @Override
    public T get() throws InterruptedException {
        synchronized (this){
            while (!isFinish){
                this.wait();
            }
        }
        return result;
    }

    public void down(T result){
        synchronized (this){
            this.isFinish = true;
            this.result = result;
            this.notify();
        }
    }

}
