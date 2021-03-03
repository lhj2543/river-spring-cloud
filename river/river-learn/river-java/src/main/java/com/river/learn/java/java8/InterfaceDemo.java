package com.river.learn.java.java8;


/**
 * java8 接口中允许有默认方法，与静态方法
 * 接口默认方法类优先原则：
 * 若一个接口中定义了一个默认方法，而另一个父类或接口中又定义了同名的方法时：
 *      选择父类方法：
 *      接口冲突，必须重写默认方法
 * @author 17822
 */
public class InterfaceDemo {

    public static void main(String[] args) {
        testImp testImp = new testImp();
        String str = testImp.getName("十六客服的");
        System.out.println(str);

        String s = ItestA.get();
        System.out.println(s);
    }

}

class  testImp implements ItestA,ItestB{

    /**
     * 接口默认方法冲突，必须重写默认方法
     */
    @Override
    public String getName(String name) {
        return ItestB.super.getName(name);
    }

    @Override
    public void test() {

    }
}


interface ItestA{

    default String getName(String name){
        return name;
    }

    static String get(){
        return "张三";
    };

    void test();

}

interface ItestB{

    static String getName(){
        return "李四";
    };

    default String getName(String name){
        return name+"————所属";
    }

}
