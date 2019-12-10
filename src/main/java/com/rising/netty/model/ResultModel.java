package com.rising.netty.model;

import java.io.Serializable;

/**
 * create by zy 2019/11/29 11:12
 * TODO
 */
public class ResultModel implements Serializable {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
