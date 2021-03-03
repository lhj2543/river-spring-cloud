package com.river.learn.java.thread.lock.readwriteLock;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * 测试
 * @author 17822
 */
public class Client {

    private static  ShareDate shareDate = new ShareDate();

    public static void main(String[] args) {

        IntStream.rangeClosed(0,10).forEach(i->{
            new Thread(){
                @Override
                public void run() {
                    try {
                        UUID uuid = UUID.randomUUID();
                        char[] chars = uuid.toString().toCharArray();
                        shareDate.wite(chars);
                        Optional.of(this.getName()+" 写数据中...,chars="+ Arrays.toString(chars)).ifPresent(System.out::println);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });

        IntStream.rangeClosed(0,100_000).forEach(i->{
            new Thread(){
                @Override
                public void run() {
                    try {
                        char[] data = shareDate.read();
                        Optional.of(this.getName()+" 读取数据，data="+Arrays.toString(data)).ifPresent(System.out::println);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });

    }
}
