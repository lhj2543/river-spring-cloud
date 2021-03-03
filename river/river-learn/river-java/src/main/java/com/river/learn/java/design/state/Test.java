package com.river.learn.java.design.state;

/**
 * @author 17822
 */
public class Test {
    public static void main(String[] args) {

        for (int i=0;i<5;i++){
            int finalI = i;
            Thread t = new Thread(){
                @Override
                public void run() {
                    Test.doTest(finalI);
                }
            };

            t.start();
        }

    }

    private static void  doTest(int i){

        ActiveContext activeContext = new ActiveContext(i);

        activeContext.doApply();

        activeContext.doApproval();

        Class<? extends State> aClass = activeContext.getState().getClass();
        String name = aClass.getName();
        System.err.println(name);

    }

}
