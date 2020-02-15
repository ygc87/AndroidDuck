package com.xhy.neihanduanzi.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mkt on 2018/4/26.
 */

public class ChargeBean implements Serializable {

    private String customer_service;

    private PayType pay_type;

    private List<PayBean> pay_num;

    public PayType getPayType() {
        return pay_type;
    }

    public void setPayType(PayType type) {
        this.pay_type = type;
    }

    public List<PayBean> getPayNum() {
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
        protected float money_value;

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

        public float getMoneyValue() {
            return money_value;
        }

        public void setMoneyValue(float money_value) {
            this.money_value = money_value;
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
