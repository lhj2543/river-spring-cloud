package com.river.learn.java.base;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

public class javaIterview {

    public static void main(String[] args) {

        String a = "";

        System.out.println(test3());

    }

    public static int test3(){

        int k = 0;
        try {
            return k;
        }catch (Exception e){

        }finally {
            ++k;
            System.out.println("执行finally"+"======= k="+k);
        }

        return 1;
    }

    /**
     * 如何在一次遍历中找到单个链表的中值?
     * 快指针每次走2个结点，慢指针每次走1个结点，当快指针走完链表，慢指针刚好走到中间。
     * 注意：
     * 当结点数是奇数时，慢指针走到中间结点，当结点数是偶数时，此时中间结点有2个，此时慢指针指向靠前那个结点。
     * 例如：1 3 5 7 9 快指针第一次走到 5 ，第二次走到 9 然后链表走完，慢指针走2步 刚好走到中间结点5。
     * 再例如：1 3 5 7 快指针第一次走到 5 ，第二次越界只走一步即链表走完，而慢指针也走一步即走到3即走完，那么3就是那个慢指针指向靠前那个结点。
     */
    public void test2(){

    }

    /**
     * 如何在一个 1 到 100 的整数数组中找到丢失的数字?
     */
    public void  test1(){

        int size = 10;

        BitSet bitSet = new BitSet(size);

        int[] i = new int[]{3,7,6,8,10};
        for(int j:i){
            bitSet.set(j);
        }


        int k = 1;
        while (k<=size){
            k = bitSet.nextClearBit(k);
            if(k<=size){
                System.out.println(k);
            }
            k++;
        }
    }

}
