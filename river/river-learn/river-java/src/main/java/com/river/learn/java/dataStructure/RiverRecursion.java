package com.river.learn.java.dataStructure;

/**
 * 递归
 * 但递归是用栈机制实现的，每深入一层，都要占去一块栈数据区域，对嵌套层数深的一些算法，递归会力不从心，
 * 空间上会以内存崩溃而告终，而且递归也带来了大量的函数调用，这也有许多额外的时间开销。所以在深度大时，它的时空性就不好了。（会占用大量的内存空间
 *
 * 能不用递归就不用递归，递归都可以用迭代来代替。
 * @author 17822
 */
public class RiverRecursion {

    public static void main(String[] args) {
        int value = factorial(4);
        System.out.println(value);
    }

    /**
     * 阶乘
     * 4 的阶乘 = 1*2*3*4
     * n 的阶乘 n1= 1*2*3...*n
     */
    public static int factorial(int n){
        if (n==1){
            return 1;
        }else {
           return factorial(n-1) * n;
        }
    }

}
