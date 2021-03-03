package com.river.learn.java.thread.lock;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁(spinlock)
 * 自旋锁是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，没有类似于wait的阻塞，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU
 *
 * 原子类型的那些AtomicInteger等，就是采用的自旋锁 + CAS
 *
 *
 * @author 17822
 */
public class SpinlockDemo {

    public static void main(String[] args) {

        AtomicInteger atomicInteger = new AtomicInteger();

        AtomicDouble atomicDouble = new AtomicDouble();
        atomicDouble.addAndGet(12d);

        SpinlockCustom spinlockCustom = new SpinlockCustom();
        spinlockCustom.atomicReference.getAndSet(Thread.currentThread());

        System.out.println(spinlockCustom.atomicReference.get());

        SpinlockCustom spinlockCustom2 = new SpinlockCustom();
        System.out.println(spinlockCustom2.atomicReference.get());


    }

}

/**
 * 自定义自旋锁
 */
class SpinlockCustom{


    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock(){
        // 获取当前进来的线程
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "\t come in ");

        // 开始自旋，期望值是null，更新值是当前线程，如果是null，则更新为当前线程，否者自旋
        while(!atomicReference.compareAndSet(null, thread)) {

        }

    }

    public void unLock(){

        // 获取当前进来的线程
        Thread thread = Thread.currentThread();

        // 自己用完了后，把atomicReference变成null
        atomicReference.compareAndSet(thread, null);

        System.out.println(Thread.currentThread().getName() + "\t invoked unlock()");

    }

}

@Data
class User{
    private String name;
}

