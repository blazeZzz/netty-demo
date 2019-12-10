package com.rising.netty.model;

import java.io.Serializable;

/**
 * create by zy 2019/11/29 11:07
 * TODO
 */
public class Type1Data implements Serializable {
    private String guid;
    private String uniquevalue;
    private String user_name;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUniquevalue() {
        return uniquevalue;
    }

    public void setUniquevalue(String uniquevalue) {
        this.uniquevalue = uniquevalue;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
