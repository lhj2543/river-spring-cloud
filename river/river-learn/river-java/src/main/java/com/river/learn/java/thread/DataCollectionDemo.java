package com.river.learn.java.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据采集例子
 * 采集1000台服务器数据，每采集一台是一个线程，但是每次最大只有10个线程同步采集
 * 最终1000台服务器数据采集完了后做最终的数据入库操作
 * @author 17822
 */
public class DataCollectionDemo {

    //每批次数据采集最大线程数
    private final int MAX_THREAD = 10;

    //当前数据采集线程数
    private int threadNnumber;

    //锁
    private final Object LOCK = new Object();

    //采集结果
    private List<Object> resutl = new ArrayList<Object>();

    /**
     * 数据采集方法
     */
    public void  dataCollection(int i){

        synchronized (LOCK){

            while (threadNnumber>=MAX_THREAD){
                try {
                    //System.out.println(Thread.currentThread().getName()+" 等待采集中a...");
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            threadNnumber++;
        }

        try {
            System.out.println(Thread.currentThread().getName()+" 进行数据采集中...");
            Thread.sleep(1000*i);
            resutl.add(Thread.currentThread().getName());
            System.out.println(Thread.currentThread().getName()+" 进行数据采集完了");
            synchronized (LOCK){
                //数据采集完了后唤醒wait
                threadNnumber--;
                LOCK.notifyAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public List<Object> getResutl() {
        return resutl;
    }

    public static void main(String[] args) {

        DataCollectionDemo dcd = new DataCollectionDemo();

        new Thread("main thread"){
            @Override
            public void run() {

                List<Thread> list = new ArrayList<Thread>();

                for (int i =0 ;i<100;i++){
                    {
                        int finalI = i;
                        Thread t = new Thread("线程"+ finalI){
                            @Override
                            public void run() {
                                dcd.dataCollection(finalI);
                            }
                        };
                        t.start();
                        list.add(t);
                    }
                }

                for (Thread t:list){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(this.getName()+" 完了,进行数据入库操作。");
                System.out.println(dcd.getResutl());

            }
        }.start();

        System.out.println("方法完了");

    }

}
