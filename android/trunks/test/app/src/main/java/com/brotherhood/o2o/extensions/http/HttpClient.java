package com.brotherhood.o2o.extensions.http;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.brotherhood.o2o.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

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

    ;


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

    public void get(String url, final OnHttpListener listener) {
        get(url, null, null, listener);
    }

    public void get(String url, Header[] headers, final OnHttpListener listener) {
        get(url, headers, null, listener);
    }

    public void get(String url, RequestParams params, final OnHttpListener listener) {
        get(url, null, params, listener);
    }

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

    private AsyncHttpResponseHandler getHandler(final OnHttpListener listener) {
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                ByLogout.out("请求返回->"+jsonStr);
                ByLogout.out("statusCode->"+statusCode);
                if (statusCode == 200) {
                    if (TextUtils.isEmpty(jsonStr)) {
                        listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                        Utils.showShortToast(Utils.getString(R.string.httpclient_data_null));
                    } else {
                        listener.OnSuccess(RequestStatu.Success, jsonStr);
                        Utils.showShortToast(Utils.getString(R.string.httpclient_success));
                    }
                } else {
                    listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                    Utils.showShortToast(Utils.getString(R.string.httpclient_unknow_fail));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                Utils.showShortToast(Utils.getString(R.string.httpclient_unknow_fail));
            }
        };
        return handler;
    }

    public void post(String url, OnHttpListener listener) {
        post(url, null, listener);
    }

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


    private AsyncHttpResponseHandler postHandler(final OnHttpListener listener) {
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = responseBody.toString();
                if (statusCode == 200) {
                    listener.OnSuccess(RequestStatu.Success, jsonStr);
                    Utils.showShortToast(Utils.getString(R.string.httpclient_upload_success));
                } else {
                    listener.OnSuccess(RequestStatu.Unknow, jsonStr);
                    Utils.showShortToast(Utils.getString(R.string.httpclient_upload_fail));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.OnFail(RequestStatu.Unknow, responseBody.toString());
                Utils.showShortToast(Utils.getString(R.string.httpclient_upload_fail));
            }
        };
        return handler;
    }


    public void download(String downUrl, OnHttpListener listener) {
        if (!Utils.isNetWorkOk()) {
            listener.OnFail(RequestStatu.NetFail, Utils.getString(R.string.httpclient_network_fail));
            return;
        }
        if (listener != null) {
            listener.OnStart();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIME_OUT);
        client.get(MyApplication.mApplication.getApplicationContext(), downUrl, downHandler(listener));
    }


    private AsyncHttpResponseHandler downHandler(final OnHttpListener listener) {
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null) {
                    listener.OnSuccess(RequestStatu.Success, responseBody);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.OnFail(RequestStatu.DownLoadFail, error.getMessage());
                }
            }

        };
        return handler;
    }


    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    private void download(String downloadUrl)
    {
        DownloadManager downloadManager = (DownloadManager)MyApplication.mApplication.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
    }
}
