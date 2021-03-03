package com.river.learn.java.dataStructure;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * 二分查找
 * @author 17822
 */
public class RiverSearchAlgorithm {

    /**
     * 递归二分查找
     * @return
     */
    public static int binarySearch(int[] data,int left,int right,int find)throws Exception{

        int min = (right - left)/2 + left;

        if(data[min] == find){
            return min;
        }else if(min == left){
            return -1;
        }else if(data[min]>find){
            return binarySearch(data,left,min,find);
        }else if(data[min]<find) {
            return binarySearch(data, min, right, find);
        }

        return -1;

    }

    /**
     * 循环二分查找 (查找多个)
     * @return
     */
    public static ArrayList<Integer> binarySearch(int[] data, int find)throws Exception{

        ArrayList<Integer> findIndexs = new ArrayList<Integer>();
        int left = 0;
        int right = data.length;

        int min = -1;
        boolean flag = true;
        while (flag){

           min = (right - left)/2 + left;
           //插值查找
           //min = (right - left) * ( (find - data[left]) / (data[right]-data[left]) ) + left;

           if(data[min] == find){

               //向左扫描
               int leftIndex = min-1;
               while (true && leftIndex>=0){
                   if(data[leftIndex--] == find){
                       findIndexs.add(leftIndex);
                   }else {
                       break;
                   }
               }
               findIndexs.add(min);
               //向右扫描
               int rifhtIndex = min+1;
               while (true && rifhtIndex<data.length){
                   if(data[rifhtIndex++] == find){
                       findIndexs.add(rifhtIndex);
                   }else {
                       break;
                   }
               }

               break;

           }else if(min == left){
               break;
           }else if(data[min]>find){
               right = min;
           }else if(data[min]<find) {
               left = min;
           }

        }

        return findIndexs;

    }

    public static void main(String[] args) {

        Hashtable hashtable =  new Hashtable();

        int[] data = new int[]{2,5,7,90,90,90,90,90,118,119,129};
        try {
            //int i = binarySearch(data, 0,data.length, 114);
            List<Integer> i = binarySearch(data, 90);
            System.out.println(i);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

}
