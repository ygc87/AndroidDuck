package com.xhy.neihanduanzi.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaokun.neihanduanzi.R;
import com.lidroid.xutils.exception.DbException;
import com.xhy.neihanduanzi.base.BaseActivity;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.view.ProgressView;
import com.xhy.neihanduanzi.widget.OWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mkt on 2018/8/7.
 */

public class WebViewActivity extends BaseActivity {

    public static final String WEB_TITLE = "web_title";

    public static final String WEB_URL = "web_url";

    @BindView(R.id.iv_title_left)
    ImageView mBackButton;

    @BindView(R.id.tv_title_center)
    TextView mTitle;


    @BindView(R.id.progress_webview)
    ProgressView mProgressView;

    protected OWebView mWebView;

    @Override
    public int bindLayout() {
        return R.layout.activity_webview;
    }

    @Override
    public void initView(View view) throws DbException {

        String title = getIntent().getStringExtra(WEB_TITLE);
        if (!StringUtils.isEmpty(title)) {
            mTitle.setText(title);
        }

        String url = getIntent().getStringExtra(WEB_URL);
        mWebView = (OWebView) findViewById(R.id.webView);
        mWebView.setProgressView(mProgressView);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
                }

                return false;
            }

        });

        if (StringUtils.isUrl(url)) {
            mWebView.loadUrl(url);
        }

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                //判断网络环境
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_webview;
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_title_left})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_title_left:
                finish();
                break;
            default:
                break;
        }
    }

    public static void show(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WEB_TITLE, title);
        intent.putExtra(WEB_URL, url);
        context.startActivity(intent);
    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
        request.setTitle("小黄鸭下载测试");
        // 设置通知栏的描述
       // request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        //log.debug("fileName:{}", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        //log.debug("downloadId:{}", downloadId);
    }

    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //log.verbose("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    //log.debug("downloadId:{}", downloadId);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    //log.debug("getMimeTypeForDownloadedFile:{}", type);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (uri != null) {
                        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                        handlerIntent.setDataAndType(uri, type);
                        context.startActivity(handlerIntent);
                    }
                }
            }
        }
    }

}
