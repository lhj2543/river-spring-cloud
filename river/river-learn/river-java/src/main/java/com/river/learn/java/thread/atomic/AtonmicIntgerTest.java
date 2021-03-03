package com.river.learn.java.thread.atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * AtomicInteger Atomi...
 * 1:在高并发线中保证了共享数据的原子性，可见性，有序性
 * //实现了CAS（轻量级锁）算法 也就是CPU级别的指令，相当于乐观锁，他可以检测到其他线程对共享数据的变化情况
 * CAS（轻量级锁） 会存在ABA问题：ABA就是说一个线程把数据A变为了B，然后又重新变成了A。此时另外一个线程读取的时候，发现A没有变化，就误以为是原来的那个A
 * 解决方案：通过维护一个版本号
 * @author 17822
 */
public class AtonmicIntgerTest {

    private  static TryLock tryLock = new TryLock();

    public static void main(String[] args) {

        final AtomicInteger atomicInteger = new AtomicInteger();

        IntStream.range(0,0).forEach(i->{
            Thread t =new Thread("线程"+i){
                @Override
                public void run() {
                    IntStream.range(0,1000).forEach(j->{
                        //atomicInteger.getAndIncrement();

                        int andSet = atomicInteger.addAndGet(1);
                        System.out.println(andSet);

                        //compareAndSet比较当前值是否为10，如果是，者更新值，否者返回false,不进行更新
                        atomicInteger.compareAndSet(10, 0);
                    });
                }
            };
            t.start();
        });

        IntStream.range(0,80).forEach(i->{
            new Thread(){
                @Override
                public void run() {
                    test();
                }
            }.start();
        });

        System.out.println(atomicInteger.get());

    }

    public static void test(){

        try {
            tryLock.lock();
            System.out.println(Thread.currentThread().getName()+" working...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TryLockException e) {
            System.err.println(Thread.currentThread().getName()+" "+e.getMessage());
        } finally {
            tryLock.unlock();
        }

    }

}
