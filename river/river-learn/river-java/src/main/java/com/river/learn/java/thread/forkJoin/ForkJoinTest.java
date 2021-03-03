package com.river.learn.java.thread.forkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoin框架用于并行执行任务，它的思想就是讲一个大任务分割成若干小任务，最终汇总每个小任务的结果得到这个大任务的结果。
 * ForkJoinPool
 *  既然任务是被逐渐的细化的，那就需要把这些任务存在一个池子里面，这个池子就是ForkJoinPool，它与其它的ExecutorService区别主要在于它使用“工作窃取
 * ForkJoinTask就是ForkJoinPool里面的每一个任务。他主要有两个子类：RecursiveAction和RecursiveTask。然后通过fork()方法去分配任务执行任务，通过join()方法汇总任务结果，
 *
 * （1）RecursiveAction 一个递归无结果的ForkJoinTask（没有返回值）
 * （2）RecursiveTask 一个递归有结果的ForkJoinTask（有返回值）
 * @author 17822
 */
public class ForkJoinTest {

    public static void main(String[] args) {
        int[]  array = new int[100];
        ForkJoinPool forkJoinPool= new ForkJoinPool();
        SumTask sumTask  = new SumTask(0,array.length-1,array);

        long start = System.currentTimeMillis();

        forkJoinPool.invoke(sumTask);
        Integer join = sumTask.join();
        System.out.println("The count is "+join+" spend time:"+(System.currentTimeMillis()-start)+"ms");
    }

    private static class SumTask extends RecursiveTask<Integer> {

        private  int threshold ;
        private static final int segmentation = 10;

        private int[] src;

        private int fromIndex;
        private int toIndex;

        public SumTask(int formIndex,int toIndex,int[] src){
            this.fromIndex = formIndex;
            this.toIndex = toIndex;
            this.src = src;
            this.threshold = src.length/segmentation;
        }

        @Override
        protected Integer compute() {
            if((toIndex - fromIndex)<threshold ){
                int count = 0;
                System.out.println(" from index = "+fromIndex+" toIndex="+toIndex);
                for(int i = fromIndex;i<=toIndex;i++){
                    count+=src[i];
                }
                return count;
            }else{
                int mid = (fromIndex+toIndex)/2;
                SumTask left =  new SumTask(fromIndex,mid,src);
                SumTask right = new SumTask(mid+1,toIndex,src);
                invokeAll(left,right);
                return left.join()+right.join();
            }
        }
    }

}

