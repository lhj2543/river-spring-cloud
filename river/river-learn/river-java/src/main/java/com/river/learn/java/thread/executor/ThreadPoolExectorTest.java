package com.river.learn.java.thread.executor;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * ThreadPoolExecutor 线程池
 *
 * @author 17822
 */
public class ThreadPoolExectorTest {

    public static void main(String[] args) throws InterruptedException {
        /**
         * ThreadPoolExecutor 线程池 构造函数参数说明:
         * @param corePoolSize
         * 核心线程池大小。当线程池中的线程数目达到corePoolSize后，就会把到达的队列放到缓存队列中
         * 这个参数是否生效取决于allowCoreThreadTimeOut变量的值，该变量默认是false，即对于核心线程没有超时限制，所以这种情况下，corePoolSize参数是起效的。
         * 如果allowCoreThreadTimeOut为true，那么核心线程允许超时，并且超时时间就是keepAliveTime参数和unit共同决定的值，这种情况下，如果线程池长时间空闲的话最终存活的线程会变为0，也即corePoolSize参数失效

         * @param maximumPoolSize 线程池能够容纳同时执行的最大线程数，此值必须大于等于1,相当有扩容后的线程数，这个线程池能容纳的最多线程数
         * 当workQueue 超出 corePoolSize 就会启用maximumPoolSize
         * 线程池中最大的存活线程数。这个参数比较好理解，对于超出corePoolSize部分的线程，无论allowCoreThreadTimeOut变量的值是true还是false，都会超时，超时时间由keepAliveTime和unit两个参数算出

         * @param keepAliveTime 多余的空闲线程存活时间
         * 超时时间 当线程数大于核心数量(corePoolSize)，这是多余空闲线程的最长时间将在终止前等待新任务

         * @param unit 超时时间的单位，秒，毫秒，微秒，纳秒等，与keepAliveTime参数共同决定超时时间

         * @param workQueue 任务队列，被提交的但未被执行的任务
         * 当调用execute方法时，如果线程池中没有空闲的可用线程，那么就会把这个Runnable对象放到该队列中。这个参数必须是一个实现BlockingQueue接口的阻塞队列，因为要保证线程安全。
         * 有一个要注意的点是，只有在调用execute方法是，才会向这个队列中添加任务，那么对于submit方法呢，难道submit方法提交任务时如果没有可用的线程就直接扔掉吗？当然不是，
         * 看一下AbstractExecutorService类中submit方法实现，其实submit方法只是把传进来的Runnable对象或Callable对象包装成一个新的Runnable对象，然后调用execute方法，
         * 并将包装后的FutureTask对象作为一个Future引用返回给调用者。Future的阻塞特性实际是在FutureTask中实现的

         * @param threadFactory 用什么方式创建线程 表示生成线程池中工作线程的线程工厂，用于创建线程池 一般用默认即可
         * 线程工厂类。用于在需要的时候生成新的线程。默认实现是Executors.defaultThreadFactory()，即new 一个Thread对象，并设置线程名称，daemon等属性。

         * @param handler 线程池对拒绝任务的处理策略
         * 当线程数量大于workQueue+maximumPoolSize 就会被拒绝策略接管
         * 这个参数的作用是当提交任务时既没有空闲线程，任务队列也满了，这时候就会调用handler的rejectedExecution方法。默认的实现是抛出一个RejectedExecutionException异常。
         */

        BlockingQueue<Runnable> ArrayBlockingQueue = new ArrayBlockingQueue<Runnable>(2);

        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                //设置守护线程,当线程任务超时时,可以中断所有线程任务
                t.setDaemon(true);
                return t;
            }
        };

        //拒绝策略:
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,2,30, TimeUnit.SECONDS,ArrayBlockingQueue,threadFactory,abortPolicy);

        myTask myTask = new myTask(5,"任务1");
        threadPoolExecutor.execute(myTask);
        myTask myTask2 = new myTask(2,"任务2");
        threadPoolExecutor.execute(myTask2);
        myTask myTask3 = new myTask(2,"任务3");
        threadPoolExecutor.execute(myTask3);
        myTask myTask4 = new myTask(2,"任务4");
        threadPoolExecutor.execute(myTask4);

        //非阻塞(空闲线程会停止,非空闲线程标记为停止,等到线程任务执行完了后停止)
        //threadPoolExecutor.shutdown();

        List<Runnable> runnables =null;
        try{
            //等待运行中的线程运行玩,然后停止线程,返回队列中等待的线程,队列中等待的线程会结束掉
            runnables = threadPoolExecutor.shutdownNow();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("队列中等待的线程数量BlockingQueue="+runnables.size()+"被停止");

        //等待所有线程任务结束
        //threadPoolExecutor.awaitTermination(1,TimeUnit.HOURS);

        
        System.out.println("===============结束====================");

        TimeUnit.MICROSECONDS.sleep(10);

        int activeCount_a=-1;
        int size_a=-1;

        while (true){
            //获取线程池活跃的数量
            int activeCount = threadPoolExecutor.getActiveCount();
            //获取等待的线程队列
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            int size = queue.size();
            if(activeCount_a!=activeCount || size!=size_a){
                System.out.println("当前活跃线程数量activeCount="+activeCount+";当前等待线程数量:queue="+size);
                activeCount_a = activeCount;
                size_a = size;
                System.out.println("=========================");
            }
        }


    }

    /**
     * 线程池如何合理配置
     * 1：CPU密集性
     * 2：IO密集性
     *
     * 生产环境中如何配置 corePoolSize 和 maximumPoolSize
     * 这个是根据具体业务来配置的，分为CPU密集型和IO密集型
     *
     * CPU密集型
     * CPU密集的意思是该任务需要大量的运算，而没有阻塞，CPU一直全速运行
     * CPU密集任务只有在真正的多核CPU上才可能得到加速（通过多线程）
     * CPU密集型任务配置尽可能少的线程数量：
     * 一般公式：CPU核数 + 1个线程数
     *
     * IO密集型
     * 由于IO密集型任务线程并不是一直在执行任务，则可能多的线程，如 CPU核数 * 2
     * IO密集型，即该任务需要大量的IO操作，即大量的阻塞
     * IO密集时，大部分线程都被阻塞，故需要多配置线程数：
     * 参考公式：CPU核数 / (1 - 阻塞系数) 阻塞系数在0.8 ~ 0.9左右
     * 例如：8核CPU：8/ (1 - 0.9) = 80个线程数
     *
     *
     * 配置线程池的大小可根据以下几个维度进行分析来配置合理的线程数：
     *     任务性质可分为：CPU密集型任务，IO密集型任务，混合型任务。
     *     任务的执行时长。
     *     任务是否有依赖——依赖其他系统资源，如数据库连接等。
     *
     *     CPU密集型任务
     * 尽量使用较小的线程池，一般为CPU核心数+1。
     * 因为CPU密集型任务使得CPU使用率很高，若开过多的线程数，只能增加上下文切换的次数，因此会带来额外的开销。
     *
     *     IO密集型任务
     * 可以使用稍大的线程池，一般为2*CPU核心数+1。
     * 因为IO操作不占用CPU，不要让CPU闲下来，应加大线程数量，因此可以让CPU在等待IO的时候去处理别的任务，充分利用CPU时间。
     *
     *     混合型任务
     * 可以将任务分成IO密集型和CPU密集型任务，然后分别用不同的线程池去处理。
     * 只要分完之后两个任务的执行时间相差不大，那么就会比串行执行来的高效。
     * 因为如果划分之后两个任务执行时间相差甚远，那么先执行完的任务就要等后执行完的任务，最终的时间仍然取决于后执行完的任务，而且还要加上任务拆分与合并的开销，得不偿失
     *
     *     依赖其他资源
     * 如某个任务依赖数据库的连接返回的结果，这时候等待的时间越长，则CPU空闲的时间越长，那么线程数量应设置得越大，才能更好的利用CPU。
     * 最佳线程数目 = （（线程等待时间+线程CPU时间）/线程CPU时间 ）* CPU数目
     * 比如平均每个线程CPU运行时间为0.5s，而线程等待时间（非CPU运行时间，比如IO）为1.5s，CPU核心数为8，那么根据上面这个公式估算得到：((0.5+1.5)/0.5)*8=32。
     *
     * 线程等待时间所占比例越高，需要越多线程。线程CPU时间所占比例越高，需要越少线程。
     */
    @Test
    public void  test1(){
        //获取CUP 处理器数
        int cupNumber = Runtime.getRuntime().availableProcessors();
        int maxThred = (int) (cupNumber / (1-0.9));
        System.out.println(maxThred);

    }

}



 class myTask implements  Runnable{
    int sleep;
    String name;
    int i;
     myTask(int sleep,String name){
         this.sleep = sleep;
         this.name = name;
     }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + name +" runing....");
        try {
            IntStream.range(0,100000000).forEach(i->{
               i++;
            });
            //TimeUnit.SECONDS.sleep(sleep);
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()+name+"被终止..error="+e.getMessage());
        }
    }

}
