package com.river.learn.java.dataStructure;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ddf.EscherColorRef;

import java.util.Arrays;
import java.util.Random;

/**
 * 排序算法
 * @author 17822
 */
public class RiverSort {

    public static void main(String[] args) {

        int length = 1000000;
        Random random = new Random(System.currentTimeMillis());
        int[] data = new int[length];
        for (int i=0;i<length;i++){
            data[i] = random.nextInt(length*10);
        }

        long startDate = System.currentTimeMillis();

        //bubblingSort(data);
        //sort2(data);
        //radixSort(data);

        jishuSort(data);

        long endDate = System.currentTimeMillis();

        System.out.println("共用时："+(endDate-startDate)+"ms");

        //System.out.println(Arrays.toString(data));

    }

    /**
     * 冒泡排序
     * 比较两个相邻的元素，将值大的元素交换到右边
     * @param data
     */
    public static void bubblingSort(int[] data){

        int temp = 0;
        boolean flag = false;

        for (int i = 0;i<data.length;i++){

            for (int j=0;j<data.length-i-1;j++){
                if(data[j]>data[j+1]){
                    flag = true;
                    temp = data[j+1];
                   data[j+1] = data[j];
                   data[j] = temp;
                }
            }

            if (!flag){
                break;
            }else {
                flag = false;
            }

        }

    }

    /**
     * 选择排序
     * 找出最小，最多值，然后插入到数组最左最右
     * @param data
     */
    public static void  sort2(int[] data){

        int min = 0;
        int max = 0;
        int temp = 0;
        int minIndex = 0;
        int maxIndex = 0 ;

        for (int i = 1;i<=(data.length/2);i++){

            int l = data.length-i*2+i;

            min = data[i-1];
            max = data[l];
            minIndex = i-1;
            maxIndex = l;
            if(min>max){
                temp = min;
                min = max;
                max =temp;
                minIndex = l;
                maxIndex = i-1;
            }


            for (int j=i;j<l;j++){

                if(min > data[j]){
                    min = data[j];
                    minIndex = j;
                }else if(max<data[j]) {
                    max = data[j];
                    maxIndex = j;
                }

            }

            data[minIndex] = data[i-1];
            data[maxIndex] = data[l];
            data[i-1] = min;
            data[l] = max;

        }

    }

    /**
     * 插入排序
     * @param data
     */
    public static void  sort3(int[] data){


    }

    /**
     * 基数排序
     * @param str
     */
    public static void radixSort(int[] str) {

        // 桶  10个桶  每个桶的最大容量默认为数组长度
        int[][] bucket = new int[10][str.length];
        // 每个桶的当前容量
        int[] capacity = new int[10];

        //元素求出最大数
        int max = str[0];
        for (int r = 0; r < str.length; r++) {
            if (str[r] > max) {
                max = str[r];
            }
        }
        //求出最大长度 用于判断循环几大轮
        int length = (max + "").length();

        //取得（个位 十位 百位。。。。）基数
        for (int b= 0,u=1; b < length; b++,u*=10) {
            for (int i = 0; i < str.length; i++) {
                //比如基数为 4
                int base = str[i] /u % 10;
                //将基数按照规则放进桶中
                //放进第四个桶中 的第一几个当前容量位置
                bucket[base][capacity[base]] = str[i];
                //容量增加
                capacity[base]++;
            }

            // 取出数据
            int d = 0;
            for (int k = 0; k < capacity.length; k++) {
                if (capacity[k] != 0) {
                    for (int p = 0; p < capacity[k]; p++) {
                        str[d] = bucket[k][p];
                        d++;
                    }
                }
                //注意：清零
                capacity[k] = 0;
            }
        }
    }

    /**
     * 计数排序
     * @param data
     */
    public static void jishuSort(int[] data){

        int max = data[0];
        for (int i = 0;i<data.length;i++){
            max = data[i]>max?data[i]:max;
        }

        int[] jishu = new int[max+1];

        for (int i = 0;i<data.length;i++){
            jishu[data[i]] = jishu[data[i]]+1;
        }

        int index = 0;

        for (int i = 0;i<jishu.length;i++){

           if (jishu[i]!=0){
               for (int j=0;j<jishu[i];j++){
                   data[index++] = i;
               }
           }

        }

    }

}
