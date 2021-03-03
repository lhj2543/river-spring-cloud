package com.river.learn.java.thread.future;

/**
 * @author 17822
 */
public interface Future<T> {

    public T get() throws InterruptedException;

}
