package com.xhy.neihanduanzi.model.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by mkt on 2018/5/12.
 */

public class Account extends DataSupport {

    private static final long serialVersionUID = -4507554211068532759L;

    private int uid;

    private String uname;
    private String zhanghao;
    private String headicon;
    private String oauth_token;
    private String oauth_token_secret;
    private String sex;
    private String cookie;
    private CreditInfoBean credit_info;
    private String avatar_original;
    private String avatar_big;
    private String avatar_middle;
    private String avatar_small;
    private String avatar_tiny;
    private CountInfoBean count_info;
    private int state;
    private int is_vip;
    private int bind_phone_credit;


    public Account() {
        super();
    }


    public String getHeadicon() {
        return headicon;
    }

    public void setHeadicon(String headicon) {
        this.headicon = headicon;
    }


    public void setAvatarOriginal(String url) {
        this.avatar_original = url;
    }

    public void setAvatarBig(String url) {
        this.avatar_big = url;
    }

    public void setAvatarMiddle(String url) {
        this.avatar_middle = url;
    }

    public void setAvatarSmall(String url) {
        this.avatar_small = url;
    }

    public void setAvatarTiny(String url) {
        this.avatar_tiny = url;
    }

    public String getAvatarOriginal() {
        return avatar_original;
    }

    public String getAvatarBig() {
        return avatar_big;
    }

    public String getAvatarMiddle() {
        return avatar_middle;
    }

    public String getAvatarSmall() {
        return avatar_small;
    }

    public String getAvatarTiny() {
        return avatar_tiny;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getZhanghao() {
        return zhanghao;
    }

    public void setZhanghao(String zhanghao) {
        this.zhanghao = zhanghao;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBindPhoneCredit() {
        return bind_phone_credit;
    }

    public void setBindPhoneCredit(int credit) {
        this.bind_phone_credit = credit;
    }

    public boolean isVip() {
        return is_vip == 1;
    }

    public void setVipState(int vip_state) {
        this.is_vip = vip_state;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getOauth_token() {
        return oauth_token;
    }

    public void setOauth_token(String oauth_token) {
        this.oauth_token = oauth_token;
    }

    public String getOauth_token_secret() {
        return oauth_token_secret;
    }

    public void setOauth_token_secret(String oauth_token_secret) {
        this.oauth_token_secret = oauth_token_secret;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setCreditInfoBean(CreditInfoBean bean) {
        this.credit_info = bean;
    }

    public CreditInfoBean getCreditInfoBean() {
        return credit_info;
    }

    public CountInfoBean getCountInfoBean() {
        return count_info;
    }

    public void setCountInfoBean(CountInfoBean bean) {
        this.count_info = bean;
    }


    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public static class CreditInfoBean extends DataSupport {

        private CreditBean credit;

        public CreditBean getCreditBean() {
            return credit;
        }

        public void setGroup(CreditBean creditBean) {
            this.credit = creditBean;
        }

        public static class CreditBean extends DataSupport {

            private CreditBean.ExperienceBean experience;

            private CreditBean.ScoreBean score;

            public void setExperience(CreditBean.ExperienceBean experience) {
                this.experience = experience;
            }

            public ExperienceBean getExperience(CreditInfoBean.CreditBean.ExperienceBean experience) {
                return experience;
            }

            public void setScore(CreditBean.ScoreBean score) {
                this.score = score;
            }

            public CreditInfoBean.CreditBean.ScoreBean getScore() {
                return score;
            }


            //经验
            public static class ExperienceBean extends DataSupport {

                private int value;

                private String alias;

                public void setValue(int value) {
                    this.value = value;
                }

                public int getValue() {
                    return value;
                }

                public void setAlias(String alias) {
                    this.alias = alias;
                }

                public String getAlias() {
                    return alias;
                }

            }

            //积分
            public static class ScoreBean extends DataSupport {

                private int value;

                private String alias;

                public void setValue(int value) {
                    this.value = value;
                }

                public int getValue() {
                    return value;
                }

                public void setAlias(String alias) {
                    this.alias = alias;
                }

                public String getAlias() {
                    return alias;
                }
            }

            //等级
            public static class LeavelBean {

            }
        }
    }

    /**
     * 收藏数量
     */
    public static class CountInfoBean extends DataSupport {

        private int favorite_count;

        private int comment_count;

        private int pay_count;

        public int getFavoriteCount() {
            return favorite_count;
        }

        public void setCreditBean(int favorite_count) {
            this.favorite_count = favorite_count;
        }

        public int getPayCount() {
            return pay_count;
        }

        public void setPayCount(int pay_count) {
            this.pay_count = pay_count;
        }

        public int getCommentsCount() {
            return comment_count;
        }

        public void setCommentsCount(int comment_count) {
            this.comment_count = comment_count;
        }
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        Account u = (Account) o;
        return this.getUid() == u.getUid() ? true : false;
        //return super.equals(o);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Account[" + "uid=" + this.getUid() + "," + "uname=" + this.getUname() + "," + "oauth_token=" + this.getOauth_token() + ","
                + "oauth_token_secret=" + this.getOauth_token_secret() + "," + "headicon=" + this.getHeadicon() + "," + "state=" + this.getState() + "]"
                ;
    }
}

