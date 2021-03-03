package com.river.learn.java.java8;

import org.junit.Test;

import java.util.Optional;

/**
 * Optional 类，是一个容器，代表一个值存在或不存在，原来用null,Optional可以避免空指针异常
 *
 * @author 17822
 */
public class OptionalDemo {


    @Test
    public void test1(){

        //创建 Optional 实例
        Optional<String> opt = Optional.of("哈哈哈哈");

        //创建 Optional 空实例
        Optional.empty();

        //Optional.ofNullable(T value)   若T不为空，创建OPtional实例，否则创建空实例
        Optional.ofNullable("");

        //判断是否包含值
        opt.isPresent();

        //如果调用对象包含值，返回值，否则返回 T (传进去的参数)
        String a = opt.orElse("a");
        System.out.println(a);

        //如果调用对象包含值，返回值，否则返回 Supplier 构造函数返回值
        opt.orElseGet(()->{
          return "kasdf";
        });

        //如果有值对其处理，并返回处理后的Optional ,否则返回Optional.empty();
        Optional<String> s = opt.map((name) -> {
            return name + "_11";
        });
        System.out.println(s.get());

        //与map() 类似，要求返回值类型必须是Optional
        Optional<String> s1 = opt.flatMap((name) -> {
            return Optional.of(name);
        });
        System.out.println(s1.get());

        //判断是否包含值
        opt.ifPresent(System.out::println);

        String s2 = opt.get();

    }

}
