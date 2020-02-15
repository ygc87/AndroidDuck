package com.xhy.neihanduanzi.model.bean;


import java.io.Serializable;

public class ScoreDetail implements Serializable {

    protected int rid;
    protected int uid;
    protected int ctime;
    protected String score;
    protected String action;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    @Override
    public String toString() {
        return

                " rid='" + rid + '\'' +
                        ", uid='" + uid + '\'' +
                        ", ctime=" + ctime +
                        ", score=" + score +
                        ", action=" + action +
                        '}';
    }
}