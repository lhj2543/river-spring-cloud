package com.river.learn.java.thread.CountDownLatch;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * join & countDownLatch
 *
 * join 等待线程执行， 原理：在当前线程中调用另一个线程线程 thread 的 join() 方法时，会调用该 thread 的 wait() 方法，直到这个 thread 执行完毕(JVM在 run() 方法执行完后调用 exit() 方法，而 exit() 方法里调用了 notifyAll() 方法)
 * 会调用 notifyAll() 方法主动唤醒当前线程。
 *
 * countDownLatch 原理：可以理解为一个计数器。在初始化 CountDownLatch 的时候会在类的内部初始化一个int的变量，
 * 每当调用 countDownt() 方法的时候这个变量的值减1，而 await() 方法就是去判断这个变量的值是否为0，是则表示所有的操作都已经完成，否则继续等待
 *
 * join和countDownLatch都能实现让当前线程阻塞等待其他线程执行完毕，
 * join使用起来更简便，不过countDownLatch粒度更细。
 * 也就是说如果当前线程只需要等待其他线程一部分任务执行完毕的情况下就可以用 countDownLatch 来实现了，而 join 则实现不了这种粒度的控制
 * 由于CountDownLatch需要开发人员很明确需要等待的条件，否则容易造成await()方法一直阻塞。
 * @author 17822
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(3);

        IntStream.range(0,5).forEach(i->{
            new Thread("线程"+i){
                @Override
                public void run() {
                    System.out.println(this.getName()+" 开始工作...");
                    if(i<3){
                        countDownLatch.countDown();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(this.getName()+" 工作完了...");
                }
            }.start();
        });

        countDownLatch.await();

        System.err.println("==============3个线程工作完了");

    }

}
