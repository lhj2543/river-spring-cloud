package com.river.learn.java.thread;

import java.util.Optional;

/**
 * @author 17822
 */
public class ThreadClose {

    public static void main(String[] args) {

        ThreadClose1 t1 = new ThreadClose1();
        t1.start();
        System.out.println("aaa");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.shutdown();
        System.out.println("bbb");

        //=================================
        ThreadClose2 t2 = new ThreadClose2();
        t2.start();
        System.out.println("aaa22");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //线程中断
        t2.interrupt();
        System.out.println("bbb22");


    }

    /**
     * 线程关闭方式1
     */
    private static class ThreadClose1 extends Thread {

        private volatile  boolean flag = true;

        @Override
        public void run() {
            while (flag){
                Optional.of(this.getName()+" = "+this.isInterrupted()).ifPresent(System.out::println);
            }
        }

        public  void  shutdown(){
            this.flag = false;
        }

    }

    /**
     * 线程关闭方式2
     */
    private static class ThreadClose2 extends Thread {


        @Override
        public void run() {
            while (true){
                Optional.of(this.getName()+" = "+this.isInterrupted()).ifPresent(System.out::println);
                //方式1:通过判断线程是否终止
                /*if(this.isInterrupted()){
                    break;
                }*/

                //方式2:通过sleep() 捕获异常，终止
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

            System.out.println("do other work.");

        }

    }

}
