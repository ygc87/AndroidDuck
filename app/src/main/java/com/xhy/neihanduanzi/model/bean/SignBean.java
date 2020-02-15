package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;

/**
 * Created by mkt on 2018/1/6.
 */

/**
 *
 * 签到获赠积分
 *
 * */


public class SignBean implements Serializable {

    private boolean ischeck;

    private int con_num;

    private int total_num;

    private String day;

    private String msg;


    public boolean getCheckState() {
        return ischeck;
    }

    public void setCheckState(boolean ischek) {

        this.ischeck = ischeck;
    }

    public int getConNum() {
        return con_num;
    }

    public void setConNum(int con_num) {
        this.con_num = con_num;
    }

    public int getTotalNum() {
        return total_num;
    }

    public void setTotalNum(int total_num) {
        this.total_num = total_num;
    }

    public String getDate() {
        return day;
    }

    public void setDate(String day) {
        this.day = day;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

}
