package com.river.learn.java.reflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * java 反射机制
 * 反射：将类的各个组成部分封装为其他对象，
 * 好处：可以在程序运行过程中，操作这些对象，可以解耦，提高程序的可可扩展性
 * @author 17822
 */
public class ReflexTest {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        /**
         * 获取Class对象的三种方式：
         * 1：Class.forName('全类名')：将字节码（*。class）文件加载进内存中，返回Class对象，（多用于配置文件，将类命定义在配置文件中，通过文件加载类）
         * 2：类名.class 通过类名属性class获取（多用于参数传递）
         * 3：对象.getCalss()  多用于对象的获取字节码方式
         * 结论：
         *      同一个字节码（*。Class）在一次程序运行过程中，只会被加载一次，不论通过那种方式加载Class对象，都是同一个
         */
        Class<?> personClass1 = Class.forName("com.lhj.java.reflex.Person");
        Class<Person> personClass2 = Person.class;
        Class<? extends Person> personClass3 = new Person().getClass();

        System.out.println(personClass3);
        System.out.println(personClass1==personClass2);
        System.out.println(personClass1==personClass3);

        /**
         * Class对象功能
         * 获取类的成员变量
         */
        // 获取类的所有public修饰的成员变量
        Field[] fields = personClass1.getFields();
        System.out.println(Arrays.toString(fields));
        //通过属性命获取类的public修饰的成员变量
        Field name = personClass1.getField("addr");
        System.out.println(name);
        //获取类的所有成员变量
        Field[] declaredFields = personClass1.getDeclaredFields();
        System.out.println(Arrays.toString(declaredFields));
        //通过属性命获取类的成员变量
        Field declaredField = personClass1.getDeclaredField("name");
        System.out.println(declaredField);


        //暴力反射（忽略访问权限（private protected default）修饰的安全性检查）
        declaredField.setAccessible(true);
        //设置成员变量的值
        Person person = new Person();
        declaredField.set(person,"地址a");
        person.toString();
        //获取成员变量的值
        Object o = declaredField.get(person);
        System.out.println(o);

        /**
         * 获取构造方法
         */
        //直接调用构造无参构造方法
        Object o1 = personClass1.newInstance();
        o1.toString();
        //获取public修饰的构造方法
        Constructor<?> constructor = personClass1.getConstructor(String.class,int.class);
        Constructor<?>[] constructors = personClass1.getConstructors();
        constructor.newInstance("张三",22);
        //获取构造方法（所有构造方法都可以获取没有修饰符限制）
        Constructor<?> declaredConstructor = personClass1.getDeclaredConstructor(String.class, int.class);
        Constructor<?>[] declaredConstructors = personClass1.getDeclaredConstructors();
        //暴力反射
        declaredConstructor.setAccessible(true);

        /**
         * 获取类方法
         */
        //获取public修饰的方法
        Method method = personClass1.getMethod("getName");
        Method[] methods = personClass1.getMethods();
        //获取方法,包含了Object类的所有方法（所有方法都可以获取没有修饰符限制）
        Method declaredMethod = personClass1.getDeclaredMethod("getName");
        Method[] declaredMethods = personClass1.getDeclaredMethods();
        //暴力反射
        declaredMethod.setAccessible(true);

        //执行方法
        Object invoke = declaredMethod.invoke(person);
        System.out.println(invoke);

    }

}


class Person{
    private String name;
    public String addr;

    private int age;
    boolean flag;
    char aChar;
    short aShort;
    long aLong;
    byte aByte;
    float aFloat;
    double aDouble;

    protected  char[] chars;

    Role role;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        String a= "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", addr='" + addr + '\'' +
                ", flag=" + flag +
                ", chars=" + Arrays.toString(chars) +
                '}';
        System.out.println(a);
        return a;
    }
}

class Role{
    private  String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}