package com.river.learn.java.thread;

import java.util.Optional;

/**
 * 线程强制停止
 * 实现思路：通过守护线程进行停止，当执行线程生命周期结束时，守护线程生命周期也会同时结束
 * @author 17822
 */
public class ThreadForceShutdownService {

    //执行线程
    private Thread executeThread;

    //守护线程执行完了标识
    private boolean finished = false;

    public void  execute(Runnable task){

        executeThread =  new Thread(){
            @Override
            public void run() {
                //创建守护线程
                Thread runner = new Thread(task);
                runner.setDaemon(true);
                runner.start();

                //必须等待守护线程执行完了
                try {
                    runner.join();
                    finished = true;
                } catch (InterruptedException e) {
                    System.err.println(runner.getName()+"线程被终止!");
                }

            }
        };

        executeThread.start();

    }

    /**
     * 强制停止方法
     * @param time 多少时间后线程还没有结束时，进行强制停止
     */
    public void shutdown(long time){
        long startTime = System.currentTimeMillis();
        //线程一直没有结束时，进行超时判断，
        while (!finished){
            //如果超时,停止执行线程
            if(System.currentTimeMillis()-startTime>=time){
                Optional.of("线程任务超时，线程终止：timeOut="+time/1000+"s").ifPresent(System.out::println);
                executeThread.interrupt();
                break;
            }

            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Optional.of("执行线程被中断！").ifPresent(System.out::println);
                break;
            }

        }

        finished = false;

    }

    public static void main(String[] args) {

        ThreadForceShutdownService threadForceShutdownService = new ThreadForceShutdownService();
        threadForceShutdownService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //假设该线程要执行10秒
                    System.out.println(Thread.currentThread().getName()+" 线程执行中...");
                    Thread.sleep(10*1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //5秒超时，强制停止线程
        threadForceShutdownService.shutdown(5*1000);

    }

}
