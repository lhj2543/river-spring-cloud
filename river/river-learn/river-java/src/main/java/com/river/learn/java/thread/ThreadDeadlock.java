package com.river.learn.java.thread;

/**
 * 线程死锁
 * @author 17822
 */
public class ThreadDeadlock {

    public static void main(String[] args) {



        A  a = new A(new B());
        B b = new B();
        b.setA(a);

        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                while (true){
                    a.a1();
                }
            }
        };


        Thread t2 = new Thread("t2"){
            @Override
            public void run() {
                while (true){
                    b.b2();
                }
            }
        };

        t1.start();
        t2.start();

    }

}

class A{
    private final Object LOCK = new Object();

    private B b;

    public A(B b) {
        this.b = b;
    }

    public void a1(){
        synchronized (LOCK){
            System.out.println("进入a1方法");
            b.b2();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void a2(){
        synchronized (LOCK){
            System.out.println("进入a2方法");
        }
    }
}

class B{
    private final Object LOCK = new Object();

    private A a;

    public void b1(){
        synchronized (LOCK){
            System.out.println("进入b1方法");
        }
    }
    public void b2(){
        synchronized (LOCK){
            System.out.println("进入b2方法");
            a.a2();
        }
    }

    public void setA(A a) {
        this.a = a;
    }
}