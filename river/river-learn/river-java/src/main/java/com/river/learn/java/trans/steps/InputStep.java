package com.river.learn.java.trans.steps;

import com.river.learn.java.trans.metas.StepMeta;

/**
 * @author 17822
 */
public class InputStep extends StepMeta {

    @Override
    protected boolean process() throws Exception {

        System.out.println("输入节点");
        return true;
    }

}
