package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;


public class VideoStatus implements Serializable {

    private int status;
    private String msg;
    private int score;

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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "PhoneToken{" +
                "status='" + status + '\'' +
                ", message='" + msg + '\'' +
                '}';
    }
}
