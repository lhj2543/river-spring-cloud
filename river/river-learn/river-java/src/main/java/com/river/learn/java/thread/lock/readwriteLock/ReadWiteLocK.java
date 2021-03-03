package com.river.learn.java.thread.lock.readwriteLock;

/**
 * 自定义读写锁
 * 读写分离锁
 * @author 17822
 */
public class ReadWiteLocK {
    //当前读数据的线程数
    private int readNumber;
    //当前等待读数据的线程数
    private int waitReadNumber;
    //当前写数据的线程数
    private int witeNumber;
    //当前等待写数据的线程数
    private int waitWiteNumber;

    private boolean preferWite;

    public ReadWiteLocK(){
        this(false);
    }

    public ReadWiteLocK(boolean preferWite){
        this.preferWite = preferWite;
    }

    public synchronized void readLock() throws InterruptedException {
        waitReadNumber++;
        try {
            //判断是否有线程在写数据,如果有就不能读数据
            //判断是否写的优先级大于读的优先级
            while (witeNumber>0 || (preferWite && waitWiteNumber>0)){
                this.wait();
            }
            readNumber++;
        }finally {
            waitReadNumber--;
        }
    }

    public synchronized void unReadLock(){
        readNumber--;
        this.notifyAll();
    }

    public synchronized void witeLock() throws InterruptedException {
        waitWiteNumber++;
        try {
            //判断是否有线程在写数据或在读数据,如果有就不能写数据
            while (witeNumber>0 || waitReadNumber>0){
                this.wait();
            }
            witeNumber++;
        }finally {
            waitWiteNumber--;
        }
    }

    public synchronized void unWiteLock(){
        witeNumber--;
        this.notifyAll();
    }


}
