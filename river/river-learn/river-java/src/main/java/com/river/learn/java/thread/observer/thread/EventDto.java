package com.river.learn.java.thread.observer.thread;

public class EventDto{

    private Thread thread;

    private Sate sate;

    private Exception e;

    public EventDto(Thread thread, Sate sate, Exception e) {
        this.thread = thread;
        this.sate = sate;
        this.e = e;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Sate getSate() {
        return sate;
    }

    public void setSate(Sate sate) {
        this.sate = sate;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "thread=" + thread +
                ", sate=" + sate +
                ", e=" + e +
                '}';
    }
}

enum Sate{
    RUNGING,BLOCK,BEVOER;
}
