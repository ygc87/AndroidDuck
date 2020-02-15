package com.xhy.neihanduanzi.app.api;

import android.text.TextUtils;

import com.xhy.neihanduanzi.model.bean.Account;
import com.xhy.neihanduanzi.model.bean.PhoneToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * OSChina Api v1 and v2
 */
public class XHYApi {

    public static final int CATALOG_ALL = 0;
    public static final int CATALOG_SOFTWARE = 1;
    public static final int CATALOG_QUESTION = 2;
    public static final int CATALOG_BLOG = 3;
    public static final int CATALOG_TRANSLATION = 4;
    public static final int CATALOG_EVENT = 5;

    public static final int CATALOG_TWEET = 100;

    public static final int RESET_PWD_INTENT = 2;

    /* =============================================================================================
     * =============================================================================================
     *
     * Oschina Api V1
     * Don't use them any more
     *
     * =============================================================================================
     * =============================================================================================
     */

    /***
     * 获取签到信息
     *
     * @param account
     * @param handler
     * @return void
     */
    @Deprecated
    public static void checkSignin(Account account, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Checkin");
        params.put("act", "get_check_info");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /***
     * 签到功能
     *
     * @param account
     * @param handler
     * @return void
     * @author 火蚁 2015-3-13 上午11:45:47
     */
    @Deprecated
    public static void signin(Account account, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Checkin");
        params.put("act", "checkin");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /***
     * 客户端扫描二维码登陆
     *
     * @param url
     * @param handler
     * @return void
     * @author 火蚁 2015-3-13 上午11:45:47
     */
    @Deprecated
    public static void scanQrCodeLogin(String url, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String uuid = url.substring(url.lastIndexOf("=") + 1);
        params.put("uuid", uuid);
        ApiHttpClient.getDirect(url, handler);
    }

    /**
     * 请求视频评论列表
     *
     * @param sourceId 视频id
     * @param handler  回调
     */
    public static void getVideoCommentList(long sourceId, Account account, int pageToken, String tableName, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "WeiboStatuses");
        params.put("act", "video_comments");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", tableName);
        params.put("id", sourceId);
        params.put("page", pageToken);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 发表视频评论列表
     *
     * @param videoId 视频id
     * @param content 评论内容
     * @param account 当前用户账号
     * @param handler 回调
     */
    public static void pubVideoComment(int videoId, String content, Account account, String tableName, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "WeiboStatuses");
        params.put("act", "comment_video");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", tableName);
        //评论视频
        params.put("row_id", videoId);
        //评论内容
        params.put("content", content);
        String partUrl = ApiHttpClient.BASE_URL;
        //提交评论内容
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 提交评论点赞
     *
     * @param commentID 评论id
     * @param handler   回调
     */
    public static void addCommentDigg(int commentID, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "add_videocomment_digg");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("comment_id", commentID);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 提交视频点赞
     *
     * @param videoId 视频id
     * @param handler 回调
     */
    public static void addVideoDigg(int videoId, Account account, String tableName, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "add_video_digg");
        params.put("source_table_name", tableName);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("video_id", videoId);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 短视频收藏
     *
     * @param videoId 视频id
     * @param handler 回调
     */
    public static void addSvideoCollected(int videoId, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "add_collected");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", "svideo");
        params.put("source_id", videoId);
        params.put("source_app", "public");
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 长视频收藏
     *
     * @param videoId 视频id
     * @param handler 回调
     */
    public static void addLvideoCollected(int videoId, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "add_collected");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", "lvideo");
        params.put("source_id", videoId);
        params.put("source_app", "public");
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 提交图片点赞
     *
     * @param objectID 图片主题id
     * @param handler  回调
     */
    public static void addPicLike(int objectID, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "add_pic_digg");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("object_id", objectID);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取个人信息
     */
    public static void getUserInfo(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "User");
        params.put("act", "show");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("user_id", account.getUid());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * update the user icon
     *
     * @param file    file
     * @param handler handler
     */
    public static void uploadFile(Account account, File file, TextHttpResponseHandler handler) throws Exception {
        // 上传文件
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "User");
        params.put("act", "upload_face");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("Filedata", file);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * login account
     *
     * @param username username
     * @param pwd      pwd
     * @param handler  handler
     */
    public static void login(String username, String pwd, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Oauth");
        params.put("act", "authorize");
        params.put("login", username);
        String md5Pwd = md5(pwd);
        //密码进行加密
        params.put("password", md5Pwd);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 发送绑定短信验证码
     */
    public static void sendRegisterCode(String phone, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("api_type", "sociax");
        params.put("mod", "Oauth");
        params.put("act", "send_register_code");
        params.put("phone", phone);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 绑定手机号
     */
    public static void do_bind_phone(String phone, Account account, String smsCode, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("api_type", "sociax");
        params.put("mod", "User");
        params.put("act", "do_bind_phone");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("phone", phone);
        params.put("code", smsCode);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 发送短信验证码
     */
    public static void sendSmsCode(String phone, int intent, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("api_type", "sociax");
        params.put("mod", "Oauth");
        params.put("act", "sendCodeByPhone");
        params.put("login", phone);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 验证验证码是否正确
     * <p>
     * validate and get phone token
     *
     * @param phoneNumber phoneNumber
     * @param smsCode     smsCode
     * @param handler     handler
     */
    public static void validateRegisterInfo(String phoneNumber, String smsCode, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("api_type", "sociax");
        params.put("mod", "Oauth");
        params.put("act", "checkCodeByPhone");
        params.put("login", phoneNumber);
        params.put("code", smsCode);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * validate and get email token
     *
     * @param handler handler
     */
    public static void validateRegister(Account account, String pwd, String inviteCode, String device_uuid, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Oauth");
        params.put("act", "register");
        params.put("uname", account.getUname());
        params.put("password", pwd);
        params.put("zhanghao", account.getZhanghao());
        params.put("sex", account.getSex());
        params.put("invite_code", inviteCode);
        params.put("device_uuid", device_uuid);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取频道列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getChannelList(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("api_type", "sociax");
        params.put("mod", "Channel");
        params.put("act", "getchannel");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取视频列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getVideoList(Account account, String channel, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String partUrl = ApiHttpClient.BASE_URL;
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "get_video_list");
        params.put("source_table", channel);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取收藏的视频
     *
     * @param account account
     * @param handler handler
     */
    public static void getVideoCollected(Account account, int page, String chanel, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "get_collected_list");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("user_id", account.getUid());
        params.put("source_table_name", chanel);
        params.put("page", page);
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 取消长收藏的视频
     *
     * @param account account
     * @param handler handler
     */
    public static void dellVideoCollected(int videoId, Account account, String tableName, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "del_collected");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("user_id", account.getUid());
        params.put("source_id", videoId);
        params.put("source_table_name", tableName);
        params.put("count", 5);
        params.put("page", 1);
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 购买短视频
     *
     * @param account account
     * @param videoID videoID
     * @param handler handler
     */
    public static void buySVideoProduct(Account account, int videoID, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "buy_video");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("video_id", videoID);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 请求视频播放地址
     *
     * @param account account
     * @param videoID videoID
     * @param handler handler
     */
    public static void requesVideoURL(Account account, int videoID, String table, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "get_videourl");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("video_id", videoID);
        params.put("source_table_name", table);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 购买短视频
     *
     * @param account account
     * @param videoID videoID
     * @param handler handler
     */
    public static void buyLVideoProduct(Account account, int videoID, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "buy_long_video");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("video_id", videoID);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取礼物列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getGiftList(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Gift");
        params.put("act", "getList");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());

        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取积分规则列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getScoreRuleList(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "rule");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());

        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取积分详情列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getScoreDetailList(Account account, int page, int limit, TextHttpResponseHandler handler) {
        if (account == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "detail");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("page", page);
        params.put("count", limit);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    public static void getTotalScore(Account account, TextHttpResponseHandler handler, int limit) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "credit_my");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * reset pwd
     *
     * @param password password
     * @param handler  handler
     */
    public static void resetPwd(String password, PhoneToken token
            , TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (token != null) {
            params.put("app", "api");
            params.put("api_type", "sociax");
            params.put("mod", "Oauth");
            params.put("act", "saveUserPasswordByPhone");
            params.put("login", token.getPhonenum());
            params.put("password", password);
            params.put("code", token.getSmsCode());
            String partUrl = ApiHttpClient.BASE_URL + "api.php";
            ApiHttpClient.post(partUrl, params, handler);
        }
    }

    /**
     * send feedback
     *
     * @param content content
     * @param handler handler
     */
    public static void commitFeedBack(String content, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("mod", "System");
        params.put("act", "sendFeedback");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("content", content);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * modify password
     *
     * @param account account
     * @param handler handler
     */
    public static void modifPassword(String oldpass, String password, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "User");
        params.put("act", "save_user_info");
        params.put("old_password", oldpass);
        params.put("password", password);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取图片列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getAllPicList(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "pic_list");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 图片收藏
     *
     * @param objectID 图片主题id
     * @param handler  回调
     */
    public static void addPicCollected(int objectID, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "pic_favorite_create");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("object_id", objectID);
        params.put("source_table_name", "pic_object");
        params.put("source_id", objectID);
        params.put("source_app", "public");
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 取消图片收藏
     *
     * @param id      图片id
     * @param handler 回调
     */
    public static void dellPicCollected(int id, Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "del_collected");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", "pic_object");
        params.put("source_id", id);
        params.put("source_app", "public");
        params.put("format", "json");
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取收藏的图片
     *
     * @param account 用户的账户
     * @param handler 回调
     */
    public static void getPicCollectedList(Account account, int page, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "collected_object_list");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", "pic_object");
        params.put("source_app", "public");
        params.put("format", "json");
        params.put("page", page);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取已购图片主题列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getBuyPicObjectList(Account account, int page, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "buy_object_list");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("page", page);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取已购主题的所有图片
     *
     * @param account account
     * @param handler handler
     */
    public static void getBuyPicList(Account account, int id, TextHttpResponseHandler handler) {
        //商品ID
        RequestParams params = new RequestParams();
        params.put("api_type", "sociax");
        params.put("app", "api");
        params.put("mod", "Pic");
        params.put("act", "buy");
        params.put("pic_id", id);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取已购视频列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getBuyVideoList(Account account, int page, String tableName, TextHttpResponseHandler handler) {
        //商品ID
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "payed_video_list");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        params.put("source_table_name", tableName);
        params.put("page", page);
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * open lock screen
     * <p>
     * 忘记解锁密码时发送请求
     *
     * @param username username
     * @param pwd      pwd
     * @param handler  handler
     */
    public static void openLockScreen(String username, String pwd, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Oauth");
        params.put("act", "authorize");
        params.put("login", username);
        String md5Pwd = md5(pwd);
        //密码进行加密
        params.put("password", md5Pwd);
        String partUrl = ApiHttpClient.BASE_URL + "api.php";
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取已购礼物列表
     *
     * @param account account
     * @param handler handler
     */
    public static void buyGift(Account account, int giftID, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Gift");
        params.put("act", "buy_gift");
        params.put("gift_id", giftID);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }


    /**
     * 获取充值价格列表
     *
     * @param account account
     * @param handler handler
     */
    public static void getChargeList(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "get_pay_list_new");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 查询支付订单是否成功支付
     *
     * @param account account
     * @param handler handler
     */
    public static void queryOrder(Account account, String orderid, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "query_order");
        params.put("order_id", orderid);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 积分充值
     *
     * @param account account
     * @param handler handler
     */
    public static void saveCharge(Account account, float chargeValue, String shopName, String type, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Credit");
        params.put("act", "createCharge");
        params.put("money", chargeValue);
        params.put("shop_name", shopName);
        params.put("type", type);
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 启动页广告
     *
     * @param handler handler
     */
    public static void getWelcomeAD(TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "welcome_ad");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 获取安装包下载地址
     *
     * @param account account
     * @param handler handler
     */
    public static void getApkUrl(Account account, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "apk_download");
        params.put("oauth_token", account.getOauth_token());
        params.put("oauth_token_secret", account.getOauth_token_secret());
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * 检查APP当前版本,并提示用户是否更新
     *
     * @param handler handler
     */
    public static void checkUpdate(TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "api");
        params.put("mod", "Video");
        params.put("act", "apk_update");
        String partUrl = ApiHttpClient.BASE_URL;
        ApiHttpClient.post(partUrl, params, handler);
    }

    /**
     * md5 加密用户名密码
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
