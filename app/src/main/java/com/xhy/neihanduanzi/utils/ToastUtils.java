package com.xhy.neihanduanzi.utils;

import android.content.Context;
import android.widget.Toast;

import com.xhy.neihanduanzi.XHYApplication;

/**
 * Created by wcb1 on 2016/5/31.
 */
public class ToastUtils {


    public static boolean isShow = true;

    /** 之前显示的内容 */
    private static String oldMsg ;
    /** Toast对象 */
    private static Toast toast = null ;
    /** 第一次时间 */
    private static long oneTime = 0 ;
    /** 第二次时间 */
    private static long twoTime = 0 ;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showStr(CharSequence message){
        if(isShow){
            Toast.makeText(XHYApplication.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
//    public static void showLong(Context context, CharSequence message)
//    {
//        if (isShow)
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//    }
    /**
     * 显示Toast错误
     *
     * @param context
     */
    public static void showErr(Context context)
    {
        if (isShow)
            Toast.makeText(context, "网络错误", Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
//    public static void showLong(Context context, int message)
//    {
//        if (isShow)
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 显示Toast
     * @param context
     * @param message
     */
    public static void showToast(Context context,String message){
        if(TextUtil.isNull(message)){
            return ;
        }
        if(toast == null){
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show() ;
            oneTime = System.currentTimeMillis() ;
        }else{
            twoTime = System.currentTimeMillis() ;
            if(message.equals(oldMsg)){
                if(twoTime - oneTime > Toast.LENGTH_SHORT){
                    toast.show() ;
                }
            }else{
                oldMsg = message ;
                toast.setText(message) ;
                toast.show() ;
            }
        }
        oneTime = twoTime ;
    }
}
