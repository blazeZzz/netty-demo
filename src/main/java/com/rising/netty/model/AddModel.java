package com.rising.netty.model;

import java.io.Serializable;
import java.util.List;

/**
 * create by zy 2019/11/29 11:02
 * TODO
 */
public class AddModel implements Serializable {

    private String guid;
    private String sguid;
    private String log_type;
    private List<Object> data;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSguid() {
        return sguid;
    }

    public void setSguid(String sguid) {
        this.sguid = sguid;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
