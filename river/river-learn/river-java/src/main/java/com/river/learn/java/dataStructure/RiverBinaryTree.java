package com.river.learn.java.dataStructure;

import cn.hutool.core.lang.tree.TreeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

class Test{
    public static void main(String[] args) {

        HashMap map =  new HashMap();
        //map.put("a","傲世狂妃多久啊快乐十分");
        map.put(1,1);

        int i = 2^1;
        System.out.println(i);

        Integer.parseInt("100",2);

        Note<String> a = new Note<>("a");
        Note<String> b = new Note<>("b");
        Note<String> c = new Note<>("c");
        Note<String> d = new Note<>("d");
        Note<String> e = new Note<>("e");
        Note<String> f = new Note<>("f");
        Note<String> g = new Note<>("g");
        Note<String> k = new Note<>("k");

        RiverBinaryTree<String> tree = new RiverBinaryTree<>();
        tree.setRoot(a);

        a.setLeftNode(b);
        a.setRightNode(d);

        b.setLeftNode(c);
        b.setRightNode(k);

        d.setLeftNode(e);
        d.setRightNode(f);

        e.setLeftNode(g);

        //tree.iteration();

    }


}

/**
 * 二叉树
 * @author 17822
 */
@Data
public class RiverBinaryTree<E> {

    /**
     * 根节点
     */
    private Note<E> root;

    /**
     * 迭代树
     */
    public void iteration(){
        if(root!=null){
            root.iteration(root);
        }
    }

}

/**
 * 节点类
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class Note<E>{

    private E data;

    private Note leftNode;

    private Note rightNode;

    public Note(E data){
        this.data = data;
    }

    /**
     * 遍历树，前序遍历
     */
    public void iteration(Note<E> root){

        E rootData = root.getData();
        System.out.println(rootData);

        if(root.leftNode!=null){
            iteration(root.leftNode);
        }
        if(root.rightNode!=null){
            iteration(root.rightNode);
        }
    }

}
