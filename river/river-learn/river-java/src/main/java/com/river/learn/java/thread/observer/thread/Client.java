package com.river.learn.java.thread.observer.thread;

public class Client {
    public static void main(String[] args) {

        ObserverThreadAImp ota = new ObserverThreadAImp();
        ota.method1("t1");

        ota.method1("t2");
    }
}
