package com.river.learn.java.thread.lock;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写分离锁
 * @author 17822
 */
public class ReentrantReadWriteLockTest {

    public static void main(String[] args) throws InterruptedException {

        List list = new ArrayList();

        //fair 公平性
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);

        //读锁
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        //写锁
        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();

        new Thread(){
            @Override
            public void run() {

                try {
                    readLock.lock();
                    list.add(this.getName());
                    System.out.println(this.getName()+"--list="+list);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    readLock.unlock();
                }

            }
        }.start();

        //TimeUnit.MICROSECONDS.sleep(10);

        new Thread(){
            @Override
            public void run() {

                try {
                    readLock.lock();
                    System.out.println(this.getName()+"--list="+list);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    readLock.unlock();
                }
            }
        }.start();



    }



}
