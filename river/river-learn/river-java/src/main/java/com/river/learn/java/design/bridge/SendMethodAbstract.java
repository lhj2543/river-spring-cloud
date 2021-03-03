package com.river.learn.java.design.bridge;

/**
 * 桥接模式
 * 消息发送方式抽象类
 * @author 17822
 */
public class SendMethodAbstract {

    MessageManage messageManage;

    public SendMethodAbstract(MessageManage messageManage) {
        this.messageManage = messageManage;
    }

    public MessageManage getMessageManage() {
        return messageManage;
    }

    public void setMessageManage(MessageManage messageManage) {
        this.messageManage = messageManage;
    }

    /**
     * 及时发送
     * @return
     */
    public boolean timelySend(Message message){
        System.out.println("及时发送消息...");
        return messageManage.send(message);
    };

    /**
     * 定时发送
     * @return
     */
    public boolean timingSend(Message message){
        System.out.println("定时发送消息...");
        return messageManage.send(message);
    };


}
