package com.river.learn.java.thread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * volatile关键字  volatile 是 Java 虚拟机提供的最轻量级的同步机制
 * 一个变量被volatile修饰
 *  1:保证了不同线程间的可见性  JMM内存模型的可见性，指的是当主内存区域中的值被某个线程写入更改后，其它线程会马上知晓更改后的值，并重新得到更改后的值。
 *  2：禁止对其进行重排序，也就是保证了有序性
 *  3:不能保证一致性（原子性）：
 *  volatile修饰的变量，对一个变量的写操作先于对变量的读操作
 *
 *  volatile 使用场景
 *  1：状态量标记
 *  volatile boolean flag = ture;
 *  2:屏障前后的一致性
 * @author 17822
 */
public class ThreadVolatile {

    private static volatile int i = 0;


    public static void main(String[] args) throws InterruptedException {

        new Thread("t1"){
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true){
                    System.out.println(this.getName()+"读取数据：="+i);
                }
            }
        }.start();

        Thread.sleep(1000);

        i++;

        new Thread("t2"){
            @Override
            public void run() {
                while (true){
                    i++;
                    try {
                        System.out.println("数据写完：i="+i);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

    @Test
    public  void test1(){

        new Thread(){
            @Override
            public void run() {

                try {
                    System.out.println("开始修改数据");
                    TimeUnit.SECONDS.sleep(2);
                    i++;
                    System.out.println(String.format("修改数据完了,i = %s",i));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();


        while ( i<= 0 ){

        }



    }

}
