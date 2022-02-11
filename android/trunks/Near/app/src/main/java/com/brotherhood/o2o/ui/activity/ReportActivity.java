package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.ReportUserRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.ViewUtil;

/**
 * 举报
 */
public class ReportActivity extends BaseActivity {

    @ViewInject(id = R.id.tvReportSend, clickMethod = "report")
    private TextView mTvSend;

    @ViewInject(id = R.id.rgReportReson)
    private RadioGroup mRadioGroup;

    private String mUid;
    private ReportUserRequest mReportUserRequest;
    private String mReason;

    public static void show(Context context, String uid){
        Intent it = new Intent(context, ReportActivity.class);
        it.putExtra(BundleKey.REPORT_USER_KEY, uid);
        context.startActivity(it);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_layout;
    }

    /**
     * 举报
     * @param view
     */
    public void report(View view){
        sendRequest();
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
        mUid = getIntent().getStringExtra(BundleKey.REPORT_USER_KEY);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.report_title, R.color.black);

        initEvent();
    }

    private void initEvent(){
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mTvSend.setTextColor(getResources().getColor(R.color.white));
                ViewUtil.setViewBackground(mTvSend, R.drawable.login_btn_bg);
                mRadioGroup.check(checkedId);
                switch (checkedId){
                    case R.id.rbReportSexy://色情
                        mReason = "1";
                        break;
                    case R.id.rbReportAd://广告
                        mReason = "2";
                        break;
                    case R.id.rbReportAbuse://辱骂
                        mReason = "3";
                        break;
                    case R.id.rbReportCheat://欺诈
                        mReason = "4";
                        break;
                    case R.id.rbReportSensitive://政治敏感
                        mReason = "5";
                        break;
                    case R.id.rbReportIllegal://违法
                        //mReason = getString(R.string.report_reason_illegal);
                        mReason = "6";
                        break;
                }
            }
        });

    }

    /**
     * 执行举报
     */

    private void sendRequest(){
        if (TextUtils.isEmpty(mUid)){
            return;
        }
        if (TextUtils.isEmpty(mReason)){
            ColorfulToast.orange(ReportActivity.this, getString(R.string.report_reason_is_empty),Toast.LENGTH_SHORT);
            return;
        }
        if (mReportUserRequest == null){
            mReportUserRequest = ReportUserRequest.createReportUserRequest(mUid, mReason, new OnResponseListener<String>() {
                @Override
                public void onSuccess(int code, String msg, String s, boolean cache) {
                    ColorfulToast.green(ReportActivity.this, getString(R.string.report_success), Toast.LENGTH_SHORT);
                   // ReportSuccessActivity.show(ReportActivity.this);
                    finish();
                }

                @Override
                public void onFailure(int code, String msg) {//getString(R.string.report_failed)
                    ColorfulToast.orange(ReportActivity.this, msg, Toast.LENGTH_SHORT);
                }
            });
        }
        mReportUserRequest.sendRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReportUserRequest != null){
            mReportUserRequest.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
        }
    }
}
