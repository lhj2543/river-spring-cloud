package com.river.learn.java.thread.CyclicBarrier;

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * CycnliBarrier
 *
 * CountDownLatch：一个或者多个线程，等待其他多个线程完成某件事情之后才能执行；
 * CyclicBarrier：多个线程互相等待，直到到达同一个同步点，再继续一起执行。
 * 对于CountDownLatch来说，重点是“一个线程（多个线程）等待”，而其他的N个线程在完成“某件事情”之后，可以终止，也可以等待。
 * 对于CyclicBarrier，重点是多个线程，在任意一个线程没有完成，所有的线程都必须互相等待，然后继续一起执行。
 * CountDownLatch是计数器，线程完成一个记录一个，只不过计数不是递增而是递减，
 * CyclicBarrier更像是一个阀门，需要所有线程都到达，阀门才能打开，然后继续执行
 *
 * reset(); 可重复用性
 *
 * @author 17822
 */
public class CycliBarrierTest {

    public static void main(String[] args) {

        Random random = new Random(System.currentTimeMillis());

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有线程第一部分准备好了，可以同步开始第二部分工作。。。");
                executorService.shutdown();
            }
        });



        IntStream.range(0,5).forEach(i->{
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"运行第一部分。。");
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(5));
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"所有线程第一部分运行完了,同步开始第二部分工作。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
        System.out.println(111);
        //可复用性
        cyclicBarrier.reset();

        IntStream.range(0,5).forEach(i->{
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"运行第一部分。。");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"所有线程第一部分运行完了,同步开始第二部分工作。。");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        });


    }

}
