package com.river.learn.java.dataStructure;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 数组环形队列
 *
 * @author 17822
 */
public class RiverArrayQueue<T> {

    /**
     * 队列最大值
     */
    private int maxSize;

    /**
     * 队列数据
     */
    private Object[] data;

    /**
     * 往队列添加数据时数组的下标，，默认=0，每插入队列一次下标往后移一位
     */
    private AtomicInteger inIndex;

    /**
     * 往队列取数据时数组的下标，，默认=0，每从队列取出数据一次下标往后移一位
     */
    private AtomicInteger outIndex;

    public RiverArrayQueue(){
        this.init(10);
    }
    public RiverArrayQueue(int maxSize){
        this.init(maxSize);
    }

    private void init(int maxSize){
        this.maxSize = maxSize;
        data =  new Object[maxSize];
        inIndex = new AtomicInteger(0);
        outIndex =  new AtomicInteger(0);
    }

    public synchronized void add(T data) throws Exception{

        if (isFull()){
            throw new RuntimeException("队列已满");
        }

        int index = (inIndex.intValue()+maxSize)%maxSize;

        this.data[index] = data;

        this.inIndex.incrementAndGet();

    }

    public synchronized T get() throws Exception{

        if (isEmpty()){
            throw new RuntimeException("队列为空");
        }

        int index = (outIndex.intValue()+maxSize)%maxSize;

        T datum = (T)this.data[index];
        this.data[index] = null;

        this.outIndex.incrementAndGet();

        return datum;
    }

    /**
     * 队列是否已满
     */
    private boolean isFull(){
        return (inIndex.intValue()>0 && (inIndex.intValue())%maxSize==outIndex.intValue()) || this.size()>=this.maxSize;
    }

    /**
     * 队列是否已空
     */
    private boolean isEmpty(){
        return inIndex.intValue()==outIndex.intValue() || this.size()<=0;
    }

    /**
     * 队列大小
     * @return
     */
    public int size(){
        /*System.out.println("inIndex=="+inIndex.intValue());
        System.out.println("outIndex=="+outIndex.intValue());*/
        return this.inIndex.intValue()-this.outIndex.intValue();
    }

    public void showList(){
        for (int i=0;i<this.data.length;i++) {
            System.out.println(i + "==" + this.data[i]);
        }
    }

    public static void test(){

        int size = 250;
        RiverArrayQueue<String> queue = new RiverArrayQueue<String>(size);

        CountDownLatch countDownLatch = new CountDownLatch(size);

        IntStream.range(0,size).forEach((i)->{
            Thread thread = new Thread(()->{
                try {
                    queue.add(String.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }finally {
                    countDownLatch.countDown();
                }
            },"t-"+i);

            thread.start();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        queue.showList();

    }

    public static void test2(){
        RiverArrayQueue<String> queue = new RiverArrayQueue<String>(5);

        Scanner scanner = new Scanner(System.in);
        char key = ' ';

        boolean flag = true;
        while (flag){

            key = scanner.next().charAt(0);

            switch (key){
                case 'l':
                    queue.showList();
                    break;
                case 'a':
                    String v = scanner.nextLine();
                    try {
                        queue.add(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("异常："+e.getMessage());
                    }
                    break;
                case 'g':
                    try {
                        System.out.println(queue.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("异常："+e.getMessage());
                    }
                    break;
                case 's':
                    System.out.println("队列size=="+queue.size());
                    break;
                case 'e':
                    flag = false;
                    break;
                default:
                    System.out.println("输入指令的错误");
                    break;
            }

        }
    }

    public static void main(String[] args) {

        //test();

        //test2();

        LinkedList<String> linkedList = new LinkedList();
        linkedList.add("aaa");
        linkedList.add("bbb");
        linkedList.add("ccc");

        linkedList.remove("aaa");

        System.out.println(linkedList);

    }

}
