package com.river.learn.java.thread.lock;

import java.util.concurrent.TimeUnit;

/**
 * 死锁
 * 死锁是指两个或多个以上的进程在执行过程中，因争夺资源而造成一种互相等待的现象，若无外力干涉那他们都将无法推进下去。
 *
 * @author 17822
 */
public class DeadLock {

    public static void main(String[] args) {

         Object lockA = new Object();
         Object lockB = new Object();

        new Thread(()->{
            DeadLock.testA(lockA,lockB);
        },"线程A").start();

        new Thread(()->{
            DeadLock.testA(lockB,lockA);
        },"线程B").start();

    }



    public static void testA(Object lockA,Object lockB) {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName() + " ===== AAAAAAAAAAAA");

            try {
                TimeUnit.MICROSECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lockB){
                System.out.println(Thread.currentThread().getName() + " ===== BBBBBBBBBBBBB");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
