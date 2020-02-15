package com.xhy.neihanduanzi.utils.dbutils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xhy.neihanduanzi.model.bean.Picture;
import com.xhy.neihanduanzi.model.bean.PictureRecord;
import com.xhy.neihanduanzi.model.bean.VideoRecord;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by mkt on 2018/5/12.
 */

public class PictureRecordHelper {

    private static Gson mGson = new Gson();

    /**
     * 获取数据库保存的某个频道的最后一条记录
     *
     * @param channelCode 频道
     * @return
     */
    public static PictureRecord getLastNewsRecord(String uid, String channelCode) {
        return DataSupport.where("uid = ? and channelCode=?", uid, channelCode).findLast(PictureRecord.class);
    }

    /**
     * 保存图片记录
     *
     * @param channelCode
     * @param json
     */
    public static void save(int uid, String channelCode, String json) {
        int page = 1;
        //保存或者更新新的记录
        PictureRecord picRecord = new PictureRecord(uid, channelCode, page, json, System.currentTimeMillis());
        picRecord.saveOrUpdate("channelCode = ? and page = ?", channelCode, String.valueOf(page));
    }


    /**
     * 保存图片按页的记录
     *
     * @param channelCode
     * @param json
     */
    public static void saveByPage(int uid, String channelCode, String json, int page) {
        //保存新的记录
        PictureRecord pictureRecord = new PictureRecord(uid, channelCode, page, json, System.currentTimeMillis());
        pictureRecord.saveOrUpdate("channelCode = ? and page = ?", channelCode, String.valueOf(page));
    }

    /**
     * 根据频道码和页码查询新闻记录
     *
     * @param channelCode
     * @param page
     * @return
     */
    private static List<PictureRecord> selectVideoRecords(String channelCode, int page) {
        return DataSupport
                .where("channelCode = ? and page = ?", channelCode, String.valueOf(page))
                .find(PictureRecord.class);
    }

    /**
     * 将json转换成新闻集合
     *
     * @param json
     * @return
     */
    public static List<Picture> convertToPictureList(String json) {
        try {
            return mGson.fromJson(json, new TypeToken<List<Picture>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json转换成套图集合
     *
     * @param obj
     * @return
     */
    public static String pictureListToJson(Object obj) {
        Gson gson = new Gson();
        String obj2 = gson.toJson(obj);
        return obj2;
    }

    public static long delete(String uid) {
        return DataSupport.where("uid=?", uid).findLast(VideoRecord.class).delete();
    }

    //删除该uid的所有数据
    public static int deleteAll(int uid) {
        String id = String.valueOf(uid);
        try {
            return DataSupport.deleteAll(PictureRecord.class, "uid=?", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
