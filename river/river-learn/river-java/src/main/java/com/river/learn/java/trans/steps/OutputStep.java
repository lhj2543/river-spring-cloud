package com.river.learn.java.trans.steps;

import com.river.learn.java.trans.metas.StepMeta;

/**
 * @author 17822
 */
public class OutputStep extends StepMeta {
    
    @Override
    protected boolean process() throws Exception {
        System.out.println("输出节点");
        return true;
    }

}
