package com.river.learn.java.design.state;

/**
 * @author 17822
 */
public class Apply extends BaseAbstractState {

    ActiveContext activeContext;

    Apply(ActiveContext activeContext){
        this.activeContext = activeContext;
    }

    @Override
    public Boolean apply() {
        System.err.println("发起申请。。");
        activeContext.setState(activeContext.getApproval());
        return true;
    }

}
