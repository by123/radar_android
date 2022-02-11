package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.UmengAnalysisUtil;
//import com.brotherhood.o2o.wrapper.DLOGWrapper;

public class SplashActivity extends BaseActivity {

    public static void show(Context context){
        Intent it = new Intent(context, SplashActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(it);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NearApplication.mInstance.setInitFromLogin(true);
        //初始化全局通用数据
        LocationManager.getInstance().initLocation(LocationManager.BD_LOACTION_TYPE);
        //LocationManager.getInstance().initLocation(LocationManager.OVERSEA_LOACTION_TYPE);
//        DLOGWrapper.init();
//        DGCPushServiceWrapper.init();
        UmengAnalysisUtil.init();
        setScreen();

        IDSIMManager.getInstance().initIP();


        TaskExecutor.scheduleTaskOnUiThread(   new Runnable() {
            @Override
            public void run() {
                //是否有本地登录信息
                boolean isLogin = AccountManager.getInstance().isLogin();
                if (isLogin) {
                    Constants.LOGIN_TYPE = DefaultSharePrefManager.getString(SharePrefConstant.PREFER_LOGIN_TYPE, Constants.LOGIN_TYPE_M);
                    MainActivity.show(SplashActivity.this);
//                    MainActivity.showFromLogin(SplashActivity.this);
                } else {
                    //登陆版本， false为中国版， true为海外版
                   // boolean loginVersion = DefaultSharePrefManager.getBoolean(SharePrefConstant.LOGIN_VERSION, true);
//                    if (!loginVersion){
//                        SplashLoginActivity.show(SplashActivity.this);
//                    } else {
                        OverseaSplashLoginActivity.show(SplashActivity.this);
//                    }
                }
                finish();
            }
        }, 1500);
    }

    private void setScreen() {
        Constants.SCREEN_WIDTH = DisplayUtil.getScreenWidth(this);
        Constants.SCREEN_HEIGHT = DisplayUtil.getScreenHeight(this);
        Constants.proportion = Constants.SCREEN_HEIGHT / Constants.DEFAULT_HEIGHT;
    }

}
