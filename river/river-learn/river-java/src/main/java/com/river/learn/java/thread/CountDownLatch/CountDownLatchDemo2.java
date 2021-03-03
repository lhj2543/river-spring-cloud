package com.river.learn.java.thread.CountDownLatch;

import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 小案例
 * 循环60次去做一件事件，每个循环耗时10秒，，循环100次就是10*60秒=10分钟，性能太低
 * 通过多线程方式：开启10个线程同时去做这个事情，也就需要60/10*10=60秒，大大的提高了性能；
 * @author 17822
 */
public class CountDownLatchDemo2 {

    //线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Test
    public void  test(){

        long startTime = System.currentTimeMillis();

        CountDownLatch countDownLatch =  new CountDownLatch(60);

        List result = new ArrayList();

        IntStream.range(0,60).forEach(i->{
            User u = new User("张三"+i,i);
            executorService.execute(new Task(u,countDownLatch,result));
        });

        try {
            countDownLatch.await();
            //executorService.wait();
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("总任务结束。。。result="+result);
        System.out.println("总任务结束。。。result size="+result.size());
        long endTime = System.currentTimeMillis();
        System.out.println("总任务使用时间。。。time="+(endTime-startTime)/1000+"s");

    }

    @Test
    public void test2(){

        long startTime = System.currentTimeMillis();

        List result = new ArrayList();

        IntStream.range(0,60).forEach(i->{
            User u = new User("张三"+i,i);
            Optional.of(Thread.currentThread().getName()+"====="+u.toString()).ifPresent(System.out::println);
            result.add(UUID.randomUUID());
            try {
                Thread.sleep(1000*1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("总任务结束。。。result="+result);
        System.out.println("总任务结束。。。result size="+result.size());
        long endTime = System.currentTimeMillis();
        System.out.println("总任务使用时间。。。time="+(endTime-startTime)/1000+"s");

    }



    private class Task implements  Runnable{

        List result;

        private User u;

        private CountDownLatch countDownLatch;

        public Task(User u,CountDownLatch countDownLatch,List result){
            this.u = u;
            this.countDownLatch = countDownLatch;
            this.result = result;
        }

        @Override
        public void run() {
            this.doTask();
        }

        private void doTask(){
            try {
                Optional.of(Thread.currentThread().getName()+"====="+u.toString()).ifPresent(System.out::println);
                Thread.sleep(1000*1);
                synchronized (result){
                    result.add(UUID.randomUUID());
                }
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    //实体类
    private class User implements Serializable {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}
