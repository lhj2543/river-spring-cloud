package com.river.learn.java.thread;

/**
 * @author 17822
 */
public class ShutdownHook {

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("监听到程序异常...");
            }
        }));

        try {
            test1();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static  void  test1() throws Exception {
        int i=0;

        while (true){
            System.out.println("runging");
            i++;
            if(i>20){
                throw new Exception("运行异常");
            }
        }

    }

}
