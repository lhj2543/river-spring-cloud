package com.river.learn.java.design.bridge;

/**
 * @author 17822
 */
public class Test {

    public static void main(String[] args) {

        Message message = new Message("签收消息","您好，你有快递需要签收","顺丰",new String[]{"张三","李四"});

        SendMethodAbstract s = new SendMethodAbstract(new EMail());
        s.timelySend(message);

        s.setMessageManage(new ShortLetter());
        s.timingSend(message);

    }

}
