package com.river.learn.java.thread.guardedSuspension;

import java.util.LinkedList;

/**
 * Guarded Suspension设计模式
 * 被监视的挂起
 * 当客户端发送较多的请求时，服务端业务繁忙不能及时的服务时，通过队列方式先挂起来，等到有空闲的时候再去处理
 * @author 17822
 */
public class GuardedSuspension {

    public LinkedList<ReuqestDto> reuqest = new LinkedList<ReuqestDto>();

    private volatile boolean isClose = false;

    public void putRequest(ReuqestDto reuqestDto){
        synchronized (reuqest){
            //往最后添加一个请求
            this.reuqest.addLast(reuqestDto);
            reuqest.notifyAll();
        }
    }

    public ReuqestDto getRequest(){

        synchronized (reuqest){
            if(reuqest.isEmpty() || reuqest.size()<=0){
                try {
                    reuqest.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            ReuqestDto reuqestDto = this.reuqest.removeFirst();
            return  reuqestDto;
        }

    }

}
