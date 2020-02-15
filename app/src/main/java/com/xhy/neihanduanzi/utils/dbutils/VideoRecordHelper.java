package com.xhy.neihanduanzi.utils.dbutils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xhy.neihanduanzi.model.bean.Video;
import com.xhy.neihanduanzi.model.bean.VideoRecord;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by mkt on 2018/5/12.
 */

public class VideoRecordHelper {

    private static Gson mGson = new Gson();

    /**
     * 获取数据库保存的某个频道的最后一条记录
     *
     * @param channelCode 频道
     * @return
     */
    public static VideoRecord getLastVideoRecord(String uid, String channelCode) {
        try {
            return DataSupport.where("uid = ? and channelCode=?", uid, channelCode).findLast(VideoRecord.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取某个频道上一组新闻记录
     *
     * @param channelCode 频道
     * @param page        页码
     * @return
     */
    public static VideoRecord getPreVideoRecord(String channelCode, int page) {
        List<VideoRecord> newsRecords = selectVideoRecords(channelCode, page - 1);
//
//        if (ListUtils.isEmpty(newsRecords)) {
//            return null;
//        }

        return newsRecords.get(0);
    }


    /**
     * 保存视频记录
     *
     * @param channelCode
     * @param json
     */
    public static void save(int uid, String channelCode, String json) {
        //保存新的记录
        VideoRecord videoRecord = new VideoRecord(uid, channelCode, 0, json, System.currentTimeMillis());
        videoRecord.saveOrUpdate("channelCode = ? and page = ?", channelCode, String.valueOf(0));
    }

    /**
     * 保存视频记录
     *
     * @param channelCode
     * @param json
     */
    public static void saveByPage(int uid, String channelCode, String json, int page) {
        //保存新的记录
        VideoRecord videoRecord = new VideoRecord(uid, channelCode, page, json, System.currentTimeMillis());
        videoRecord.saveOrUpdate("channelCode = ? and page = ?", channelCode, String.valueOf(page));
    }


    /**
     * 根据频道码和页码查询新闻记录
     *
     * @param channelCode
     * @param page
     * @return
     */
    private static List<VideoRecord> selectVideoRecords(String channelCode, int page) {
        return DataSupport
                .where("channelCode = ? and page = ?", channelCode, String.valueOf(page))
                .find(VideoRecord.class);
    }


    /**
     * 将json转换成新闻集合
     *
     * @param json
     * @return
     */
    public static List<Video> convertToVideoList(String json) {
        try {
            return mGson.fromJson(json, new TypeToken<List<Video>>() {
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
    public static String videoListToJson(Object obj) {
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
        return DataSupport.deleteAll(VideoRecord.class, "uid=?", id);
    }
}
