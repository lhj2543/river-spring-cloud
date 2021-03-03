package com.river.learn.java.thread.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 17822
 */
public class Subject {

   private List<Observer> observers = new ArrayList<Observer>();

   private String state;

   public Subject(){}

    public String getState() {
        return state;
    }

    public void  addObserver(Observer observer){
        observers.add(observer);
    }

    public void setState(String state) {
       if(this.state != state){
           this.state = state;
            this.notifyAllObserver();
       }
    }

    private void notifyAllObserver(){
        //通知所有观察者
        observers.stream().forEach(Observer::update);
    }


}
