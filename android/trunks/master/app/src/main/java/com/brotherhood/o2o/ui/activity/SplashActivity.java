package com.brotherhood.o2o.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.database.PreferenceHelper;
import com.brotherhood.o2o.extensions.BDLocationServiceWrapper;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.DGCPassWrapper;
import com.brotherhood.o2o.extensions.DGCPushServiceWrapper;
import com.brotherhood.o2o.extensions.DLOGWrapper;
import com.brotherhood.o2o.extensions.UmengWrapper;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;
import com.brotherhood.o2o.ui.widget.drop.ScrollerViewPager;
import com.brotherhood.o2o.ui.widget.drop.SpringIndicator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.snowdream.android.app.DownloadListener;
import com.github.snowdream.android.app.DownloadManager;
import com.github.snowdream.android.app.DownloadTask;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ZhengYi on 15/6/5.
 */
public class SplashActivity extends BaseActivity {
    private BasicDialog mDialog;

    @InjectView(R.id.img_bg)
    SimpleDraweeView mBgImg;

    @InjectView(R.id.viewpager)
    ScrollerViewPager mViewPager;

    @InjectView(R.id.indicator)
    SpringIndicator mSpringIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        BDLocationServiceWrapper.init();
        DGCPassWrapper.init();
        DLOGWrapper.init();
        DGCPushServiceWrapper.init();
        UmengWrapper.init();
        setScreen();
//        AccountComponent.shareComponent().autoLogin();

        ButterKnife.inject(this);
        ImageLoader.getInstance().setImageResource(mBgImg, R.drawable.welcome_bg);
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


        setViewPager();
        boolean isFirst = PreferenceHelper.sharePreference(this).getBoolean(Constants.FirstRun, true);
        if (isFirst) {
            PreferenceHelper.sharePreference(this).setBoolean(Constants.FirstRun, false);
            mViewPager.setVisibility(View.VISIBLE);
            mSpringIndicator.setVisibility(View.VISIBLE);
        } else {
            mViewPager.setVisibility(View.GONE);
            mSpringIndicator.setVisibility(View.GONE);
            checkUpdate();
        }
    }


    private void goNext() {
        final long startTimeInMills = System.currentTimeMillis();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DLOGWrapper.onFinishLoading(computeLoadingDuration(startTimeInMills));
                finish();
                if (PreferenceHelper.sharePreference(SplashActivity.this).getBoolean(Constants.FirstRun, true)) {
                    SplashLoginActivity.show(SplashActivity.this);
                } else {
                    MainActivity.show(SplashActivity.this);
                }
            }
        }, 3000L);
    }

    private long computeLoadingDuration(long startTimeInMills) {
        return System.currentTimeMillis() - startTimeInMills;
    }


    private void checkUpdate() {
        if (DeviceInfoUtils.defaultHelper().hasActiveNetwork()) {
            HttpClient.getInstance().get(Constants.URL_GET_APP_UPDATE, new HttpClient.OnHttpListener() {
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
                    int versionCode = 1;
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
        } else {
            goNext();
        }
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


    private void setViewPager() {
        final ArrayList<View> views = new ArrayList<View>();
        int []imgsRes={R.drawable.img_lobbypage_bg1,R.drawable.img_lobbypage_bg2,R.drawable.img_lobbypage_bg3,R.drawable.img_lobbypage_bg4};
        for (int i = 0; i < imgsRes.length; i++) {
            View rootView = LayoutInflater.from(this).inflate(R.layout.splash_viewpager_item, null);
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) rootView.findViewById(R.id.img_bg);
            ImageLoader.getInstance().setImageResource(simpleDraweeView, imgsRes[i]);
            if (i == imgsRes.length-1) {
                View nextBtn = rootView.findViewById(R.id.btn_next);
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final long startTimeInMills = System.currentTimeMillis();
                        DLOGWrapper.onFinishLoading(computeLoadingDuration(startTimeInMills));
                        finish();
                        SplashLoginActivity.show(SplashActivity.this);
                    }
                });
            }
            views.add(rootView);
        }

        mViewPager.setAdapter(new SplashPagerAdapter(views));
        mSpringIndicator.setViewPager(mViewPager);
    }

    public class SplashPagerAdapter extends PagerAdapter {
        public ArrayList<View> mListViews;

        public SplashPagerAdapter(ArrayList<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
    }

    private void setScreen() {
        Constants.SCREEN_WIDTH = Utils.getScreenWidth(this);
        Constants.SCREEN_HEIGHT = Utils.getScreentHeight(this);
        Constants.proportion = Constants.SCREEN_HEIGHT / Constants.DEFAULT_HEIGHT;
    }
}
