package com.xhy.neihanduanzi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.xhy.neihanduanzi.AppConfig;
import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.activity.DetailVideoActivity;
import com.xhy.neihanduanzi.activity.LoginActivity;
import com.xhy.neihanduanzi.app.AppOperator;
import com.xhy.neihanduanzi.app.api.XHYApi;

/**
 * 界面帮助类
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 */
public class UIHelper {

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" " +
            "src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page" +
            ".js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window" +
            ".location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" " +
            "href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore" +
            ".css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common" +
            ".css\">";
    public final static String WEB_STYLE = linkCss;

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var " +
            "allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

    /**
     * 显示登录界面
     *
     * @param context
     */
    public static void showLoginActivity(Context context) {
        LoginActivity.show(context);
    }

//    /**
//     * 显示Team界面
//     *
//     * @param context
//     */
//    public static void showTeamMainActivity(Context context) {
//        Intent intent = new Intent(context, TeamMainActivity.class);
//        context.startActivity(intent);
//    }

//    /**
//     * 显示新闻详情
//     *
//     * @param context
//     * @param newsId
//     */
//    public static void showNewsDetail(Context context, long newsId,
//                                      int commentCount) {
//        net.oschina.app.improve.detail.general.NewsDetailActivity.show(context, newsId);
//    }


//    /**
//     * 显示博客详情
//     *
//     * @param context
//     * @param blogId
//     */
//    public static void showBlogDetail(Context context, long blogId) {
//        net.oschina.app.improve.detail.general.BlogDetailActivity.show(context, blogId);
//    }

//    /**
//     * 显示帖子详情
//     *
//     * @param context
//     * @param postId
//     */
//    public static void showPostDetail(Context context, long postId, int count) {
//        net.oschina.app.improve.detail.general.QuestionDetailActivity.show(context, postId);
//    }

//    /**
//     * 显示活动详情
//     *
//     * @param context
//     * @param eventId
//     */
//    public static void showEventDetail(Context context, long eventId) {
//        net.oschina.app.improve.detail.general.EventDetailActivity.show(context, eventId);
//    }

//    /**
//     * 显示相关Tag帖子列表
//     *
//     * @param context
//     * @param tag
//     */
//    public static void showPostListByTag(Context context, String tag) {
//        Bundle args = new Bundle();
//        args.putString(QuestionTagFragment.BUNDLE_KEY_TAG, tag);
//        showSimpleBack(context, SimpleBackPage.QUESTION_TAG, args);
//    }
//
//    public static void showSoftwareDetailById(Context context, int id) {
//        net.oschina.app.improve.detail.general.SoftwareDetailActivity.show(context, id);
//    }

