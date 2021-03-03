package com.river.learn.java.thread;

import java.util.Collection;

/**
 * 自定义锁
 * @author 17822
 */
public interface Lock {
    /**
     * 加锁
     * @throws InterruptedException
     */
    public void lock() throws InterruptedException;

    /**
     * 加锁
     * @param mills 获取锁的的时间，若超时后会抛出异常
     * @throws InterruptedException
     * @throws RuntimeException
     */
    public void lock(long mills) throws  InterruptedException,RuntimeException;

    /**
     * 解锁
     */
    public void unlock();

    /**
     * 获取所有等待锁的对象集合
     * @return
     */
    public Collection<Thread> getBlockedThreads();


    public static class TimeOutException extends  RuntimeException{
        public TimeOutException(String message){
            super(message);
        }
    }

}