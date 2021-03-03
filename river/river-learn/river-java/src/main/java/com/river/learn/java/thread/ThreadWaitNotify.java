package com.river.learn.java.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * wait() notify() notifyAll()
 *
 * sleep() 和 wait() 区别
 * 1：sleep 是 Thread的方法，wait 是Object的方法
 * 2：sleep和wait都是休眠作用，sleep指定休眠时间后会自动唤醒，而wait必须通过notify或notifyAll来唤醒。（除wait(1000),设置的休眠时间外）
 * 3：sleep不会释放锁，wait会释放锁,wait被notify唤醒后仍然需要获取锁
 * 4: wait() notify() 必须有 synchronized 不然会报错
 * 生产者和消费者案例
 * @author 17822
 */
public class ThreadWaitNotify {

    //共享数据
    private  int i;

    //是否生产了数据
    private volatile boolean isProduct = false;

    //对象锁
    private final Object LOCK = new Object();

    //生产者
    public void product(){
        synchronized (LOCK){
            while (isProduct){
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            i++;
            isProduct  = true;
            LOCK.notifyAll();
            System.out.println(Thread.currentThread().getName()+"生产了数据："+i);
        }
    }

    //消费者
    public void  consume(){
        synchronized (LOCK){
            while(!isProduct){
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName()+"消费了数据："+i);
            i--;
            isProduct = false;
            LOCK.notifyAll();
        }
    }

    public static void main(String[] args) {

        //ThreadWaitNotify wn = new ThreadWaitNotify();
        ProductConsume2 wn = new ProductConsume2();

        Stream.of("p1","p2","p3").forEach(val->{
            new Thread(val){
                @Override
                public void run() {
                    for(int i=0;i<1000;i++){
                        wn.product();
                    }
                }
            }.start();
        });

        Stream.of("c1","c2","c3").forEach(val->{
            new Thread(val){
                @Override
                public void run() {
                    for(int i=0;i<1000;i++){
                        wn.consume();
                    }
                }
            }.start();
        });


    }


}

/**
 * 生产者与消费者2
 */
class ProductConsume2{

    private int i = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void product(){
        try {
            lock.lock();

            while (i!=0){
                condition.await();
            }
            i++;
            condition.signalAll();
            System.out.println(Thread.currentThread().getName()+"生产了数据："+i);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void consume(){
        try {
            lock.lock();

            while (i==0){
                condition.await();
            }
            System.out.println(Thread.currentThread().getName()+"消费了数据："+i);
            i--;
            condition.signalAll();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}

/**
 * 生产者与消费者3（高并发）
 */
class ProductConsume3{

    private volatile boolean  falg = true;

    private BlockingQueue queue = null;

    private AtomicInteger atomicInteger = new AtomicInteger();

    ProductConsume3(BlockingQueue queue){
        this.queue = queue;
    }

    public void product() throws InterruptedException {
        boolean offer;
        String data = null;
        while (falg){
            data = atomicInteger.incrementAndGet() + "";
            offer = queue.offer(data, 1, TimeUnit.SECONDS);
            if(offer){
                System.out.println(Thread.currentThread().getName()+" 生产数据："+data);
            }else {
                System.out.println(Thread.currentThread().getName()+" 生产数据失败："+data);
            }
            TimeUnit.SECONDS.sleep(2);
        }

    }

    public void consume() throws InterruptedException {
        Object data = null;
        while (falg){
            data = queue.poll(1, TimeUnit.SECONDS);
            if(data != null){
                System.out.println(Thread.currentThread().getName()+" 消费数据："+data);
            }else {
                System.out.println(Thread.currentThread().getName()+" 数据已消费完");
                this.falg = false;
                return;
            }
        }
    }

    public static void main(String[] args) {

        //ThreadWaitNotify wn = new ThreadWaitNotify();
        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(2);
        ProductConsume3 wn = new ProductConsume3(arrayBlockingQueue);

        Stream.of("p1","p2","p3").forEach(val->{
            new Thread(()->{
                try {
                    wn.product();
                }catch (Exception e){
                    e.printStackTrace();
                }
            },val).start();
        });

        Stream.of("c1").forEach(val->{
            new Thread(()->{
                try {
                    wn.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },val).start();
        });


    }


}