    /**
     * show detail  method
     *
     * @param context context
     * @param type    type
     * @param id      id
     */
    public static void showDetail(Context context, int type, long id, String href) {
        switch (type) {
            case XHYApi.CATALOG_ALL:
                //新闻链接
                //showUrlRedirect(context, id, href);
                break;
            case XHYApi.CATALOG_SOFTWARE:
                //软件推荐/
                //SoftwareDetailActivity.show(context, id);
                //UiUtil.showSoftwareDetailById(context, (int) id);
                break;
            case XHYApi.CATALOG_QUESTION:
                //问答
                //QuestionDetailActivity.show(context, id);
                break;
            case XHYApi.CATALOG_BLOG:
                //博客
                //BlogDetailActivity.show(context, id);
                break;
            case XHYApi.CATALOG_TRANSLATION:
                //4.翻译
                //NewsDetailActivity.show(context, id, News.TYPE_TRANSLATE);
                break;
            case XHYApi.CATALOG_EVENT:
                //活动
                //EventDetailActivity.show(context, id);
                break;
            case XHYApi.CATALOG_TWEET:
                // 动弹
                //VideoDetailActivity.show(context, id);
                DetailVideoActivity.show(context, id);
                break;
            default:
                //6.资讯
                //NewsDetailActivity.show(context, id);
                break;
        }
    }

//    public static void showBannerDetail(Context context, BannerBean banner) {
//        long newsId = banner.getId();
//        switch (banner.getType()) {
//            case BannerBean.BANNER_TYPE_URL:
//                showNewsDetail(context, Integer.parseInt(String.valueOf(newsId)), 0);
//                break;
//            case BannerBean.BANNER_TYPE_SOFTWARE:
//                showSoftwareDetailById(context, Integer.parseInt(String.valueOf(newsId)));
//                break;
//            case BannerBean.BANNER_TYPE_POST:
//                showPostDetail(context, StringUtils.toInt(String.valueOf(newsId)),
//                        0);
//                break;
//            case BannerBean.BANNER_TYPE_BLOG:
//                showBlogDetail(context, StringUtils.toLong(String.valueOf(newsId)));
//                break;
//            case BannerBean.BANNER_TYPE_EVENT:
//                net.oschina.app.improve.detail.general.EventDetailActivity.show(context, newsId);
//                break;
//            case BannerBean.BANNER_TYPE_NEWS:
//                NewsDetailActivity.show(context, newsId);
//            default:
//                showUrlRedirect(context, banner.getHref());
//                break;
//        }
//    }

//    /**
//     * 动态点击跳转到相关新闻、帖子等
//     *
//     * @param context context
//     * @param active  动态实体类
//     *                0其他 1新闻 2帖子 3动弹 4博客
//     */
//    public static void showActiveRedirect(Context context, Active active) {
//        String url = active.getUrl();
//        // url为空-旧方法
//        if (StringUtils.isEmpty(url)) {
//            int id = active.getObjectId();
//            int catalog = active.getCatalog();
//            switch (catalog) {
//                case Active.CATALOG_OTHER:
//                    // 其他-无跳转
//                    break;
//                case Active.CATALOG_NEWS:
//                    showNewsDetail(context, id, active.getCommentCount());
//                    break;
//                case Active.CATALOG_POST:
//                    showPostDetail(context, id, active.getCommentCount());
//                    break;
//                case Active.CATALOG_TWEET:
//                    TweetDetailActivity.show(context, id);
////                    showTweetDetail(context, null, id);
//                    break;
//                case Active.CATALOG_BLOG:
//                    showBlogDetail(context, id);
//                    break;
//                default:
//                    break;
//            }
//        } else {
//            showUrlRedirect(context, url);
//        }
//    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(14);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        //webView.setWebViewClient(UiUtil.getWebViewClient());
    }

//    /**
//     * 添加网页的点击图片展示支持
//     */
//    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
//    @JavascriptInterface
//    public static void addWebImageShow(final Context cxt, WebView wv) {
//        wv.getSettings().setJavaScriptEnabled(true);
//        wv.addJavascriptInterface(new OnWebViewImageListener() {
//            @Override
//            @JavascriptInterface
//            public void showImagePreview(String bigImageUrl) {
//                if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
//                    ImageGalleryActivity.show(cxt, bigImageUrl);
//                }
//            }
//        }, "mWebViewImageListener");
//    }

    public static String setHtmlCotentSupportImagePreview(String body) {
        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {
            // 过滤掉 img标签的width,height属性
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
            // 添加点击图片放大支持
            // 添加点击图片放大支持
            body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"showImagePreview('$2')\"");
        } else {
            // 过滤掉 img标签
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }

        // 过滤table的内部属性
        body = body.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");

        return body;
    }

//    private static void showUrlRedirect(Context context, long id, String url) {
//        if (url == null && id > 0) {
//            net.oschina.app.improve.detail.general.NewsDetailActivity.show(context, id);
//            return;
//        }
//
//        URLUtils.parseUrl(context, url);
//    }

    /**
     * url跳转
     *
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {
        //showUrlRedirect(context, 0, url);
    }

    /**
     * 打开外置的浏览器
     *
     * @param context
     * @param url
     */
    public static void openExternalBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, "选择打开的应用"));
    }

    /**
     * 组合动态的回复文本
     *
     * @param name
     * @param body
     * @return
     */
    public static SpannableStringBuilder parseActiveReply(String name,
                                                          String body) {
        Spanned span = Html.fromHtml(body.trim());
        SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
        sp.append(span);
        // 设置用户名字体加粗、高亮
        // sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
        // name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
    }

    /**
     * 清除app缓存
     */
    public static void clearAppCache(final Context context, boolean showToast) {
        final Handler handler = showToast ? new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastUtils.showToast(context,"缓存清除成功");
                } else {
                    ToastUtils.showToast(context,"缓存清除失败");
                }
            }
        } : null;
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                if (handler != null)
                    handler.sendMessage(msg);
            }
        });
    }

}
