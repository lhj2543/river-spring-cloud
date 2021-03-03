package com.river.learn.java.dataStructure.tree;

import lombok.Data;
import org.junit.Test;

import java.util.TreeMap;

public class TreeTest {

    public static void main(String[] args) {

        TreeMap treeMap = new TreeMap();

        User u = new User();
        u.setName("张三");
        Object put = treeMap.put(u, u.getName());

        User u2 = new User();
        u2.setName("李四");
        Object put2 = treeMap.put(u2, u2.getName());

        System.out.println(put2);

        /*treeMap.put("a","aa");
        treeMap.put("b","bb");*/

        System.out.println(treeMap.size());
        System.out.println(treeMap);

        double pow = Math.pow(2, 4);

    }

    /**
     * 测试二叉排序树
     */
    @Test
    public void testBinarySortTree(){

        RiverBinarySortTree<Integer> t =  new RiverBinarySortTree<>();
        t.add(20);
        t.add(27);
        t.add(17);
        t.add(5);
        t.add(77);
        t.add(62);
        t.add(20);
        t.add(19);
        t.add(25);

        RiverBinarySortTree<Integer>.Note<Integer> root = t.getRoot();

        t.iterationRecursion();
        t.iteration();
        t.iterationMid();

    }

    @Data
    static class User implements Comparable<User>{
        private int id;
        private String name;

        @Override
        public int compareTo(User o) {
            return this.id-o.getId();
        }

    }

}
