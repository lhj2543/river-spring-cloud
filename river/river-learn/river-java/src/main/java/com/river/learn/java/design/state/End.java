package com.river.learn.java.design.state;

/**
 * @author 17822
 */
public class End extends BaseAbstractState {

    @Override
    public Boolean end() {
        System.err.println("案件完了。。。");
        return true;
    }

}
