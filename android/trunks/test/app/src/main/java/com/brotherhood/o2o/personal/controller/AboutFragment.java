package com.brotherhood.o2o.personal.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.SplashActivity;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.widget.webview.WebViewActivity;

import javax.xml.parsers.FactoryConfigurationError;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/6/9.
 */
public class AboutFragment extends BaseFragment implements View.OnClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getMainActivity().isNeedBottomBar(false);
        initData();
    }

    private void initData() {

        mCallTxt.setText(Constants.CALL);
        ByLogout.out(DeviceInfoUtils.defaultHelper().getAppVersionName());
        mCurrentVersionTxt.setText("v" + DeviceInfoUtils.defaultHelper().getAppVersionName());
        mVersionTxt.setText("v" + DeviceInfoUtils.defaultHelper().getAppVersionName());

        mAgreementLayout.setOnClickListener(this);
        mUpdateLayout.setOnClickListener(this);
        mWelcomeLayout.setOnClickListener(this);
        mCallLayout.setOnClickListener(this);
        mGradeLayout.setOnClickListener(this);

        getMainActivity().setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getMainActivity() != null) {
                    getMainActivity().swichToFragment(new PersonalFragment(), false);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {

        if (view == mAgreementLayout) {
            WebViewActivity.show("www.baidu.com");
        } else if (view == mUpdateLayout) {
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getMainActivity(), "正在检查更新", Toast.LENGTH_SHORT).show();
            getMainActivity().newThread(new BaseActivity.OnThreadListener() {
                @Override
                public void doInThread() {
                    Toast.makeText(getMainActivity(), "已是最新", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }, 3000);

        } else if (view == mWelcomeLayout) {
            startActivity(new Intent(getMainActivity(), SplashActivity.class));
        } else if (view == mCallLayout) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                    + mCallTxt.getText().toString()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view == mGradeLayout) {
            Toast.makeText(getMainActivity(), "开发中...", Toast.LENGTH_SHORT).show();
        }
    }
}
