package com.river.learn.java.thread;

/**
 * 捕获线程异常
 * @author 17822
 */
public class ThreadException {


    public static void main(String[] args) {

        Thread t = new Thread("test"){
            @Override
            public void run() {

                System.out.println(this.getName()+" runing");
                int a = 10/0;
            }
        };

        //捕获线程异常
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t.getName()+" 线程异常：message="+e.getMessage());
                e.printStackTrace();
            }
        });

        t.start();

    }

}
