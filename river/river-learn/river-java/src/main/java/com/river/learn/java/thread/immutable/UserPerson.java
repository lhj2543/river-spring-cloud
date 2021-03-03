package com.river.learn.java.thread.immutable;

import java.util.Optional;

/**
 * @author 17822
 */
public class UserPerson extends Thread {

    private Person person;

    public UserPerson(String name,String nodes){
        person = new Person(name,nodes,null);
    }

    @Override
    public void run() {
        Optional.of(person.toString()).ifPresent(System.out::println);
    }
}
