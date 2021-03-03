package com.river.learn.java.dataStructure;

import java.util.Stack;

/**
 * 给定一个数字字符串,运算结果
 * @author 17822
 */
public class StringOperarion {
    public static void main(String[] args) {

        String str = "10+2*8*1-9*9/3+3*2+1+3";

        System.out.println("运算结果=="+ ( 10+2*8*1-9*9/3+3*2+1+3 ));

        int operation = operation(str);

        System.out.println("运算结果2="+ operation);

    }

    /**
     * 给定一个数字字符串,运算结果
     * @param str
     * @return
     */
    public static int operation(String str){

        Stack<Integer> numberStack =  new Stack<Integer>();

        Stack<String> operatorStack =  new Stack<String>();

        char[] chars = str.toCharArray();
        //根据运算符分解字符串
        String baseOprator = "+-*/()";
        int index = 0;

        //String str = "10+2*83/(12-3)";

        for (int i=0;i<chars.length;i++){
            char c = chars[i];

            if(baseOprator.indexOf(String.valueOf(c))>-1 ){
                String StrNumber = str.substring(index, i);
                if(!StrNumber.trim().isEmpty()){
                    Integer number = Integer.valueOf(StrNumber);

                    numberStack.push(number);

                }

                String opt = str.substring(i, i+1);

                if(operatorStack.empty()){
                    operatorStack.push(opt);
                }else {
                    //判断当前运算符级别是否高于前一个运算符级别
                    String popOpt = operatorStack.peek();
                    boolean b = compareLevel(opt, popOpt);
                    if(!b){
                        Integer popA = numberStack.pop();
                        Integer popB = numberStack.pop();

                        int computeValue = compute(popB, popA, popOpt);

                        operatorStack.pop();
                        numberStack.push(computeValue);
                    }else {

                    }

                    operatorStack.push(opt);

                }

                index = i+1;

            }

            if (i==chars.length-1){
                Integer number = Integer.valueOf(str.substring(index, chars.length));
                numberStack.push(number);
            }

        }

        System.out.println(numberStack);
        System.out.println(operatorStack);

        int size = operatorStack.size();
        for (int i=0;i<size;i++){
            Integer popA = numberStack.pop();
            Integer popB = numberStack.pop();
            String popOpt = operatorStack.pop();
            int computeValue = compute(popB, popA, popOpt);
            numberStack.push(computeValue);
        }

        return numberStack.pop();
    }

    /**
     * 比较两个操作符优先级
     * @param a
     * @param b
     * @return
     */
    public static boolean compareLevel(String a,String b){
        String[] str = new String[]{"()","*/","+-"};
        int indexA = -1;
        int indexB = -1;
        for (int i=0;i<str.length;i++){
            int i1 = str[i].indexOf(a);
            int i2 = str[i].indexOf(b);
            if(i1>-1 && i2>-1){
                return false;
            }else if(i1>-1){
                indexA = i;
            }else if(i2>-1){
                indexB = i;
            }
        }
        return indexA<indexB;
    }

    /**
     * 计算结果
     * @param a
     * @param b
     * @param opt
     * @return
     */
    public static int compute(Integer a,Integer b,String opt){
        switch (opt){
            case "+":
                return a+b;
            case "-":
                return a-b;
            case "*":
                return a*b;
            case "/":
                return a/b;
            default:
                break;
        }
        return 0;
    }



}
