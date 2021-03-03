package com.river.learn.java.design.single;


/**
 * 单例设计模式：饿汉式（静态常量）
 * 1) 构造器私有化 (防止 new )
 * 2) 类的内部创建对象
 * 3) 向外暴露一个静态的公共方法。getInstance
 */

/**
 * 优缺点说明：
 * 1) 优点：这种写法比较简单，就是在类装载的时候就完成实例化。避免了线程同 步问题。
 * 2) 缺点：在类装载的时候就完成实例化，没有达到Lazy Loading的效果。如果从始 至终从未使用过这个实例，则会造成内存的浪费
 * 3) 这种方式基于classloder机制避免了多线程的同步问题，不过，instance在类装载 时就实例化，在单例模式中大多数都是调用getInstance方法，
 *    但是导致类装载 的原因有很多种，因此不能确定有其他的方式（或者其他的静态方法）导致类 装载，这时候初始化instance就没有达到lazy loading的效果
 * 4) 结论：这种单例模式可用，可能造成内存浪费
 */
public class Hungry {

    private static Hungry instance = new Hungry();

    //构造器私有化 (防止 new )
    private Hungry(){}

    /**
     * 向外暴露一个静态的公共方法
     * @return
     */
    public static Hungry getInstance(){
        return instance;
    };

    public static void main(String[] args) {
        Hungry hungry = Hungry.getInstance();
        Hungry hungry2 = Hungry.getInstance();
        System.err.println(hungry.hashCode()==hungry2.hashCode());

    }

}

/**
 * 单例设计模式：饿汉式（静态代码块）
 *优缺点说明：
 * 1) 这种方式和上面的方式其实类似，只不过将类实例化的过程放在了静态代码块 中，也是在类装载的时候，就执行静态代码块中的代码，初始化类的实例。
 * 优 缺点和上面是一样的。
 * 2) 结论：这种单例模式可用，但是可能造成内存浪费
 */
class Hungry2{

    private static Hungry2 instance;

    //在静态代码块执行时，创建单例对象
    static {
        instance = new Hungry2();
    }

    //构造器私有化 (防止 new )
    private Hungry2(){}

    /**
     * 向外暴露一个静态的公共方法
     * @return
     */
    public static Hungry2 getInstance(){
        return instance;
    };

}


