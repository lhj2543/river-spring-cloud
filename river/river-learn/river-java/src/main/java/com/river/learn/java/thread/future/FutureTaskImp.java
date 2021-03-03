package com.river.learn.java.thread.future;

/**
 * 任务实现类
 * @author 17822
 */
public class FutureTaskImp implements FutureTask<Object>{

    @Override
    public Object task() {
        try {
            Thread.sleep(2000);
            System.out.println("做异步任务中...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "异步任务完了";
    }

}
