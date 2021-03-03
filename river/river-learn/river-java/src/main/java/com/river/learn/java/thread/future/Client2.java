package com.river.learn.java.thread.future;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author 17822
 */
public class Client2 {

    public static void main(String[] args) {

        syncMethodCallback(new Consumer<Object>() {
            @Override
            public void accept(Object data) {
                Optional.of("回调结果："+data).ifPresent(System.out::println);
            }
        });

        System.out.println("做其他事情.....");
    }

    public static <T> void syncMethodCallback(Consumer<Object> consumer){

        new Thread(){
            @Override
            public void run() {
                System.out.println("异步runing...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //回调
                consumer.accept("哈哈哈");
            }
        }.start();

    }

}
