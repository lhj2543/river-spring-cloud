package com.river.learn.java.design.bridge;

/**
 *  短信发送实现类
 */
public class ShortLetter  implements MessageManage{

    @Override
    public boolean send(Message message) {
        System.err.println("短信发送...");
        System.out.println(message.toString());
        return false;
    }

}
