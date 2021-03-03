package com.river.learn.java.base;

/**
 * 内部类
 * @author 17822
 */
public class InnerClass {

     protected  String name = "外部类名称";

     public void a(){
         System.out.println("aaaaaaa");
     }

      protected  class User{

        String name = "成员内部类名称";

        String a;

        public void test(){
            System.out.println(InnerClass.this.name);
            System.out.println(this.name);

        }

    }

    public static void main(String[] args) {

        InnerClass.User u = new InnerClass().new User();

    }

}

class Test2 extends InnerClass{


    public void  test(){



    }

}
