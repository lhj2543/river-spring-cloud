package com.river.learn.java.design.state;

/**
 * @author 17822
 */
public class Approval extends BaseAbstractState {

    ActiveContext activeContext;

    Approval(ActiveContext activeContext){
        this.activeContext = activeContext;
    }

    @Override
    public Boolean approval() {
        System.err.println("审批案件。。。");
        activeContext.setState(activeContext.getEnd());
        return true;
    }

}
