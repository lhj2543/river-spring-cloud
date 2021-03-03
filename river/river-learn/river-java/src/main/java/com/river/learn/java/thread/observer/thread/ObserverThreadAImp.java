package com.river.learn.java.thread.observer.thread;

import org.apache.commons.codec.binary.StringUtils;

/**
 * 观察者设计模式
 * 线程生命周期监听例子
 * @author 17822
 */
public class ObserverThreadAImp implements ThreadLifeCycleListenter {

    private  final  Object LOCK = new Object();

    public void method1(String name){

        Thread t = new Thread(new ObserverRunbale(this) {
            @Override
            public void run() {
                try {
                    this.notify(new EventDto(Thread.currentThread(),Sate.RUNGING,null));
                    System.out.println(name + "数据运行中。。。");
                    if(StringUtils.equals(name,"t1")){
                        int i =10/0;
                    }
                    this.notify(new EventDto(Thread.currentThread(),Sate.BEVOER,null));
                } catch (Exception e) {
                    this.notify(new EventDto(Thread.currentThread(),Sate.BLOCK,e));
                    e.printStackTrace();
                }
            }
        },name);
        t.start();
    }

    @Override
    public void onEvent(EventDto event) {
        synchronized(LOCK){
            System.out.println(event.toString());
            if(event.getSate()==Sate.BLOCK){
                System.err.println(event.getE().getMessage());
            }
        }
    }

}
