package com.river.learn.java.thread;

/**
 * synchronized 重入锁（所谓重入锁，指的是以线程为单位，当一个线程获取对象锁之后，这个线程可以再次获取本对象上的锁，而其他的线程是不可以的） 不可被打断
 * 在并发编程中存在线程安全问题，主要原因有：1.存在共享数据 2.多线程共同操作共享数据。关键字synchronized可以保证在同一时刻，
 * 只有一个线程可以执行某个方法或某个代码块，同时synchronized可以保证一个线程的变化可见（可见性），即可以代替volatile。
 *  Synchronized是Java中解决并发问题的一种最常用最简单的方法 ，他可以确保线程互斥的访问同步代码
 *
 * 普通方法（实例方法），锁是当前实例对象 ，进入同步代码前要获得当前实例的锁
 * 静态方法，锁是当前类的class对象 ，进入同步代码前要获得当前类对象的锁
 * 方法块，锁是括号里面的对象，对给定对象加锁，进入同步代码库前要获得给定对象的锁
 * @author 17822
 */
public class ThreadSynchronized{

    /**
     * 静态代码块同步锁，锁是当前类的class对象
     * 即使当类中静态方式没有加synchronized 锁，线程同步时也会去竞争锁
     */
    static {
        synchronized (ThreadSynchronized.class){
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void method4(){
        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void method5(){
        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //共享数据
    private static  int i = 0;

    private Object LOCK = new Object();

    //普通同步方法（实例方法），锁是当前实例对象 ，进入同步代码前要获得当前实例的锁
    public synchronized  void  doAdd(){
        System.out.println(Thread.currentThread().getName()+" 抢到锁");
        i++;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //普通同步方法（实例方法），锁是当前实例对象 ，进入同步代码前要获得当前实例的锁
    public synchronized  void  doAdd2(){
        System.out.println(Thread.currentThread().getName()+" 抢到锁");
        i++;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为什么要同步代码块呢？在某些情况下，我们编写的方法体可能比较大，同时存在一些比较耗时的操作，而需要同步的代码又只有一小部分，
     * 如果直接对整个方法进行同步操作，可能会得不偿失，此时我们可以使用同步代码块的方式对需要同步的代码进行包裹，
     * 这样就无需对整个方法进行同步操作了。
     */
    public void method2(){

        synchronized (LOCK){
            System.out.println("同步代码块");
            i++;
        }
    }

    //因为静态方法是依附于类而不是对象的，当synchronized修饰静态方法时，锁是class对象
    public synchronized static void method3 (){
        System.out.println("静态同步方法");
        i++;
    }

    public   void  met2(){
        System.out.println("runing met2 ");
    }


    public static void main(String[] args) {

        ThreadSynchronized ts =  new ThreadSynchronized();

        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                ts.doAdd();
            }
        };

        Thread t2 = new Thread("t2"){
            @Override
            public void run() {
                ts.doAdd2();
            }
        };

        Thread t3 = new Thread("t3"){
            @Override
            public void run() {
                ts.method2();
            }
        };
        Thread t4 = new Thread("t4"){
            @Override
            public void run() {
                ThreadSynchronized.method4();
            }
        };
        Thread t5 = new Thread("t5"){
            @Override
            public void run() {
                ThreadSynchronized.method5();
            }
        };

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

    }
}
