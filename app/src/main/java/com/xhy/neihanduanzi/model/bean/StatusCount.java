package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2018/3/6.
 */

public class StatusCount implements Serializable {

    private int status;
    private int count;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    @Override
    public String toString() {
        return "PhoneToken{" +
                "status='" + status + '\'' +
                ", message='" + msg + '\'' +
                '}';
    }
}
