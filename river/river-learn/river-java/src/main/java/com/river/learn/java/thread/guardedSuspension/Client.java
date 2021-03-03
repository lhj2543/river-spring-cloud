package com.river.learn.java.thread.guardedSuspension;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * 测试
 * @author 17822
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {

       GuardedSuspension guardedSuspension = new GuardedSuspension();

        ClientThread.request(guardedSuspension);

        ServerThread serverThread = new ServerThread(guardedSuspension);
        serverThread.start();

        Thread.sleep(1_000 *4);
        serverThread.close();
        //System.out.println(Arrays.asList(guardedSuspension.reuqest));
    }

}

/**
 * 客户端
 */
class ClientThread{

    public static void request(GuardedSuspension guardedSuspension){

        Random random = new Random(System.currentTimeMillis());

        IntStream.range(0,10).forEach(i->{
            new Thread("请求线程"+i){
                @Override
                public void run() {
                    ReuqestDto reuqestDto = new ReuqestDto(String.valueOf(random.nextInt()));
                    guardedSuspension.putRequest(reuqestDto);
                    Optional.of(this.getName()+"发送请求：request = "+reuqestDto.getName()).ifPresent(System.out::println);
                }
            }.start();
        });

    }

}

/**
 * 服务端
 */
class ServerThread extends Thread{

    private GuardedSuspension guardedSuspension;
    private volatile  boolean isClosed = false;

    ServerThread(GuardedSuspension guardedSuspension){
        this.guardedSuspension = guardedSuspension;
    }

    @Override
    public void run() {
        while (!isClosed){
            ReuqestDto request = guardedSuspension.getRequest();
            Optional.of(this.getName()+"处理请求数据：request = "+request).ifPresent(System.out::println);
        }
    }

    public void close(){
        this.isClosed = true;
        this.interrupt();
    }

}