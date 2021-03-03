package com.river.learn.java.collection;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * ArrayList 线程不安全的。查找快，增删慢
 * ArrayList 底层使用数组存储数据
 * 默认大小10，按1.5倍扩容，将旧的数组数据复制到新的数组中，new ArrayList()时，能确定数据大小时，建议指定默认大小
 *
 * List<?> ?通配符，不允许添加数据，除了null
 * 添加限制通配符
 * List<? extends A>  添加数据必须是<=A 比如A的父类
 * List<? super A> 添加数据必须是>=A 比如A的子类 ，Object
 *
 * @author 17822
 */
public class ArrayListDemo {


    /**
     * ArrayList 线程不安全，如何解决
     *
     * 为什么线程不安全：原因
     *
     * 解決方案:
     *  new Vector() 并发性能低
     *  Collections.synchronizedList(new ArrayList<>());
     *  new CopyOnWriteArrayList<>()
     *
     *  CopyOnWrite 写时复制，往一个容器添加元素时候，不直接往当前容器Object[]数据添加，而是先将当前容器Object[]进行复制，
     *  复制出一个新的容器Object[] 然后新容器Object[]添加元素，添加完后再讲原容器的应用指向新容器。这样做的好处是对CopyOnWrite并发的读数据，不需要加锁，CopyOnWrite是读写分离的思想
     */
    @Test
    public void test1(){

        //线程不安全
        //ArrayList list = new ArrayList();

        //创建线程安全List
        //List list = new Vector();
        //List<Object> list = Collections.synchronizedList(new ArrayList<>());
        List<Object> list = new CopyOnWriteArrayList<>();

        int i = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(i);

        for(int j=0;j<i;j++){

            Thread thread = new Thread(() -> {

                countDownLatch.countDown();
                list.add(System.currentTimeMillis());

                //System.out.println(Thread.currentThread().getName());

            },String.valueOf(j));

            thread.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(list.size());

    }

    @Test
    public void test2(){

        HashSet set = new HashSet();
        set.add("a");



    }



}
