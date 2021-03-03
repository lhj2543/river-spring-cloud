package com.river.learn.java.thread.single;

/**
 * @author 17822
 */
public class ShareDate {

    private String name;
    private String addr;

    public ShareDate(String name, String addr) {
        this.name = name;
        this.addr = addr;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean test(){
        if(this.name.charAt(0)==this.addr.charAt(0) ){
            System.err.println(this.toString() + Thread.currentThread().getName()+" blocked..");
            return true;
        }else {
            System.out.println(">>>>>>>>>>>"+this.toString());
        }

        return false;

    }

    @Override
    public String toString() {
        return "ShareDate{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }
}
