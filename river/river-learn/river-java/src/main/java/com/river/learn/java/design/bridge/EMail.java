package com.river.learn.java.design.bridge;

/**
 *邮件发送实现类
 */
public class EMail implements MessageManage{

    @Override
    public boolean send(Message message) {
        System.err.println("邮件发送...");
        System.out.println(message.toString());
        return false;
    }
}
