package com.river.learn.java.design.bridge;

import java.util.Arrays;

/**
 * 消息实体类
 * @author 17822
 */
public class Message {

    //消息标题
    private String title;

    //消息内容
    private String content;

    //发送者
    private String sendUser;

    //接收者
    private String[] receiveUser;

    public Message(String title, String content, String sendUser, String[] receiveUser) {
        this.title = title;
        this.content = content;
        this.sendUser = sendUser;
        this.receiveUser = receiveUser;
    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sendUser='" + sendUser + '\'' +
                ", receiveUser=" + Arrays.toString(receiveUser) +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String[] getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String[] receiveUser) {
        this.receiveUser = receiveUser;
    }
}
