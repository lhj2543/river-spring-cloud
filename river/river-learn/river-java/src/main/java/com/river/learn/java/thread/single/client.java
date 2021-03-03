package com.river.learn.java.thread.single;

import java.util.stream.Stream;

/**
 * @author 17822
 */
public class client {

    public static void main(String[] args) {


        Stream.of("t1","t2").forEach(val->{
            new Thread(val){
                @Override
                public void run() {
                    while (true){
                        ShareDate d = new ShareDate("shanghi","shpi");
                        d.test();
                    }
                }
            }.start();
        });

        Stream.of("p1","p2").forEach(val->{
            new Thread(val){
                @Override
                public void run() {
                    boolean flag = false;
                    while (!flag){
                        ShareDate d1 = new ShareDate("shanghi","beijing");
                        flag = d1.test();
                    }
                }
            }.start();
        });

    }

}

class  userShareDate{
    private String name;
    private String addr;
    private ShareDate shareDate;

    public userShareDate(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }
}

