package com.river.learn.java.thread.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Semaphore
 * Semaphore 是 synchronized 的加强版，作用是控制线程的并发数量。用于共享资源的互斥使用。
 * @author 17822
 */
public class SemaphoreTest {

    public static void main(String[] args) {
        //初始化许可证数量（统一时刻可以允许多少个线程运行一段程序）
        Semaphore semaphore = new Semaphore(2);
        IntStream.range(0,3).forEach(i->{

            new Thread(){
                @Override
                public void run() {
                    try {
                        //获取一个许可证
                        semaphore.acquire();
                        //semaphore.acquireUninterruptibly();//不可中断
                        System.out.println(this.getName());
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        //释放一个许可证
                        semaphore.release();
                    }
                }
            }.start();

        });

        //获取Semaphore 对象中的当前可用许可数
        int i = semaphore.availablePermits();
        System.out.println();
        //获取并返回立即可用的所有许可（通路）个数，并将可用许可置为0。
        semaphore.drainPermits();
        // getQueueLength() 和 hasQueuedThreads() 都是在判断当前有没有等待许可的线程信息时使用。

        //5、线程公平性
        //上面用的 Semaphore  构造方法是 Semaphore semaphore = new Semaphore(int permits)
        //其实，还有一个构造方法： Semaphore semaphore = new Semaphore(int permits , boolean isFair)
        //isFair 的意思就是，是否公平，获得锁的顺序与线程启动顺序有关，就是公平，先启动的线程，先获得锁。isFair 不能100% 保证公平，只能是大概率公平。
        //isFair 为 true，则表示公平，先启动的线程先获得锁。

        //6、方法 tryAcquire() 、 tryAcquire(int permits)、 tryAcquire(int permits , long timeout , TimeUint unit) 的使用：
        //tryAcquire 方法，是 acquire 的扩展版，tryAcquire 作用是尝试得获取通路，如果未传参数，就是尝试获取一个通路，如果传了参数，就是尝试获取 permits 个 通路 、在指定时间 timeout  内 尝试 获取 permits 个通路。

    }

}
