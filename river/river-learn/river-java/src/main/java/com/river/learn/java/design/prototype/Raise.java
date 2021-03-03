package com.river.learn.java.design.prototype;
import java.io.*;

/**
 * 原型模式
 * 基本介绍
 * 1) 原型模式(Prototype模式)是指：用原型实例指定创建对象的种类，并且通过拷 贝这些原型，创建新的对象
 * 2) 原型模式是一种创建型设计模式，允许一个对象再创建另外一个可定制的对象， 无需知道如何创建的细节
 * 3) 工作原理是:通过将一个原型对象传给那个要发动创建的对象，这个要发动创建 的对象通过请求原型对象拷贝它们自己来实施创建，即 对象.clone()
 */

/**
 * 浅拷贝和深拷贝
 * 浅拷贝的介绍
 * 1) 对于数据类型是基本数据类型的成员变量，浅拷贝会直接进行值传递，也就是将 该属性值复制一份给新的对象。
 * 2) 对于数据类型是引用数据类型的成员变量，比如说成员变量是某个数组、某个类 的对象等，那么浅拷贝会进行引用传递，
 * 也就是只是将该成员变量的引用值（内 存地址）复制一份给新的对象。因为实际上两个对象的该成员变量都指向同一个
 * 实例。在这种情况下，在一个对象中修改该成员变量会影响到另一个对象的该成 员变量值
 * 4) 浅拷贝是使用默认的 clone()方法来实现 sheep = (Sheep) super.clone();
 */

/**
 * 深拷贝实现方式
 * 1：重写clone方法来实现深拷贝
 * 2：通过对象序列化实现深拷贝(推荐)
 */

/**
 * 原型模式的注意事项和细节
 * 1) 创建新的对象比较复杂时，可以利用原型模式简化对象的创建过程，同时也能够提 高效率
 * 2) 不用重新初始化对象，而是动态地获得对象运行时的状态
 * 3) 如果原始对象发生变化(增加或者减少属性)，其它克隆对象的也会发生相应的变化， 无需修改代码
 * 4) 在实现深克隆的时候可能需要比较复杂的代码
 * 5) 缺点：需要为每一个类配备一个克隆方法，这对全新的类来说不是很难，但对已有 的类进行改造时，需要修改其源代码，违背了ocp原则
 */

class test{
    public static void main(String[]args){
        Types type = new Types("杂种");
        Raise r = new Raise("绵羊",12,type);
        Raise r2 = r.cone();

        System.err.println(r.getType().hashCode());
        System.err.println(r2.getType().hashCode());

        Raise2 rr = new Raise2("绵羊2",122,type);
        Raise2 rr2 = rr.cone();
        System.err.println(rr.getType().hashCode()+"=========="+rr2.getType().hashCode());

    }
}

/**
 * 浅拷贝
 */
public class Raise implements Cloneable {
    private String Name;
    private int age;

    private Types type;

    public Raise(){};
    public Raise(String name, int age,Types type) {
        Name = name;
        this.age = age;
        this.type = type;
    }

    //克隆
    public Raise cone() {
        try {
            return  (Raise) super.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Raise{" +
                "Name='" + Name + '\'' +
                ", age=" + age +
                ", type=" + type +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }
}

/**
 * 深拷贝
 */
class Raise2 implements Serializable,Cloneable {
    private String Name;
    private int age;

    private Types type;

    public Raise2(){};
    public Raise2(String name, int age,Types type) {
        Name = name;
        this.age = age;
        this.type = type;
    }

    //深拷贝1 实现Serializable接口，通过序列化反序列化实现
    public Raise2 cone() {

        ByteArrayOutputStream bOut = null;
        ObjectOutputStream oOut = null;

        ByteArrayInputStream bIn = null;
        ObjectInputStream oIn = null;

        try {
            //序列化
            bOut = new ByteArrayOutputStream();
            oOut = new ObjectOutputStream(bOut);
            oOut.writeObject(this);

            //反序列化
            bIn = new ByteArrayInputStream(bOut.toByteArray());
            oIn = new ObjectInputStream(bIn);
            Raise2 o = (Raise2)oIn.readObject();
            return o;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //深拷贝2 实现Cloneable接口，重写clone 方法
    public Raise2 cone2() {
        try {
            Raise2 r =   (Raise2) super.clone();
            //r.setType(r.getType().clone());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Raise{" +
                "Name='" + Name + '\'' +
                ", age=" + age +
                ", type=" + type +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }
}
