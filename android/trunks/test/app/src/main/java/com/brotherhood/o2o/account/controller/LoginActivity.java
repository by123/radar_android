package com.brotherhood.o2o.account.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.account.model.LoginUserInfo;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.widget.ColorfulToast;
import com.brotherhood.o2o.widget.ProgressButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/3.
 */
public class LoginActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.field_account)
    EditText mAccountField;
    @InjectView(R.id.field_password)
    EditText mPasswordField;
    @InjectView(R.id.btn_login)
    ProgressButton mLoginButton;
    @InjectView(R.id.btn_send)
    Button mSendButton;

    private Runnable mCountDownTaskOrNil;
    private Handler mHandler;

    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.account_act_login);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        setErrorText(null);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginButtonClick();
                }
                return true;
            }
        });
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClick() {
        if (true) {
            ColorfulToast.orange(this, "请输入验证码哦", Toast.LENGTH_SHORT);
            return;
        }

        Utils.hideKeyboard(mAccountField, mPasswordField);
        String account = mAccountField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            setErrorText("账号不能为空");
        } else if (TextUtils.isEmpty(account)) {
            setErrorText("密码不能为空");
        } else {
            setErrorText(null);
            mLoginButton.setIsProcessing(true);
            AccountURLFetcher.login(account, password, new BaseURLFetcher.Callback<LoginUserInfo>() {
                @Override
                public void onCallback(LoginUserInfo dataOrNil, String errorOrNil) {
                    mLoginButton.setIsProcessing(false);
                    if (!TextUtils.isEmpty(errorOrNil)) {
                        setErrorText(errorOrNil);
                    } else {
                        assert dataOrNil != null;
                        setErrorText("登录成功");
                    }
                }
            });
        }
    }

    @OnClick(R.id.btn_send)
    void onSendButtonClick() {
        setSendButtonEnable(false);
        if (mCountDownTaskOrNil != null) {
            mHandler.removeCallbacks(mCountDownTaskOrNil);
        }
        mCountDownTaskOrNil = new Runnable() {
            int mRemainingTime = Constants.SEND_VERIFY_CODE_DURATION_IN_SECOND;

            @Override
            public void run() {
                if (mRemainingTime >= 0) {
                    mSendButton.setText(mRemainingTime + "秒后重新获取");
                    mRemainingTime--;
                    mHandler.postDelayed(this, 1000L);
                } else {
                    mSendButton.setText("获取验证码");
                    setSendButtonEnable(true);
                }
            }
        };
        mHandler.post(mCountDownTaskOrNil);
    }

    private void sendVerifyCode() {
    }

    private void setSendButtonEnable(boolean isEnable) {
        if (isEnable) {
            mSendButton.setTextColor(Color.parseColor("#e93e44"));
            mSendButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
        } else {
            mSendButton.setTextColor(Color.parseColor("#585858"));
            mSendButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
        }
    }

    private void setErrorText(String errorOrNil) {
    }
}
