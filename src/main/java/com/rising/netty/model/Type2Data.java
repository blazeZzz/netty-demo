package com.rising.netty.model;

import java.io.Serializable;

/**
 * create by zy 2019/11/29 11:08
 * TODO
 */
public class Type2Data implements Serializable {
    private String guid;
    private String taskid;
    private String taskname;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }
}
