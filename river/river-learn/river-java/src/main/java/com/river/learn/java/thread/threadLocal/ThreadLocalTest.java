package com.river.learn.java.thread.threadLocal;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * ThreadLocal并不是一个Thread，而是Thread的局部变量
 * ThreadLocal是解决线程安全问题一个很好的思路，它通过为每个线程提供一个独立的变量副本解决了变量并发访问的冲突问题。
 * 在很多情况下，ThreadLocal比直接使用synchronized同步机制解决线程安全问题更简单，更方便，且结果程序拥有更高的并发性。
 *
 * 在Java的多线程编程中，为保证多个线程对共享变量的安全访问，通常会使用synchronized来保证同一时刻只有一个线程对共享变量进行操作。
 * 这种情况下可以将类变量放到ThreadLocal类型的对象中，使变量在每个线程中都有独立拷贝，不会出现一个线程读取变量时而被另一个线程修改的现象。
 * 最常见的ThreadLocal使用场景为用来解决数据库连接、Session管理等
 *
 * 总结：
 * 在每个线程Thread内部有一个ThreadLocal.ThreadLocalMap类型的成员变量threadLocals，
 * 这个threadLocals就是用来存储实际的变量副本的，键值为当前ThreadLocal变量，value为变量副本（即T类型的变量）。
 * 初始时，在Thread里面，threadLocals为空，当通过ThreadLocal变量调用get()方法或者set()方法，就会对Thread类中的threadLocals进行初始化，
 * 并且以当前ThreadLocal变量为键值，以ThreadLocal要保存的副本变量为value，存到threadLocals。
 * 然后在当前线程里面，如果要使用副本变量，就可以通过get方法在threadLocals里面查找
 *
 * 在进行get之前，必须先set，否则会报空指针异常；如果想在get之前不需要调用set就能正常访问的话，必须重写initialValue()方法
 *
 * @author 17822
 */
public class ThreadLocalTest {

    static ThreadLocal<Object>  threadLocal = new ThreadLocal<Object>();

    public static void main(String[] args) {

        IntStream.range(0,3).forEach(i->{

            threadLocal.set("main"+i);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocal.set(Thread.currentThread().getName());

                    Optional.of(threadLocal.get()).ifPresent(System.out::println);
                }
            },"线程"+i).start();

            Optional.of(threadLocal.get()).ifPresent(System.out::println);

        });

    }

}
