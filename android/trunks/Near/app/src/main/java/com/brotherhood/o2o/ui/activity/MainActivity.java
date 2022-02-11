package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.controller.ExploreComponent;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.request.wrapper.AccountWrapperRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.fragment.ExploreFragment;
import com.brotherhood.o2o.ui.fragment.SlideMenuFragment;
import com.brotherhood.o2o.util.ActivityUtils;
import com.brotherhood.o2o.util.DisplayUtil;
import com.skynet.library.login.net.LoginManager;


public class MainActivity extends BaseActivity {

    @ViewInject(id = R.id.container_drawer)
    private DrawerLayout mDrawerLayout;

    private long previousTime;
    private int count;
    private ExploreFragment mExploreFragment;
    private SlideMenuFragment mSlideMenuFragment;

    private final static String EXTRA_FROM_LOGIN = "EXTRA_FROM_LOGIN";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_layout;
    }

    public static void show(Context context) {
        Intent it = new Intent(context, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进来先清空登陆activity
        ActivityUtils.getScreenManager().popAllActivityExceptOne(MainActivity.class);
        boolean hasToken = LoginManager.getInstance().hasToken();
        boolean isLogout = LoginManager.getInstance().isLogout();
        boolean isLogin = AccountManager.getInstance().isLogin();
        if (isLogout) {//是否logout，调用了通行证logout接口时才为true，这里也要跑回去登陆界面
        } else {
            if (hasToken) {//有token的时候才能跑这里
                AccountWrapperRequest.loginWithLastData(this);

            } else {//没有token 要去登陆界面
            }
        }
        
        mExploreFragment = new ExploreFragment();
        mSlideMenuFragment = new SlideMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flMainExplore, mExploreFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fmMainSlideMenu, mSlideMenuFragment).commit();
    }

    public void destroy(){
        ExploreComponent.shareComponent().destroy();
        if (mExploreFragment != null){
            getSupportFragmentManager().beginTransaction().detach(mExploreFragment);
            mExploreFragment = null;
        }
        if (mSlideMenuFragment != null){
            getSupportFragmentManager().beginTransaction().detach(mSlideMenuFragment);
            mSlideMenuFragment = null;
        }
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected boolean containFragment() {
        return true;
    }


    public void toggleSlideMenu() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long time = System.currentTimeMillis() - previousTime;
            if (time > 2000) {
                count = 0;
            } else {
                if (count == 1) {
                    ActivityUtils.getScreenManager().popAllActivity();
                    destroy();
                    NearApplication.mInstance.getMessagePump().destroyMessagePump();
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return true;
                }
            }
            if (count == 0) {
                count = 1;
                previousTime = System.currentTimeMillis();
                DisplayUtil.showToast(this, R.string.near_exit_msg);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExploreFragment != null) {
            getSupportFragmentManager().beginTransaction().detach(mExploreFragment);
        }
        if (mSlideMenuFragment != null) {
            getSupportFragmentManager().beginTransaction().detach(mSlideMenuFragment);
        }
        AccountWrapperRequest.cancelGetUserInfoRequest();//取消获取用户信息请求

    }
}
