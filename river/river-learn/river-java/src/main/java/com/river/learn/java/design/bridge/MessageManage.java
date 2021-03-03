package com.river.learn.java.design.bridge;

/**
 * 消息接口
 * @author 17822
 */
public interface MessageManage {

    /**
     * 发送消息
     * @param message
     * @return
     */
    public boolean  send(Message message);

}
