package com.river.learn.java.thread.queue.blockingQueue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 阻塞队列
 * 当阻塞队列是空时，从队列中获取元素的操作将会被阻塞
 * ​当阻塞队列是满时，从队列中添加元素的操作将会被阻塞
 * ​
 * ​也就是说 试图从空的阻塞队列中获取元素的线程将会被阻塞，直到其它线程往空的队列插入新的元素
 * 同理，试图往已经满的阻塞队列中添加新元素的线程，直到其它线程往满的队列中移除一个或多个元素，或者完全清空队列后，使队列重新变得空闲起来，并后续新增
 *
 * 为什么需要BlockingQueue
 * 好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都帮你一手包办了
 *
 * BlockingQueue阻塞队列是属于一个接口，底下有七个实现类
 * ArrayBlockQueue：由数组结构组成的有界阻塞队列
 * LinkedBlockingQueue：由链表结构组成的有界（但是默认大小 Integer.MAX_VALUE）的阻塞队列
 * 有界，但是界限非常大，相当于无界，可以当成无界
 * PriorityBlockQueue：支持优先级排序的无界阻塞队列
 * DelayQueue：使用优先级队列实现的延迟无界阻塞队列
 * SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列	（生产一个，消费一个，不存储元素，不消费不生产）
 * LinkedTransferQueue：由链表结构组成的无界阻塞队列
 * LinkedBlockingDeque：由链表结构组成的双向阻塞队列
 *
 * 这里需要掌握的是：ArrayBlockQueue、LinkedBlockingQueue、SynchronousQueue
 *
 * BlockingQueue核心方法
 * 抛出异常：
 *      当阻塞队列满时：在往队列中add插入元素会抛出 IIIegalStateException：Queue full    当阻塞队列空时：再往队列中remove移除元素，会抛出NoSuchException
 *      插入：add(e) 移除：remove() 检查：element()
 * 特殊值：
 *      插入方法，成功true，失败false       移除方法：成功返回出队列元素，队列没有就返回空
 *      offer(e)  poll()  peek()
 * 阻塞:
 *      当阻塞队列满时，生产者继续往队列里put元素，队列会一直阻塞生产线程直到put数据or响应中断退出， 当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用。
 *      put(e) take()
 * 超时：
 *      当阻塞队列满时，队里会阻塞生产者线程一定时间，超过限时后生产者线程会退出
 *      offer(e,time,unit)
 *      poll(time,unit)
 *
 * @author 17822
 */
public class BlockingQueueDemo {

    @Test
    public void test1(){

        ArrayBlockingQueue<String> list = new ArrayBlockingQueue<String>(2);
        list.add("a");
        list.add("b");

        String remove = list.remove();
        System.out.println(remove);

        String element = list.element();
        System.out.println(element);

        list.offer("dd");
        System.out.println(list.offer("d2d"));

        try {
            list.put("ee");
            list.put("ff");
            System.out.println("111111111111111");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
