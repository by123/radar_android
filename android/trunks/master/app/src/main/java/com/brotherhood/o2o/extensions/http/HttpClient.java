package com.brotherhood.o2o.extensions.http;

import android.text.TextUtils;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.skynet.library.login.net.LoginManager;

import org.apache.http.Header;

/**
 * Created by by.huang on 2015/5/29.
 */
public class HttpClient {

    private static final int TIME_OUT = 30 * 1000;
    private static HttpClient mInstance;
    private static Byte[] syncByte = new Byte[0];

    public interface OnHttpListener {
        void OnStart();

        void OnSuccess(RequestStatu statu, Object respondObject);

        void OnFail(RequestStatu statu, String resons);

    }

    public enum RequestStatu {NetFail, DataNull, Success, Unknow, DownLoadFail}

    public static HttpClient getInstance() {
        if (mInstance == null) {
            synchronized (syncByte) {
                if (mInstance == null) {
                    mInstance = new HttpClient();
                }
            }
        }
        return mInstance;
    }

    /**
     * get请求(网关)
     *
     * @param url
     * @param params
     * @param listener
     */
    public void get_v2(String url, RequestParams params, final OnHttpListener listener) {
        String header = LoginManager.getInstance().generateOAuthHeader("GET", url);
        if (listener == null) {
            return;
        }
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        listener.OnStart();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIME_OUT);
        client.addHeader("Authorization", header);
        client.get(MyApplication.mApplication.getApplicationContext(), url, params, getHandler(listener));
    }

    /**
     * get请求
     *
     * @param url
     * @param listener
     */
    public void get(String url, final OnHttpListener listener) {
        get(url, null, null, listener);
    }

    /**
     * get请求
     *
     * @param url
     * @param headers
     * @param listener
     */
    public void get(String url, Header[] headers, final OnHttpListener listener) {
        get(url, headers, null, listener);
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param listener
     */
    public void get(String url, RequestParams params, final OnHttpListener listener) {
        get(url, null, params, listener);
    }

    /**
     * get请求
     *
     * @param url
     * @param headers
     * @param params
     * @param listener
     */
    public void get(String url, Header[] headers, RequestParams params, final OnHttpListener listener) {
        if (listener == null) {
            return;
        }
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        listener.OnStart();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIME_OUT);
        client.get(MyApplication.mApplication.getApplicationContext(), url, headers, params, getHandler(listener));
    }

    /**
     * get请回调
     *
     * @param listener
     * @return
     */
    private AsyncHttpResponseHandler getHandler(final OnHttpListener listener) {
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                ByLogout.out("请求返回->" + jsonStr);
                ByLogout.out("statusCode->" + statusCode);
                if (statusCode == 200) {
                    if (TextUtils.isEmpty(jsonStr)) {
                        listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                    } else {
                        listener.OnSuccess(RequestStatu.Success, jsonStr);
                    }
                } else {
                    listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                }
                Utils.showShortToast(Utils.getString(R.string.httpclient_unknow_fail));
            }
        };
        return handler;
    }

    /**
     * post请求
     *
     * @param url
     * @param listener
     */
    public void post(String url, OnHttpListener listener) {
        post(url, null, listener);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param listener
     */
    public void post_v2(String url, RequestParams params, OnHttpListener listener) {
        String header = LoginManager.getInstance().generateOAuthHeader("POST", url);
        if (listener == null) {
            return;
        }
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", header);
        client.post(MyApplication.mApplication, url, params, postHandler(listener));
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param listener
     */
    public void post(String url, RequestParams params, OnHttpListener listener) {
        if (listener == null) {
            return;
        }
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(MyApplication.mApplication, url, params, postHandler(listener));
    }

    /**
     * post请求
     *
     * @param url
     * @param headers
     * @param params
     * @param contentType
     * @param listener
     */
    public void post(String url, Header[] headers, RequestParams params, String contentType, OnHttpListener listener) {
        if (listener == null) {
            return;
        }
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(MyApplication.mApplication, url, headers, params, contentType, postHandler(listener));
    }


    /**
     * post请求回调
     */
    private AsyncHttpResponseHandler postHandler(final OnHttpListener listener) {
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listener.OnSuccess(RequestStatu.Success, new String(responseBody));
                } else {
                    listener.OnSuccess(RequestStatu.Unknow, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                Utils.showShortToast(Utils.getString(R.string.httpclient_upload_fail));
            }
        };
        return handler;
    }
}
