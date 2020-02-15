package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/10/26.
 * desc:
 */

public class PhoneToken implements Serializable {

    private int status;
    private String phonenum;
    private String message;
    private String smsCode;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String token) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "PhoneToken{" +
                "status='" + status + '\'' +
                ", token='" + message + '\'' +
                '}';
    }
}
