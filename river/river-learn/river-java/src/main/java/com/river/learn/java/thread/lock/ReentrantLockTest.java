package com.river.learn.java.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * ReentrantLock 重入锁（所谓重入锁，指的是以线程为单位，当一个线程获取对象锁之后，这个线程可以再次获取本对象上的锁，而其他的线程是不可以的）
 *
 * @author 17822
 */
public class ReentrantLockTest {


    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();


        IntStream.range(0,10).forEach(i->{
            new Thread(){
                @Override
                public void run() {

                    //异常锁(没有竞争到锁的情况下，不会进入blocked状态)
                    boolean b = lock.tryLock();

                    try {
                        //等待多久没有获取到锁后就不等待,等待过程中可以被中断
                        boolean b1 = lock.tryLock(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!b){
                        System.out.println(this.getName()+"没有竞争到锁");
                    }else {

                        try {
                            //加锁(可中断锁)
                            //lock.lock();
                            //不可中断锁
                            //lock.lockInterruptibly();

                            System.out.println(this.getName()+"运行中。。。");
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            //释放锁
                            lock.unlock();
                        }
                    }

                }
            }.start();
        });

    }



}
