package com.river.learn.java.thread.threadLocal;

/**
 * 单例
 * @author 17822
 */
public class ThreadLocalContext {

    private static ThreadLocalContext threadLocalContext;

    private final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>(){
        //重写ThreadLocal初始方法
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    private ThreadLocalContext(){}

    public static ThreadLocalContext getInstall(){
        return ContextHolder.threadLocalContext;
    }

    private static class ContextHolder{
        private static ThreadLocalContext threadLocalContext = new ThreadLocalContext();
    }

    public ThreadLocal<Context> getThreadLocal() {
        return threadLocal;
    }
}
