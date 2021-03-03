package com.river.learn.java.thread;

/**
 * 实现线程方式：
 * 1：继承Thread,重写run 方法
 * 2:实现Runnable 接口
 * 3:使用Callable和Future
 * @author 17822
 */

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 创建线程的三种方式对比
 * 实现Runnable和Callable接口的方法基本相同，只不过Callable接口定义的方法可以有返回值，而且可以声明抛出异常而已。
 * 因此采用实现Runnable和Callable接口方式创建多线程——
 * 优势：
 * 1.线程类只是实现了接口类，还可以继承其他类
 * 2.在这种方式下，多个线程可以共享同一个target对象，所以非常适合多个相同线程类处理同一份资源的情况。
 * 劣势：
 * 编程稍稍复杂，如果要访问当前线程必须使用Thread.currentThread()方法
 * 而继承Thread方式则与之相反，因为已经继承了Thread类，不能再继承其他类。编写简单，如果要访问当前线程，直接使用this即可获得当前线程。
 * 故一般建议采用实现Runnable、Callable接口的方式实现多线程。
 */
public class ThreadDemo1 extends Thread {

    public static void main(String[] args) {
        for(int i =0;i<100;i++){
            ThreadDemo1 d1 = new ThreadDemo1();
            d1.i = i;
            d1.start();
        }

        ThreadDemo2 d2 = new ThreadDemo2();
        Thread t = new Thread(d2);
        t.start();

        ThreadDemo3 d3 = new ThreadDemo3();
        FutureTask<Object> ft = new FutureTask<>(d3);
        Thread t3 = new Thread(ft,"Callable Thread");
        t3.start();

        //匿名内部类
        new Thread("匿名内部类 thread"){
            @Override
            public void run() {
               System.out.println("进入匿名内部类 thread");
            }
        }.start();

    }

    protected  int i;

    @Override
    public  void  run(){
        System.out.println(i+"线程...");
    }
}

/**
 * 2:实现Runnable 接口
 */
class  ThreadDemo2 implements  Runnable{

    @Override
    public void run() {
        System.out.println("线程2");
    }

}

/**
 * 3:使用Callable和Future
 */
class  ThreadDemo3 implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        String result =  "Thread Callable ";
        System.out.println("进入Thread Callable...");
        return result;
    }
}

