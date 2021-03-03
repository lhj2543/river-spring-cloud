package com.river.learn.java.java8;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lambda 表达式
 * -> 操作符将Lambda表达式拆成两部分：
 * 左侧：Lambda 表达式参数列表
 * 右侧：Lamdba 表达式执行体
 *
 * 语法格式：
 *  无参数，无返回值： （）-> {}
 *  有参数，无返回值,若只有一个参数，（）可以省略不写： （x,y,....）-> {}
 *  有参数，有返回值：
 *     Object obj =（x,y）->{
 *          return null;
 *      };
 *      若方法体只有一条语句，{} 和 return 可以省略
 *      Object obj =（x,y）-> null ;
 *
 * Lambda 表达式 需要"函数式接口"支持
 * 函数式接口:接口中只有一个抽象方法的接口,称为函数式接口，使用注解：@FunctionalInterface 可以检查是否是函数式接口
 *
 * @author 17822
 */
public class LambdaDemo {

    @Test
    public void test1(){

        Runnable runnable = ()->{System.out.println("哈哈哈哈");};
        runnable.run();

        ITest iTest = (name) -> {
            return name;
        };

        System.out.println(iTest.getName("hhhh"));

        Integer count = getCount(100, (i) -> i+10);
        System.out.println(count);

    }

    /**
     * java 8 内置的4大核心函数式接口
     * Consumer<T> 消费型接口：
     *      void accept(T t);
     * Supplier<T> 供给型接口：
     *      T get();
     * Function<T,R> 函数式接口：
     *      R apply(T t);
     * Predicate<T> 断言接口：
     *      boolean test(T t);
     *
     */
    @Test
    public void  functest(){

        //消费型接口
        Consumer<List<String>> consumer = (rows) -> {
            rows.remove(rows.size()-1);
        };
        List<String> li = new ArrayList<>(2);
        li.add("a");
        li.add("b");
        consumer.accept(li);
        System.out.println(Arrays.toString(li.toArray()));

        //供给型接口
        Supplier<String> supplier = () -> "哈哈哈啊";
        String s = supplier.get();
        System.out.println(s);

        //函数式接口
        Function<String, HashMap<String,Object>> function = (key) -> {
          HashMap<String,Object> result = new HashMap<>();
          result.put(key,"了考试解法");
          return result;
        };
        HashMap<String, Object> hp = function.apply("admin");
        System.out.println(hp);

        //断言接口
        Predicate<Integer> predicate = i -> i>100;
        boolean flag = predicate.test(220);
        System.out.println(flag);

    }

    /**
     * 方法引用：若lambda 体中的内容有方法实现，可以使用方法引用
     * 对象::实例方法名
     * 类::静态方法
     * 类::实例方法名
     *
     * 前提条件：Lambda 体中调用方法的参数列表和返回值类型，必须跟函数式接口中的抽象方法的参数列表和返回值类型一致
     *
     * 构造器引用：ClassName::new
     * 注意：需要调用的构造器的参数列表要与函数式接口中抽象方法的参数列表保持一致
     *
     * 数组引用：Type[]::new
     */
    @Test
    public void methods(){

        Consumer<String> consumer = System.out::println;
        consumer.accept("哈哈哈哈");

        Supplier<Integer> supplier = "hhhh"::hashCode;
        Integer integer = supplier.get();
        Optional.of(integer).ifPresent(System.out::println);

        //构造器引用
        Supplier<String> supplier1 = String::new;
        int length = supplier1.get().length();
        Optional.of(length).ifPresent(System.out::println);

        //数组引用
        Function<Integer,String[]> function = String[]::new;
        Optional.of(function.apply(12).length).ifPresent(System.out::println);

    }


    public Integer getCount(Integer i,FunNumCount funNumCount){
        return funNumCount.count(i);
    }

}

@FunctionalInterface
interface FunNumCount{
    Integer count(Integer integer);
}

@FunctionalInterface
interface ITest{
    String getName(String name);
}

