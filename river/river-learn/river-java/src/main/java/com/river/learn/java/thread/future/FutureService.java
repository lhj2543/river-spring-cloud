package com.river.learn.java.thread.future;

import java.util.function.Consumer;

/**
 * @author 17822
 */
public class FutureService{

    /**
     * 异步方法
     * @param task
     * @param consumer
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public <T> Future<T> syncMehtod(final FutureTask<T> task, Consumer<T> consumer) throws InterruptedException {

        FutureImp<T> future =  new FutureImp<T>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                T result = task.task();
                //任务完了
                future.down(result);
                //回调
                consumer.accept(result);
            }
        }).start();

        return future;

    }


}
