package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2018/5/24.
 */

public class OrderBean implements Serializable {

    private int status;
    private String message;
    private String order_id;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderID() {
        return order_id;
    }

    public void setOrderID(String data) {
        this.order_id = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PhoneToken{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
