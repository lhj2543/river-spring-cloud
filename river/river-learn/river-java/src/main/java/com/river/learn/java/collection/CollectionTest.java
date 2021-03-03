package com.river.learn.java.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;

/**
 * @author 17822
 */
public class CollectionTest {

    public static void main(String[] args) {

        /*System.out.println(2222);

        Collection list = new ArrayList<String>();
        list.add("a");
        list.add("a");

        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("k");
        linkedList.add("a");
        linkedList.addLast("u");
        linkedList.addFirst("sadfa");

        linkedList.get(2);

        String remove = linkedList.remove();
        System.out.println(remove);

        System.out.println(linkedList);*/

        LinkedList linkedList = new LinkedList();
        linkedList.add("a");
        linkedList.add("b");

        linkedList.add(1,"d");
        linkedList.remove("a");
        linkedList.remove(1);

        HashMap map = new HashMap();


        for (int i= 0;i<100000;i++){

            map.put(i+"",i+"_v");

            if (i==90000){
                System.out.println(1111);
            }

        }


    }
}
