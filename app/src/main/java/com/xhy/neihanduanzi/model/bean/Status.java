package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

public class Status implements Serializable {

    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
