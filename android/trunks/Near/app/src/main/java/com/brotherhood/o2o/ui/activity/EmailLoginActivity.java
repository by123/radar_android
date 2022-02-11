package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnCommonResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.wrapper.AccountWrapperRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.dd.CircularProgressButton;

/**
 * Created by billy.shi on 2016/1/4.
 */
public class EmailLoginActivity extends BaseActivity {

    @ViewInject(id = R.id.etLoginEmail)
    private EditText mEtLoginEmail;

    @ViewInject(id = R.id.etLoginPassword)
    private EditText mEtLoginPassword;

    //@ViewInject(id = R.id.llEmailLogin, clickMethod = "emailLogin")
    //private LinearLayout mLlEmailLogin;

    /*@ViewInject(id = R.id.llLoginFacebookBtn, clickMethod = "loginToFacebook")
    private LinearLayout mLoginFacebookBtn;*/

    @ViewInject(id = R.id.tvForgotPassword, clickMethod = "forgotPassword")
    private TextView mForgetPassword;

    @ViewInject(id = R.id.cpbEmailLogin, clickMethod = "emailLogin")
    private CircularProgressButton cpbEmailLogin;

    private AccountWrapperRequest mAccountWrapperRequest;
    private boolean toggle = false;
    private String TAG = "EmailLoginActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_login_layout;
    }

    @Override
    protected boolean addOverlayActionBar() {
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
                .setBackImage(R.mipmap.login_phone_close)
                .setDivideColor(R.color.tan)
                .setHeadBackgroundColor(R.color.transparent)
                .setBaseTitle(R.string.login_with_email, R.color.white)
                .hideHorizontalDivide();

        String lastEmail = DefaultSharePrefManager.getString(SharePrefConstant.LAST_LOGIN_EMAIL,"");
        if(!TextUtils.isEmpty(lastEmail)) {
            mEtLoginEmail.setText(lastEmail);
            mEtLoginEmail.requestFocus();
            mEtLoginEmail.setSelection(lastEmail.length());
        }

        mForgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mForgetPassword.getPaint().setAntiAlias(true);//抗锯齿
    }


    /**
     * email登陆
     *
     * @param view
     */
    public void emailLogin(View view) {
        if(toggle) {
            return;
        }

        final String loginEmail = mEtLoginEmail.getText().toString().trim();
        String password = mEtLoginPassword.getText().toString().trim();

        DefaultSharePrefManager.putString(SharePrefConstant.LAST_LOGIN_EMAIL, loginEmail);
        if(TextUtils.isEmpty(loginEmail) || TextUtils.isEmpty(password)) {
            ColorfulToast.orangeNoIcon(this, getString(R.string.do_not_empty), Toast.LENGTH_LONG);
            return;
        }

        toggle = true;
        Constants.LOGIN_TYPE = Constants.LOGIN_TYPE_EMAIL;
        DefaultSharePrefManager.putString(SharePrefConstant.PREFER_LOGIN_TYPE, Constants.LOGIN_TYPE);
        AccountWrapperRequest.loginWithEmailCallback(EmailLoginActivity.this, loginEmail, password, new OnCommonResponseListener<String>() {
            @Override
            public void onSuccess(String data) {
                DefaultSharePrefManager.putString(SharePrefConstant.LAST_LOGIN_EMAIL, loginEmail);
                //跳转到雷达界面
                //            MainActivity.show(EmailLoginActivity.this);
                toggle = false;
                //清空除了mainActivity的activity
                //ActivityUtils.getScreenManager().popAllActivityExceptOne(MainActivity.class);


            }

            @Override
            public void onFailed(String errorMsg) {
                //提示登录失败
                //ColorfulToast.orange(EmailLoginActivity.this, "Username or password is wrong", Toast.LENGTH_SHORT);
                cpbEmailLogin.setProgress(0);
                toggle = false;


            }
        });


        cpbEmailLogin.setIndeterminateProgressMode(true);
        cpbEmailLogin.setProgress(50);
    }

    /**
     * 从email登陆界面跳转带facebook
     *
     * @param view
     */
    public void loginToFacebook(View view) {

    }

    /**
     * email登陆忘记密码
     *
     * @param view
     */
    public void forgotPassword(View view) {

        Intent intent = new Intent(this, SendEmailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, EmailLoginActivity.class);
        context.startActivity(intent);
    }
}
