package com.river.learn.java.collection;

/**
 * 自定义简单 单向LinkedList
 * @author 17822
 */
public class CustomLinkedList<E> {


    int size;

    private Node<E> last;

    private Node<E> firstNode;

    public void add(E e){
        Node newNode = new Node(e, null);
        if(this.last !=null){
            this.last.next = newNode;
        }else {
            this.firstNode = newNode;
        }
        this.last = newNode;
        this.size ++ ;
    }

    public void addFirst(E e){
        if(this.firstNode==null){
            this.add(e);
            return;
        }
        Node newNode = new Node(e, this.firstNode);
        this.firstNode = newNode;
        this.size ++ ;
    }

    public  E  remove(){
        if(this.firstNode==null){
            return null;
        }
        E a = this.firstNode.value;
        this.firstNode = this.firstNode.next;
        this.size -- ;
        return a;
    }

    /**
     * 单向链表实现反转
     * 思路：
     * 1：定义一个变量(topNode)存储最顶端的节点，
     * 2：循环链表,把当前节点的下一个指针指向最顶端的节点（也就是变量topNode），然后把当前节点变为最顶端节点
     * 3：把链表头替换成顶端节点
     *
     * @return
     */
    public void reversal(){

        if(firstNode==null || firstNode.next==null){
            return;
        }

        //存储最顶端节点
        Node topNode = new Node(firstNode.value,null);

        //临时存储当前节点的下一个节点
        Node nextNode = null;

        Node node = firstNode.next;
        while (node!=null){
            //临时存储当前节点的下一个节点
            nextNode = node.next;

            //当前节点的下一个节点指向最顶端节点
            node.next = topNode;
            //每循环一次，最顶端的节点变为当前节点
            topNode = node;

            node = nextNode;
        }

        this.firstNode = topNode;

    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        if(size<=0){
            result.append("[]");
            return result.toString();
        }

         Node fn = this.firstNode;
        while (fn != null){
            //System.out.println(fn.value);
            result.append(fn.value + ",");
            fn = fn.next;
        }

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

}

class test{

    public static void main(String[] args) {

        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>();
        customLinkedList.add("k");
        customLinkedList.add("t");
        customLinkedList.add("a");
        customLinkedList.add("Y");
        customLinkedList.add("aslkfa");
        customLinkedList.addFirst("第一个");

        /*String remove = customLinkedList.remove();
        System.out.println(remove);*/

        //customLinkedList.addFirst("fff");

        System.out.println(customLinkedList.size);

        System.out.println(customLinkedList.toString());

        customLinkedList.reversal();

        System.out.println(customLinkedList.toString());

    }

}
