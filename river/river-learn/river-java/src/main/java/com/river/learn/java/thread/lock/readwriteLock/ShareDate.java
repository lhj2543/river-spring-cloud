package com.river.learn.java.thread.lock.readwriteLock;

/**
 * 共享数据类
 * @author 17822
 */
public class ShareDate{

    private  ReadWiteLocK lock = new ReadWiteLocK(true);

    private char[] chars;

    public ShareDate(){
    }

    public ShareDate(char[] chars){
        this.chars = chars;
    }

    public char[] read() throws InterruptedException {
        try {
            lock.readLock();
            return doReadDate();
        } finally {
            lock.unReadLock();
        }
    }

    private char[] doReadDate() throws InterruptedException {
        if(this.chars==null){
            return  null;
        }

        int length = this.chars.length;
        char[] result = new char[length];
        for(int i=0;i<length;i++){
            result[i]  = this.chars[i];
        }
        return  result;

    }


    public void wite(char[] chars) throws InterruptedException {
        try {
            lock.witeLock();
            this.doWiteDate(chars);
        } finally {
            lock.unWiteLock();
        }
    }

    private void doWiteDate(char[] chars) {
        int length = chars.length;
        this.chars = new char[length];
        for(int i=0;i<length;i++){
            this.chars[i]  = chars[i];
        }
    }

}
