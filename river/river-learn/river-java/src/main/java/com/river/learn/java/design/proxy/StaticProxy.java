package com.river.learn.java.design.proxy;

/**
 * 代理模式的实现
 *
 * 　　代理模式很简单，只要记住以下关键点，简单易实现：
 *
 * 　　　　（1）代理类与委托类实现同一接口
 *
 * 　　　　（2）在委托类中实现功能，在代理类的方法中中引用委托类的同名方法
 *
 * 　　　　（3）外部类调用委托类某个方法时，直接以接口指向代理类的实例，这正是代理的意义所在：屏蔽。
 *
 * 代理模式的应场景
 *
 *  如果已有的方法在使用的时候需要对原有的方法进行改进，此时有两种办法：
 *
 * 1. 修改原有的方法来做到改进。但这样违反了“对扩展开放，对修改关闭”的原则。
 * 2. 采用一个代理类调用原有的方法，且对产生的结果进行控制。这就是代理模式。 　　　　
 *
 *             （1）当我们想要隐藏某个类时，可以为其提供代理类
 *
 * 　　　　（2）当一个类需要对不同的调用者提供不同的调用权限时，可以使用代理类来实现（代理类不一定只有一个，我们可以建立多个代理类来实现，也可以在一个代理类中金进行权限判断来进行不同权限的功能调用）
 *
 * 　　　　（3）当我们要扩展某个类的某个功能时，可以使用代理模式，在代理类中进行简单扩展（只针对简单扩展，可在引用委托类的语句之前与之后进行）
 *
 * 　　代理模式虽然实现了调用者与委托类之间的强耦合，但是却增加了代理类与委托类之间的强耦合（在代理类中显式调用委托类的方法），而且增加代理类之后明显会增加处理时间，拖慢处理时间。
 *
 *
 */

import lombok.Data;

/**
 * 静态代理
 * 由程序员创建代理类或特定工具自动生成源代码再对其编译。再程序运行前代理类的.class文件就已经存在了
 *  静态代理优点：
 * 　　客户端不必知道实现类（委托类）如何如何，只需要调用代理类即可。
 * 缺点：
 *     代理类和委托类实现了相同的接口，代理类通过委托类实现了相同的方法。但这样出现了大量的代码重复。如果接口增加一个方法，除了所有实现类需要实现这个方法外，所有代理类也要实现这个方法。这显然增加了代码的复杂度。
 *     代理对象只服务于一种类型的对象，如果要服务多类型的对象，那就要对每种对象都进行代理。静态代理子啊程序规模稍大是就无法胜任了。
 * @author river
 */
public class StaticProxy {
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setName("张三");

        CatProxy catProxy = new CatProxy(customer);
        catProxy.buyCat();

    }
}

/**
 * 买车接口
 */
interface IBuyCat{
    public void buyCat();
}

/**
 * 委托类
 */
@Data
class Customer implements IBuyCat{

    private String name;

    @Override
    public void buyCat() {
        System.out.println(name  + "买了一辆车。");
    }

}

/**
 * 买车代理类
 */
class CatProxy implements IBuyCat{

    private Customer customer;

    public CatProxy(Customer customer){
        this.customer = customer;
    }

    @Override
    public void buyCat() {
        System.out.println("代理开始");
        customer.buyCat();
        System.out.println("代理结束");
    }

}


