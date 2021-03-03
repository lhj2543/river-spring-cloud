package com.river.learn.java.thread;

import java.util.*;

/**
 * 自定义锁
 * @author 17822
 */
public class CustomLock implements Lock {

    //isBlcoked = ture 时说明锁已被抢走，其他线程属于等待状态
    private boolean isBlcoked;

    //等待锁的对象集合
    private Collection<Thread> blockedThreads;

    //当前获得锁的线程
    private Thread thisLockThread;

    public CustomLock(){
        this.isBlcoked = false;
        this.blockedThreads = new ArrayList<Thread>();
    }

    @Override
    public synchronized void lock() throws InterruptedException {
        while (isBlcoked){
            if(!blockedThreads.contains(Thread.currentThread())){
                blockedThreads.add(Thread.currentThread());
            }
            //Optional.of(Thread.currentThread().getName()+" 等待释放锁中...").ifPresent(System.out::println);
            this.wait();
        }

        this.thisLockThread = Thread.currentThread();
        blockedThreads.remove(Thread.currentThread());
        isBlcoked = true;
    }

    @Override
    public synchronized void lock(long mills) throws InterruptedException, RuntimeException {
        if(mills<=0){
            this.lock();
        }else{

            long hasRemaining = mills;
            long endTime  = System.currentTimeMillis() + mills;

            while (isBlcoked){

                if(hasRemaining<=0){
                    blockedThreads.remove(Thread.currentThread());
                    throw new RuntimeException(Thread.currentThread().getName()+" 获取锁超时");
                }

                if(!blockedThreads.contains(Thread.currentThread())){
                    blockedThreads.add(Thread.currentThread());
                }
                this.wait(mills);
                hasRemaining = endTime - System.currentTimeMillis();
            }

            this.thisLockThread = Thread.currentThread();
            blockedThreads.remove(Thread.currentThread());
            isBlcoked = true;
        }
    }

    @Override
    public synchronized void unlock() {
        //只有当前线程才能释放
        if(this.thisLockThread == Thread.currentThread()){
            isBlcoked = false;
            this.notifyAll();
            Optional.of(Thread.currentThread().getName()+" 释放了锁").ifPresent(System.out::println);
        }

    }

    @Override
    public Collection<Thread> getBlockedThreads() {
        //防止blockedThreads被修改
        return Collections.unmodifiableCollection(this.blockedThreads);
    }


}
