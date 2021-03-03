package com.river.learn.java.design.prototype;

import java.io.Serializable;

public class Types implements Serializable {

    private String type;

    Types(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
