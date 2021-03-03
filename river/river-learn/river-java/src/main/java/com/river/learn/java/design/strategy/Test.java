package com.river.learn.java.design.strategy;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author 17822
 */
public class Test {
    public static void main(String[] args) {

        OptA a = new OptA(new StrategyAImp());
        a.opt1();

        Integer[] i = new Integer[]{5,7,8,2,9};
        Comparator<Integer> comparator = new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        Arrays.sort(i);
        Arrays.sort(i,comparator);

        System.out.println(Arrays.toString(i));


    }
}
