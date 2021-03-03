package com.river.learn.java.thread.future;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author 17822
 */
public class Client {
    public static void main(String[] args) {
        
        FutureService futureService = new FutureService();
        FutureTask futureTask = new FutureTaskImp();

        try {
            long startTime = System.currentTimeMillis();
            Future future = futureService.syncMehtod(futureTask, new Consumer<Object>() {
                //回调方法，异步任务完了后会回调该方法
                @Override
                public void accept(Object data) {
                    Optional.of(data).ifPresent(System.out::println);
                }
            });

            System.out.println("做其他事情中.....");

            //Object data = future.get();
            //Optional.of(data).ifPresent(System.out::println);

            long endTime = System.currentTimeMillis();
            System.out.println("任务总消耗时间："+ (endTime-startTime)/1000+"s");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
