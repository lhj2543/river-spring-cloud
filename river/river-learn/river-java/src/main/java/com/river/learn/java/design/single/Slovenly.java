package com.river.learn.java.design.single;


/**
 * 单例设计模式：懒汉式(线程安全 双重检查)
 *
 * 优缺点说明：
 * 1) Double-Check概念是多线程开发中常使用到的，如代码中所示，我们进行了两 次if (singleton == null)检查，这样就可以保证线程安全了。
 * 2) 这样，实例化代码只用执行一次，后面再次访问时，判断if (singleton == null)， 直接return实例化对象，也避免的反复进行方法同步.
 * 3) 线程安全；延迟加载；效率较高
 * 4) 结论：在实际开发中，推荐使用这种单例设计模式
 */
public class Slovenly {

    private static volatile Slovenly instance;

    //构造器私有化 (防止 new )
    private Slovenly(){}

    /**
     * 向外暴露一个静态的公共方法
     * 加入双重检查，解决线程安全问题，同时解决懒加载问题
     * @return
     */
    public static Slovenly getInstance(){
        if(instance==null){
            synchronized (Slovenly.class){
                if(instance==null){
                    instance = new Slovenly();
                }
            }
        }
        return instance;
    };

    public static void main(String[] args) {

        for (int i=1;i<1000;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Slovenly instance = Slovenly.getInstance();
                    System.err.println(instance.hashCode());
                }
            }).start();
        }
    }
}


/**
 * 单例设计模式：懒汉式(线程不安全)
 *
 * 优缺点说明：
 * 1) 起到了Lazy Loading的效果，但是只能在单线程下使用。
 * 2) 如果在多线程下，一个线程进入了if (singleton == null)判断语句块，还未来得及 往下执行，另一个线程也通过了这个判断语句，这时便会产生多个实例。
 * 所以 在多线程环境下不可使用这种方式 3
 * 3) 结论：在实际开发中，不要使用这种方式
 */
class Slovenly1 {

    private static Slovenly1 instance;

    //构造器私有化 (防止 new )
    private Slovenly1(){}

    /**
     * 向外暴露一个静态的公共方法
     * @return
     */
    public static Slovenly1 getInstance(){
        if(instance==null){
            instance = new Slovenly1();
        }
        return instance;
    };

}

/**
 * 单例设计模式：懒汉式(线程安全，同步方法)
 *
 * 优缺点说明：
 * 1) 解决了线程不安全问题
 * 2) 效率太低了，每个线程在想获得类的实例时候，执行getInstance()方法都要进行 同步。
 * 而其实这个方法只执行一次实例化代码就够了，后面的想获得该类实例， 直接return就行了。方法进行同步效率太低
 * 3) 结论：在实际开发中，不推荐使用这种方式
 */
class Slovenly2 {

    private static Slovenly2 instance;

    //构造器私有化 (防止 new )
    private Slovenly2(){}

    /**
     * 向外暴露一个静态的公共方法
     * 加入同步代码块，解决线程安全问题
     * @return
     */
    public static synchronized Slovenly2 getInstance(){
        if(instance==null){
            instance = new Slovenly2();
        }
        return instance;
    };

}

/**
 * 单例设计模式：懒汉式(线程安全，同步代码块)
 *
 * 优缺点说明：
 * 1) 解决了线程不安全问题
 * 2) 效率太低了，每个线程在想获得类的实例时候，执行getInstance()方法都要进行 同步。
 * 而其实这个方法只执行一次实例化代码就够了，后面的想获得该类实例， 直接return就行了。方法进行同步效率太低
 * 3) 结论：在实际开发中，不推荐使用这种方式
 */
class Slovenly3 {

    private static Slovenly3 instance;

    //构造器私有化 (防止 new )
    private Slovenly3(){}

    /**
     * 向外暴露一个静态的公共方法
     * @return
     */
    public static  Slovenly3 getInstance(){
        if(instance==null){

            synchronized(Slovenly3.class){
                instance = new Slovenly3();
            }

        }
        return instance;
    };

}
