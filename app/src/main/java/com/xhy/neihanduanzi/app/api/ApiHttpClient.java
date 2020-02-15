package com.xhy.neihanduanzi.app.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xhy.neihanduanzi.AppContext;
import com.xhy.neihanduanzi.Setting;
import com.xhy.neihanduanzi.utils.StringUtils;
import com.xhy.neihanduanzi.utils.TDevice;
import com.xhy.neihanduanzi.utils.TLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.AbstractHttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

@SuppressWarnings("WeakerAccess")
public class ApiHttpClient {

    public static String BASE_URL = "http://www.djk777.com:8089/";

    static class ApiAsyncHttpClient extends AsyncHttpClient {
        @Override
        protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
            return new CheckNetAsyncHttpRequest(client, httpContext, uriRequest, responseHandler);
        }
    }

    static class CheckNetAsyncHttpRequest extends AsyncHttpRequest {
        public CheckNetAsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
            super(client, context, request, responseHandler);
        }

        @Override
        public void run() {
            if (!TDevice.hasInternet()) {
                new Thread() {
                    @Override
                    public void run() {
                        // 延迟一秒
                        try {
                            SystemClock.sleep(1000);
                            cancel(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            try {
                //防止网络出现延迟
                super.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static AsyncHttpClient CLIENT;

    private ApiHttpClient() {
    }

    /**
     * 初始化网络请求，包括Cookie的初始化
     *
     * @param context AppContext
     */
    public static void init(Application context) {
        BASE_URL = Setting.getServerUrl(context);//+ "%s";
        AsyncHttpClient client = new ApiAsyncHttpClient();
        client.setConnectTimeout(5 * 1000);
        client.setResponseTimeout(7 * 1000);
        ApiHttpClient.setHttpClient(client, context);
        if (!StringUtils.isEmpty(BASE_URL)) {
            //需要对API_URL进行判断
            client.post(BASE_URL, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //判断状态码
                    if (statusCode == 200) {
                        //获取结果
                        try {
                            String result = new String(responseBody, "utf-8");
                            //Toast.makeText(context, result, 0).show();
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                }
            });
        }

    }

    public static AsyncHttpClient getHttpClient() {
        return CLIENT;
    }

    public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.delete(getAbsoluteApiUrl(partUrl), handler);
        log("DELETE " + partUrl);
    }


    public static String getAbsoluteApiUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format("https://www.oschina.net/%s", partUrl);
        }
        log("request:" + url);
        return url;
    }


    public static String getAbsoluteBaseUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(BASE_URL, partUrl);
        }
        log("request:" + url);
        return url;
    }

    /**
     * 注册用户
     */
    public static void registerAccount(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        CLIENT.post(getAbsoluteBaseUrl(partUrl), params, handler);
    }


    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        CLIENT.get(url, handler);
        log("GET " + url);
    }

    public static void log(String log) {
        TLog.log("ApiHttpClient", log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.post(getAbsoluteApiUrl(partUrl), handler);
        log("POST " + partUrl);
    }

    public static void post(String partUrl, RequestParams params,
                            AsyncHttpResponseHandler handler) {
        CLIENT.post(getAbsoluteApiUrl(partUrl), params, handler);
        log("POST " + partUrl + "?" + params);
    }

    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), handler);
        log("PUT " + partUrl);
    }

    public static void put(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), params, handler);
        log("PUT " + partUrl + "?" + params);
    }

    public static void setHttpClient(AsyncHttpClient c, Application application) {
        c.addHeader("Accept-Language", Locale.getDefault().toString());
        //c.addHeader("Host", HOST);
        c.addHeader("Connection", "Keep-Alive");
        //noinspection deprecation
        c.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        // Set AppToken
        //c.addHeader("AppToken", Verifier.getPrivateToken(application));
        //c.addHeader("AppToken", "123456");
        // setUserAgent
        c.setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
        CLIENT = c;
        //initSSL(CLIENT);
    }

    public static void setCookieHeader(String cookie) {
        if (!TextUtils.isEmpty(cookie))
            CLIENT.addHeader("Cookie", cookie);
        log("setCookieHeader:" + cookie);
    }

    /**
     * 销毁当前AsyncHttpClient 并重新初始化网络参数，初始化Cookie等信息
     *
     * @param appContext AppContext
     */
    public static void destroyAndRestore(Application appContext) {
        cleanCookie();
        CLIENT = null;
        init(appContext);
    }

    public static void cleanCookie() {
        // first clear store
        // new PersistentCookieStore(AppContext.getInstance()).clear();
        // clear header
        AsyncHttpClient client = CLIENT;
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);
            // 清理Async本地存储
            if (cookies != null) {
                cookies.clear();
            }
            // 清理当前正在使用的Cookie
            client.removeHeader("Cookie");
        }
        log("cleanCookie");
    }

    /**
     * 从AsyncHttpClient自带缓存中获取CookieString
     *
     * @param client AsyncHttpClient
     * @return CookieString
     */
    private static String getClientCookie(AsyncHttpClient client) {
        String cookie = "";
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);

            if (cookies != null && cookies.getCookies() != null && cookies.getCookies().size() > 0) {
                for (Cookie c : cookies.getCookies()) {
                    cookie += (c.getName() + "=" + c.getValue()) + ";";
                }
            }
        }
        log("getClientCookie:" + cookie);
        return cookie;
    }

    /**
     * 得到当前的网络请求Cookie，
     * 登录后触发
     *
     * @param headers Header
     */
    public static String getCookie(Header[] headers) {
        String cookie = getClientCookie(ApiHttpClient.getHttpClient());
        if (TextUtils.isEmpty(cookie)) {
            cookie = "";
            if (headers != null) {
                for (Header header : headers) {
                    String key = header.getName();
                    String value = header.getValue();
                    if (key.contains("Set-Cookie"))
                        cookie += value + ";";
                }
                if (cookie.length() > 0) {
                    cookie = cookie.substring(0, cookie.length() - 1);
                }
            }
        }

        log("getCookie:" + cookie);
        return cookie;
    }

    private static void initSSL(AsyncHttpClient client) {
        try {
            /// We initialize a default Keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // We load the KeyStore
            trustStore.load(null, null);
            // We initialize a new SSLSocketFacrory
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            // We set that all host names are allowed in the socket factory
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // We set the SSL Factory
            client.setSSLSocketFactory(socketFactory);
            // We initialize a GET http request
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        @SuppressWarnings("WeakerAccess")
        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


}
