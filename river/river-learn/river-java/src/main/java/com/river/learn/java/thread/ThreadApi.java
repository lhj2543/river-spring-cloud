package com.river.learn.java.thread;

import java.util.Optional;

/**
 * @author 17822
 */
public class ThreadApi {

    public static void main(String[] args) throws  Exception {

        Object o = new Object();
        o.wait();
        o.notify();

        /**
         * 创建一个线程对象new Thread() 并且start()启动 默认有一个线程名，以Thread-开头，从0开始计数
         * 如果在Thread的时候没有传递Runable或没有复写run()方法，该Thread不会调用任何东西
         */
        Thread t1 = new Thread();
        t1.start();
        String  threadName = t1.getName();
        System.out.println(threadName);

        /**
         *如果构造线程对象时未传入ThreadGroup,Thread会默认获取父线程的ThreadGroup作为该线程的ThreadGroup,此时子线程和父线程将会在同一个ThreadGroup中
         */
        Thread t2= new Thread(){
            @Override
            public void run() {
                System.out.println(this.getThreadGroup());
            }
        };
        t2.start();

        ThreadGroup threadGroup = t2.getThreadGroup();
        System.out.println(Thread.currentThread().getName());
        System.out.println(threadGroup.getName());

        //守护线程：主线程结束后，同时该守护线程子线程也会同时结束
        Thread t3 =  new Thread(){
            @Override
            public void run() {
                Thread innerT = new Thread("innerThread"){
                    @Override
                    public void run() {
                       int k = 0;
                       boolean flag = true;
                       while (flag){
                           k++;
                           if(k>=10){
                               flag =  false;
                           }
                           System.out.println(this.getName()+"runing");
                       }
                    }
                };
                //开启守护线程 ：主线程结束后，同时该守护线程子线程也会同时结束
                innerT.setDaemon(true);
                innerT.start();

                try {
                    System.out.println("thread runing");
                    //Thread.sleep(1_000L);
                    System.out.println("thread runing end");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        t3.start();


        Thread.sleep(1_000L);
        //线程ID
        Optional.of(t3.getId()).ifPresent(System.out::println);
        //线程优先级
        Optional.of(t3.getPriority()).ifPresent(System.out::println);


        /**
         * join() 等待线程
         */
        Thread t4 = new Thread("thread join"){
            @Override
            public void run() {
                try {
                    System.out.println(this.getName() + " runing");
                    Thread.sleep(5_000L);
                    System.out.println(this.getName() + " runing end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t4.start();
        //等待线程
        //t4.join();
        //等待1秒后如果线程没有执行完，就不等待了
        t4.join(1_000);

        Optional.of(t4.getName()+" 线程执行完毕").ifPresent(System.out::println);

        /**
         * interrupt()
         * Thread.interrupted()
         * 中断线程
         */
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        System.out.println(">>" + Thread.currentThread().getName()+" = "+ Thread.currentThread().isInterrupted());
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"thread interrupt ");
        t5.start();
        Thread.sleep(100);
        System.out.println(t5.getName()+" = "+ t5.isInterrupted());
        //中断线程
        t5.interrupt();
        System.out.println(t5.getName()+" = "+ t5.isInterrupted());



    }


}
