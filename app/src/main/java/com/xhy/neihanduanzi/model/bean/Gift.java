package com.xhy.neihanduanzi.model.bean;


import java.io.Serializable;

/**
 * 礼物实例
 */

public class Gift implements Serializable {

    protected int id;
    protected String name;
    protected String brief;
    protected String info;
    protected String image;
    protected int score;
    protected int stock;
    protected int sale_count;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSaleCount() {
        return sale_count;
    }

    public void setSaleCount(int saleCount) {
        this.sale_count = saleCount;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                        ", name='" + name + '\'' +
                        ", info='" + info + '\'' +
                        ", image=" + image +
                        ", score=" + score +
                        ", stock=" + stock +
                        '}';
    }
}
