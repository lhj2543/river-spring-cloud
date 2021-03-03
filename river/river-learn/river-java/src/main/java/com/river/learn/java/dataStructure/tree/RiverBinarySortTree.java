package com.river.learn.java.dataStructure.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 二叉排序树
 */
public class RiverBinarySortTree<E> {

    private Note<E> root;

    public Note<E> getRoot(){
        return root;
    }

    /**
     * 添加节点
     * @param data
     */
    public void add(E data){

        if(root == null){
            root = new Note<>(data,null,null,null);
            return;
        }

        Note<E> t = root;
        Note<E> parent;
        int cmp;
        //遍历寻找当前插入的节点的父节点
        do {
            parent  = t;
            cmp = ((Comparable<E>) data).compareTo(parent.getData());
            if(cmp<0){
                t = t.leftNode;
            }else{
                t = t.rightNode;
            }
        }while ( t != null );

        Note<E> note = new Note<E>(data,parent);
        if(cmp<0){
            parent.leftNode = note;
        }else {
            parent.rightNode = note;
        }
    }


    /**
     * 刪除节点
     * @param data
     * @return
     */
    public E remove(E data){



        return null;
    }

    /**
     * 非递归 遍历树 前序遍历
     */
    public void iteration(){
        if(root!=null){
            Stack<Note<E>> stack = new Stack<>();
            stack.add(root);

            while (!stack.isEmpty()){
                Note<E> note = stack.pop();

                System.out.println(note.data);

                if(note.rightNode!=null){
                    stack.add(note.rightNode);
                }

                if (note.leftNode!=null){
                    stack.add(note.leftNode);
                }


            }

            System.out.println("=============前序遍历完了===============");
        }
    }

    /**
     * 递归 遍历树 前序遍历
     */
    public void iterationRecursion(){
        if(root!=null){
            root.iterationPreface(root);
            System.out.println("=============前序遍历完了===============");
        }
    }

    /**
     * 递归 遍历树 中序遍历
     */
    public void iterationMid(){
        if(root!=null){
            root.iterationMid(root);
            System.out.println("=============中序遍历完了===============");
        }
    }


    /**
     * 节点类
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    class Note<E>{

        private E data;

        private Note<E> parentNode;

        private Note<E> leftNode;

        private Note<E> rightNode;

        public Note(E data,Note<E> parentNode){
            this.data = data;
            this.parentNode = parentNode;
        }

        /**
         * 递归遍历树，前序遍历
         */
        public void iterationPreface(Note<E> root){

            E rootData = root.getData();
            System.out.println(rootData);

            if(root.leftNode!=null){
                iterationPreface(root.getLeftNode());
            }
            if(root.rightNode!=null){
                iterationPreface(root.getRightNode());
            }
        }
        /**
         * 递归遍历树，中序遍历
         */
        public void iterationMid(Note<E> root){

            if(root.leftNode!=null){
                iterationMid(root.getLeftNode());
            }

            E rootData = root.getData();
            System.out.println(rootData);

            if(root.rightNode!=null){
                iterationMid(root.getRightNode());
            }

        }

    }



}



