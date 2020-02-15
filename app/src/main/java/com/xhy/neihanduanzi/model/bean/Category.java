package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;
import java.util.List;

//动态导航分类
public class Category implements Serializable {

    private String customer_service;

    private ChargeBean.PayType pay_type;

    private List<ChargeBean.PayBean> pay_num;

    public ChargeBean.PayType getPayType() {
        return pay_type;
    }

    public void setPayType(ChargeBean.PayType type) {
        this.pay_type = type;
    }

    public List<ChargeBean.PayBean> getPayNum() {
        return pay_num;
    }

//    public void setPayNum(PayNum num) {
//        this.pay_num = num;
//    }

    public String getCustomerServicePic() {
        return customer_service;
    }

    public void setCustomerServicePic(String servicePic) {
        this.customer_service = servicePic;
    }

    //支付类型
    public class PayType implements Serializable {
        private int weixin;
        private int alipay;

        public int getWeixin() {
            return weixin;
        }

        public int getAlipay() {
            return alipay;
        }
    }

    //支付类型
    public class PayBean implements Serializable {
        protected int id;
        protected String money;
        protected String credit;
        protected String send_credit;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String name) {
            this.money = money;
        }

        public String getCredit() {
            return credit;
        }

        public void setCredit(String brief) {
            this.credit = credit;
        }

        public String getSendredit() {
            return send_credit;
        }

        public void setSendredit(String info) {
            this.send_credit = send_credit;
        }

    }

    @Override
    public String toString() {
        return "";
//                "id=" + id +
//                        ", money='" + money + '\'' +
//                        ", credit='" + credit + '\'' +
//                        ", send_credit=" + send_credit +
//                        '}';
    }
}
