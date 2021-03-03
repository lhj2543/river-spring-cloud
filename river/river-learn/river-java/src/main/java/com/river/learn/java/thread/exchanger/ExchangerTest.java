package com.river.learn.java.thread.exchanger;

import java.util.UUID;
import java.util.concurrent.Exchanger;

/**
 * Exchanger 用于两个工作线程之间交换数据的封装工具类
 * 就是一个线程在完成一定的事务后想与另一个线程交换数据，则第一个先拿出数据的线程会一直等待第二个线程，直到第二个线程拿着数据到来时才能彼此交换对应数据。其定义为 Exchanger<V> 泛型类型，其中 V 表示可交换的数据类型
 *
 * Exchanger 注意：交换的数据是同一个对象：引用地址一样，交换的数据改变时，被交换的数据也会改变
 * @author 17822
 */
public class ExchangerTest {
    public static void main(String[] args) {

        Exchanger<String> exchanger = new Exchanger<String>();
        new Persion(exchanger).start();
        new Persion2(exchanger).start();

    }


    private static class Persion extends  Thread{

        private Exchanger<String> exchanger;
        public String name;

        public Persion(Exchanger<String> exchanger) {
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            name = this.getName()+ UUID.randomUUID();
            System.out.println(this.getName()+"交换前name="+name);
            try {
                String exchange = exchanger.exchange(name);
                System.out.println(this.getName()+"交换前后name="+exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private static class Persion2 extends  Thread{

        private Exchanger<String> exchanger;
        public String name;

        public Persion2(Exchanger<String> exchanger) {
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            name = this.getName()+ UUID.randomUUID();
            System.out.println(this.getName()+"交换前name="+name);
            try {
                String exchange = exchanger.exchange(name);
                System.out.println(this.getName()+"交换前后name="+exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
