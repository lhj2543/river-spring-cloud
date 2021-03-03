package com.river.learn.java.thread.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异常锁
 * 当多个线程争抢一个资源时，没有争抢到锁的线程抛出异常
 * @author 17822
 */
public class TryLock {

    private  Thread thisThred;

    final AtomicInteger atomicInteger = new AtomicInteger(0);

    public TryLock() {
    }

    public void lock() throws TryLockException{
        //compareAndSet比较当前值是否为0，如果是，者更新值，否者返回false,不进行更新
        boolean flag = atomicInteger.compareAndSet(0, 1);
        if(!flag){
            throw new TryLockException("未抢到锁！");
        }else {
            thisThred = Thread.currentThread();
        }
    }

    public void  unlock(){
        //判断是否当前线程是否锁
        if(atomicInteger.get()==0 || thisThred != Thread.currentThread()){
            return;
        }
        atomicInteger.compareAndSet(1, 0);
    }

}

class  TryLockException extends  Exception{

    public TryLockException(){
        super();
    }

    public TryLockException(String message){
        super(message);
    }

}
