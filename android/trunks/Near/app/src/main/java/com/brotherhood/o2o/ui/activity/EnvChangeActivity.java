package com.brotherhood.o2o.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.ActivityUtils;
import com.brotherhood.o2o.util.DialogUtil;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;

/**
 * Created by billy.shi on 2016/1/18.
 */
public class EnvChangeActivity extends BaseActivity {

    @ViewInject(id = R.id.btTest)
    private Button mBtTest;

    @ViewInject(id = R.id.btDev)
    private Button mBtDev;

    @ViewInject(id = R.id.btOffical)
    private Button mBtFinish;

    @ViewInject(id = R.id.tvShowEnv)
    private TextView mTvShowEnv;

    private IDSEnvManager mManager = IDSEnvManager.getInstance();
    private IDSEnvManager.IDSEnv mEnv;

    private String currentEnvTest ="当前为：测试环境";
    private String currentEnvDev ="当前为：开发环境";
    private String currentEnvOffical ="当前为：正式环境";
    private String isTestEnv = "当前已是测试环境";
    private String isDevEnv = "当前已是开发环境";
    private String isOfficalEnv = "当前已是正式环境";


    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_env_change_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.tan)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.change_environment, R.color.black);


        mEnv = mManager.getEnv();
        switch (mEnv) {
            case TEST:
                mTvShowEnv.setText(currentEnvTest);
                break;
            case DEV:
                mTvShowEnv.setText(currentEnvDev);
                break;
            case OFFICIAL:
                mTvShowEnv.setText(currentEnvOffical);
                break;
        }

        mBtTest.setOnClickListener(this);
        mBtDev.setOnClickListener(this);
        mBtFinish.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Dialog dialog = DialogUtil.createLoadingDialog(this,"");
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;

            case R.id.btTest:

                if(mManager.getEnv() == IDSEnvManager.IDSEnv.TEST) {
                    ColorfulToast.green(this, isTestEnv, Toast.LENGTH_LONG);
                    return;
                }
                dialog.show();

                mManager.setEnv(IDSEnvManager.IDSEnv.TEST);
                DefaultSharePrefManager.putString(SharePrefConstant.IDSENV, "TEST");
                changeEnv();
                break;

            case R.id.btDev:
                if(mManager.getEnv() == IDSEnvManager.IDSEnv.DEV) {
                    ColorfulToast.green(this, isDevEnv, Toast.LENGTH_LONG);
                    return;
                }
                dialog.show();
                mManager.setEnv(IDSEnvManager.IDSEnv.DEV);
                DefaultSharePrefManager.putString(SharePrefConstant.IDSENV, "DEV");
                changeEnv();
                break;

            case R.id.btOffical:
                if(mManager.getEnv() == IDSEnvManager.IDSEnv.OFFICIAL) {
                    ColorfulToast.green(this, isOfficalEnv, Toast.LENGTH_LONG);
                    return;
                }
                dialog.show();
                mManager.setEnv(IDSEnvManager.IDSEnv.OFFICIAL);
                DefaultSharePrefManager.putString(SharePrefConstant.IDSENV, "OFFICIAL");
                changeEnv();
                break;
        }
    }

    private void changeEnv() {
        NearBugtagsWrapper.init();
        AccountManager.getInstance().logout();
        DialogUtil.createLoadingDialog(this, "环境已切换，请直接退出，程序重启").show();
       // ActivityUtils.getScreenManager().popAllActivityExceptOne(SplashLoginActivity.class);
       //System.exit(0);//退出进程
        //finish();
    }

}
