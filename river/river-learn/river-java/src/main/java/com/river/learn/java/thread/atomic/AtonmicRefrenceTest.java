package com.river.learn.java.thread.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Stream;

/**
 * AtomicReference 原子引用
 * AtomicStampedReference 解决CAS  ABA问题
 * @author 17822
 */
public class AtonmicRefrenceTest {

    @Test
    public void test1(){

        User us = new User("张三",12);
        User us2 = new User("张三2",32);
        AtomicReference<User> u = new AtomicReference<User>();
        u.getAndSet(us);

        u.compareAndSet(us,us2);

        System.out.println(u.get().toString());
        int v1 = (int)System.currentTimeMillis();
        AtomicStampedReference<User> uu = new AtomicStampedReference<User>(us,v1);

        int v2 = (int)System.currentTimeMillis();
        uu.compareAndSet(us,us2,v1,v2);

        System.out.println(uu.getReference().toString());


    }

}

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class User{
    private String name;
    private int age;
}
