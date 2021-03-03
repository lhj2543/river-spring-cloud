package com.river.learn.java.dataStructure;

import java.util.Stack;

/**
 * 单向环形链表
 * @author 17822
 */
public class RiverAnnularLinked<E> {


    int size;

    private Node<E> last;

    private Node<E> firstNode;

    public void add(E e){

        if(firstNode == null){
            firstNode = new Node(e, null);
            firstNode.next = firstNode;
            this.last = firstNode;
        }else {
            Node newNode = new Node(e, firstNode);
            this.last.next = newNode;
            this.last = newNode;
        }

        this.size ++ ;
    }

    public  E  remove(){

        return null;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        if(size<=0){
            result.append("[]");
            return result.toString();
        }

         Node fn = this.firstNode;
        do {
            //System.out.println(fn.value);
            result.append(fn.value + ",");
            fn = fn.next;
        }while (fn != this.firstNode);

        return  result.toString();
    }


    /**
     * 节点
     * @param <E>
     */
    private class Node<E>{

        Node(E value, Node<E> next){
            this.value = value;
            this.next = next;
        }

        public E value;

        public  Node<E> next;

    }


    public static void main(String[] args) {
        RiverAnnularLinked<String> linked = new RiverAnnularLinked<>();

        linked.add("a");
        linked.add("b");
        linked.add("c");

        System.out.println(linked.size);

        System.out.println(linked.toString());

    }

}
