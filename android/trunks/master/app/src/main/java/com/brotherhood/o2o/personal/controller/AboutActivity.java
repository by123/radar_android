package com.brotherhood.o2o.personal.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.SplashActivity;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.ui.widget.webview.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/6/9.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.layout_agreement)
    View mAgreementLayout;

    @InjectView(R.id.layout_update)
    View mUpdateLayout;

    @InjectView(R.id.layout_welcome)
    View mWelcomeLayout;

    @InjectView(R.id.layout_call)
    View mCallLayout;

    @InjectView(R.id.layout_grade)
    View mGradeLayout;

    @InjectView(R.id.txt_version)
    TextView mVersionTxt;

    @InjectView(R.id.txt_call)
    TextView mCallTxt;

    @InjectView(R.id.txt_current_version)
    TextView mCurrentVersionTxt;

    @InjectView(R.id.progressbar)
    View mProgressBar;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCallTxt.setText(Constants.CALL);
        ByLogout.out(DeviceInfoUtils.defaultHelper().getAppVersionName());
        mCurrentVersionTxt.setText("v" + DeviceInfoUtils.defaultHelper().getAppVersionName());
        mVersionTxt.setText("v" + DeviceInfoUtils.defaultHelper().getAppVersionName());

        mAgreementLayout.setOnClickListener(this);
        mUpdateLayout.setOnClickListener(this);
        mWelcomeLayout.setOnClickListener(this);
        mCallLayout.setOnClickListener(this);
        mGradeLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        if (view == mAgreementLayout) {
            WebViewActivity.show("www.baidu.com");
        } else if (view == mUpdateLayout) {
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "正在检查更新", Toast.LENGTH_SHORT).show();
            newThread(new BaseActivity.OnThreadListener() {
                @Override
                public void doInThread() {
                    Toast.makeText(AboutActivity.this, "已是最新", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }, 3000);

        } else if (view == mWelcomeLayout) {
            startActivity(new Intent(AboutActivity.this, SplashActivity.class));
        } else if (view == mCallLayout) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                    + mCallTxt.getText().toString()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view == mGradeLayout) {
            Toast.makeText(this, "开发中...", Toast.LENGTH_SHORT).show();
        }
    }
}
