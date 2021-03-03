package com.river.learn.java.thread.observer;

/**
 * @author 17822
 */
public class ObserverClient {
    public static void main(String[] args) {

        Subject s = new Subject();

        ObserverA a = new ObserverA(s);
        ObserverB b = new ObserverB(s);

        s.setState("a");
        s.setState("a");
        s.setState("ab");

    }
}
