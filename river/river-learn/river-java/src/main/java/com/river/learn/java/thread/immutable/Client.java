package com.river.learn.java.thread.immutable;

import java.util.stream.IntStream;

public class Client {

    public static void main(String[] args) {


        IntStream.range(0,10).forEach(i->{
            UserPerson u = new UserPerson("哈哈哈","notes");
            u.start();
        });


    }
}
