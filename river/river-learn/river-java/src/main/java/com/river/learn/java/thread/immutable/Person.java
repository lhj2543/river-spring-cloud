package com.river.learn.java.thread.immutable;

import java.util.Collections;
import java.util.List;

/**
 * 不可变对象
 * 1：不可变对象一定是线程安全
 * 2：所有的属性或引用都是final修饰,只提供get方法，注意：引用类型对象get的时候一定要保证不可修改，或返回全新的一个对象
 * 3：String类就是一个不可变对象，每次修改String都是返回一个新的对象
 */
public final class Person {

    private final String name;

    private final String notes;

    private final List list;

    public Person(final String name, final String notes,final List list) {
        this.name = name;
        this.notes = notes;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public List getList() {
        //防止数据修改
        return Collections.unmodifiableList(list);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

}
