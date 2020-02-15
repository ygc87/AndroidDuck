package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;


public class ScoreRule implements Serializable {

    protected String name;
    protected String alias;
    protected String score;
    protected int experience;
    protected String score_alias;
    protected String experience_alias;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getScoreAlias() {
        return score_alias;
    }

    public void setScoreAlias(String info) {
        this.score_alias = score_alias;
    }

    public String getExperienceAlias() {
        return experience_alias;
    }

    public void setExperienceAlias(String experience_alias) {
        this.experience_alias = experience_alias;
    }


    @Override
    public String toString() {
        return

                " name='" + name + '\'' +
                        ", alias='" + alias + '\'' +
                        ", score=" + score +
                        ", stock=" + experience +
                        ", score_alias=" + score_alias +
                        ", experience_alias=" + experience_alias +
                        '}';
    }


}
