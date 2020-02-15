package com.xhy.neihanduanzi.utils.dbutils;

import com.xhy.neihanduanzi.model.bean.Account;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;


/**
 * Created by mkt on 2018/5/12.
 */

public class AccountHelper {

    private Account account;

    private AccountHelper() {

    }

    private static final class InstanceHolder {
        private static final AccountHelper instance = new AccountHelper();
    }

    public static AccountHelper INSTANCE(){
        return InstanceHolder.instance;
    }

    public static void save(Account account) {
        account.saveOrUpdate("uid = ? ", String.valueOf(account.getUid()));
    }

    public static Account getAccount() {
        return getLastAccount("1");
    }

    //查询上次用户
    private static Account getLastAccount(String state) {
        if(AccountHelper.INSTANCE().account == null){
            ClusterQuery cQuery = DataSupport.where("state=?", state);
            AccountHelper.INSTANCE().account = cQuery.findLast(Account.class);
        }
        return AccountHelper.INSTANCE().account;
    }

    public static long deleteAccount(String uid) {
        return DataSupport.where("uid=?", uid).findLast(Account.class).delete();
    }

    //当前无法判断是否登陆，需要重新修改
    public static boolean isLogin() {
        return getUserId() > 0;
    }

    public static int getUserId() {
        Account account = getLastAccount("1");
        if (account == null) {
            return 0;
        }
        return account.getUid();
    }

}
