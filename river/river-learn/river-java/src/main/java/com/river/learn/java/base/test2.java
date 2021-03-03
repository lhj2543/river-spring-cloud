package com.river.learn.java.base;

import java.util.BitSet;
import java.util.HashMap;
import java.util.TreeSet;

public class test2 {

    public static void main(String[] args) {

        BitSet bitSet = new BitSet();
        System.out.println(bitSet.size());

        TreeSet<String> set = new TreeSet<>();
        set.add("kk");
        set.add("ee");
        set.add("aa");

        System.out.println(set);

        HashMap map = new HashMap();
        map.put("","");
        map.get("");


    }




    /**
     * 找出 数组中 1-00不存在的数
     */
    public void  test1(){
        BitSet bitSet = new BitSet(10);

        int[] i = new int[]{3,7,6,8,10};
        for(int j:i){
            bitSet.set(j);
        }


        int k = 1;
        while (k<=10){
            k = bitSet.nextClearBit(k);
            if(k<=10){
                System.out.println(k);
            }
            k++;
        }
    }


    static {
        System.out.println("静态代码块");
    }

    static  int i = init();

    public static int init(){
        System.out.println("静态变量赋值");
        return 10;
    }
    public static int init2(){
        System.out.println("非静态变量赋值");
        return 10;
    }
    public test2(){
        System.out.println("构造方法");
    }


    {
        System.out.println("非静态代码块");
    }

    private int k = init2();


}
