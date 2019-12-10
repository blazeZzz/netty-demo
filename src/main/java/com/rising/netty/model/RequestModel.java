package com.rising.netty.model;

import java.io.Serializable;
import java.util.List;

/**
 * create by zy 2019/11/29 11:11
 * TODO
 */
public class RequestModel implements Serializable {
    private String list;
    private String timestamp;
    private String check_info;

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCheck_info() {
        return check_info;
    }

    public void setCheck_info(String check_info) {
        this.check_info = check_info;
    }
}
