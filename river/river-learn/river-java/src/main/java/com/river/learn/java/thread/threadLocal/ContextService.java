package com.river.learn.java.thread.threadLocal;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author 17822
 */
public class ContextService implements  Runnable {


    @Override
    public void run() {
        getContextId();
        getContextNameById();

        ThreadLocal<Context> threadLocal = ThreadLocalContext.getInstall().getThreadLocal();
        Context context = threadLocal.get();

        System.out.println(Thread.currentThread().getName()+" coutext data = "+context.toString());
    }

    public void getContextId(){
        Random random = new Random(System.currentTimeMillis());
        int id = random.nextInt(10000);
        System.out.println(Thread.currentThread().getName()+"获取id="+id);
        ThreadLocal<Context> threadLocal = ThreadLocalContext.getInstall().getThreadLocal();
        Context context = threadLocal.get();
        context.setId(String.valueOf(id));
    }

    public void getContextNameById(){

        ThreadLocal<Context> threadLocal = ThreadLocalContext.getInstall().getThreadLocal();
        Context context = threadLocal.get();
        System.out.println(Thread.currentThread().getName()+"根据ID获取name。。。id="+context.getId());

        context.setName(UUID.randomUUID().toString());
    }

}

class  test{
    public static void main(String[] args) {

        IntStream.range(0,10).forEach(i->{
            new Thread(new ContextService(),"线程"+i).start();
        });

    }
}
