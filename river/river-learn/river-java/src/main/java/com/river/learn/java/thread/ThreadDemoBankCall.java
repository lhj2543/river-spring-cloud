package com.river.learn.java.thread;

/**
 * @author 17822
 */
public class ThreadDemoBankCall implements  Runnable{

    public static void main(String[] args) {

        ThreadDemoBankCall bc = new ThreadDemoBankCall();
        for (int i = 0;i<4;i++){
            Thread t = new Thread(bc,(i+1)+"号柜台");
            t.start();
        }

    }


    private  Integer i = 0;
    private final  Object LOCK = new Object();

    public void  call(){

            while (true){
                //同步代码块，防止共享数据线程安全
                synchronized (LOCK){
                    if(i>=500){
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    System.out.println("请"+i+"号客户到"+ Thread.currentThread().getName()+"办理业务。");
            }
        }
    }

    @Override
    public void run() {
        this.call();
    }
}
