package com.river.learn.java.trans.metas;

import com.river.learn.java.trans.steps.InputStep;

/**
 * @author 17822
 */
public class TransMeta {

    private StepMeta stepMeta;

    public void start(){
        new Thread(stepMeta).start();
    }

    public StepMeta initStep(){
        stepMeta = new InputStep();
        return stepMeta;
    }

    public static void main(String[] args) {
        TransMeta transMeta = new TransMeta();
        transMeta.initStep();
        transMeta.start();
    }



}
