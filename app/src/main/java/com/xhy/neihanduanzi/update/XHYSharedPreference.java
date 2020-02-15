package com.xhy.neihanduanzi.update;

import android.content.Context;


@SuppressWarnings("unused")
public final class XHYSharedPreference extends SharedPreferenceUtil {

    private static XHYSharedPreference mInstance;

    public static void init(Context context, String name) {
        if (mInstance == null) {
            mInstance = new XHYSharedPreference(context, name);
        }
    }

    public static XHYSharedPreference getInstance() {
        return mInstance;
    }

    private XHYSharedPreference(Context context, String name) {
        super(context, name);
    }

    /**
     * 点击更新过的版本
     */
    void putUpdateVersion(int code) {
        put("xhy_update_code", code);
    }

    /**
     * 设置更新过的版本
     */
    public int getUpdateVersion() {
        return getInt("xhy_update_code", 0);
    }

    /**
     * 设置不弹出更新
     */
    public void putShowUpdate(boolean isShow) {
        put("xhy_update_show", isShow);
    }

    /**
     * 是否弹出更新
     * 或者是新版本重新更新 259200000
     */
    boolean isShowUpdate() {
        return getBoolean("xhy_update_show", true);
    }

    /**
     * 是否已经弹出更新
     *
     * @return 不弹出更新代表已经更新
     */
    public boolean hasShowUpdate() {
        return !getBoolean("xhy_update_show", true);
    }
}
