package com.river.learn.java.thread;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author 17822
 */
public class CustomLockTest {

    private static Lock lock = new CustomLock();

    public  void test(){
        try {
            lock.lock(1000*2);
            Optional.of(Thread.currentThread()+" runing...").ifPresent(System.out::println);
            Thread.sleep(1000*1);

            Optional.of(lock.getBlockedThreads()).ifPresent(System.out::println);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

        CustomLockTest tset = new CustomLockTest();

        Stream.of("t1","t2","t3","t4").forEach(val->{

            new Thread(val){
                @Override
                public void run() {
                    tset.test();
                }
            }.start();

            try {
                Thread.sleep(100);
                lock.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        });
    }

}
