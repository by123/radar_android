package com.brotherhood.o2o;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.DGCPassWrapper;
import com.brotherhood.o2o.extensions.DGCPushServiceWrapper;
import com.brotherhood.o2o.extensions.DLOGWrapper;
import com.brotherhood.o2o.extensions.UmengWrapper;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.widget.BasicDialog;
import com.github.snowdream.android.app.DownloadListener;
import com.github.snowdream.android.app.DownloadManager;
import com.github.snowdream.android.app.DownloadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by ZhengYi on 15/6/5.
 */
public class SplashActivity extends BaseActivity {
    private BasicDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        DGCPassWrapper.init();
        DLOGWrapper.init();
        DGCPushServiceWrapper.init();
        UmengWrapper.init();

        mDialog = new BasicDialog(this, new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                Toast.makeText(SplashActivity.this, "开始更新", Toast.LENGTH_SHORT).show();
                downloadNewVersion("http://42.48.1.73/m.wdjcdn.com/release/files/phoenix/4.29.1.8027/wandoujia-wandoujia_web_4.29.1.8027.apk");
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
                dialog.dismiss();
                goNext();
            }
        }).setConfirmTxt("更新").setCancelTxt("取消");

        checkUpdate();
    }

    private void goNext() {
        final long startTimeInMills = System.currentTimeMillis();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DLOGWrapper.onFinishLoading(computeLoadingDuration(startTimeInMills));
                finish();
                MainActivity.show(SplashActivity.this);
            }
        }, 3000L);
    }

    private long computeLoadingDuration(long startTimeInMills) {
        return System.currentTimeMillis() - startTimeInMills;
    }


    private void checkUpdate() {
        HttpClient.getInstance().get(Constants.URL_APP_UPDATE, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = new String(respondObject.toString());
//                try {
//                    JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("data");
//                    int versionCode = jsonObject.getInt("version_code");
//                    String versionName = jsonObject.getString("version_name");
//                    int versionMin = jsonObject.getInt("version_min");
//                    int isEnforce = jsonObject.getInt("is_enforce");
//                    String desc = jsonObject.getString("descript");
//                    String timestamp = jsonObject.getString("insert_timestamp");

                int versionMin = 1;
                int versionCode = 2;
                int isEnforce = 0;
                String timestamp = "2015-6-11";
                String desc = "更新";
                int version = DeviceInfoUtils.defaultHelper().getAppVersionCode();
                if (version < versionMin || isEnforce == 1) {
                    mDialog.setMainTxt(desc);
                    mDialog.setMinorTxt(timestamp);
                    mDialog.hideOneButton();
                    mDialog.setCancelable(false);
                    mDialog.show();
                } else if (version < versionCode) {
                    mDialog.setMainTxt(desc);
                    mDialog.setCancelable(false);
                    mDialog.setMinorTxt(timestamp);
                    mDialog.show();
                } else {
                    goNext();
                }

//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                    goNext();
//                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
                goNext();
            }

        });
    }

    private void downloadNewVersion(String url) {
        if (url == null) {
            return;
        }
        DownloadManager downloadManager = new DownloadManager(this);
        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.setUrl(url);
        downloadTask.setId(url.hashCode());
        downloadTask.setPath("/sdcard/1.apk");
        downloadManager.start(downloadTask, listener);
    }

    private DownloadListener listener = new DownloadListener() {

        @Override
        public void onStart() {
            super.onStart();
            ByLogout.out("开始下载");
        }

        @Override
        public void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            mDialog.setMinorTxt(values[0] + "%");
            ByLogout.out("下载中->" + values[0]);

        }

        @Override
        public void onSuccess(Object o) {
            super.onSuccess(o);
            ByLogout.out("下载成功");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File("/sdcard/1.apk")), "application/vnd.android.package-archive");
            startActivity(intent);
        }

        @Override
        public void onError(Throwable thr) {
            super.onError(thr);
            ByLogout.out("下载失败" + thr.getMessage());

        }
    };
}
