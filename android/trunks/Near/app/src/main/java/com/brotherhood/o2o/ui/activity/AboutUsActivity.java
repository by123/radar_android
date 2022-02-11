package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.LanguageUtil;
import com.brotherhood.o2o.util.Res;

/**
 * Created by billy.shi on 2015/12/26.
 */
public class AboutUsActivity extends BaseActivity {

    @ViewInject(id = R.id.tvServiceAgreement)
    private TextView mTextView;

    @ViewInject(id = R.id.rlAboutUs)
    private RelativeLayout mRlAboutUs;

    @ViewInject(id = R.id.tv_version)
    private TextView tv_version;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us_layout;
    }


    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.personalfragment_about, R.color.black);

        //设置监听
        mTextView.setOnClickListener(this);
        mRlAboutUs.setOnClickListener(this);
        String version = Res.getString(R.string.version) + DeviceUtil.getVersionName(this);
        tv_version.setText(version);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.tvServiceAgreement:
                Intent intentService = new Intent(this, ServiceAgreementActivity.class);
                startActivity(intentService);
                break;
            case R.id.rlAboutUs:
                if (!(IDSEnvManager.getInstance().getEnv() == IDSEnvManager.IDSEnv.OFFICIAL)) {
                    Intent intentEnv = new Intent(this, EnvironmentActivity.class);
                    startActivity(intentEnv);
                }
                break;
        }
    }

    public static void show(Context context) {
        Intent it = new Intent(context, AboutUsActivity.class);
        context.startActivity(it);
    }
}
