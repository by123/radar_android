package com.brotherhood.o2o.account.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.widget.ProgressButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class FindPasswordActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.field_phone)
    EditText mPhoneField;
    @InjectView(R.id.field_code)
    EditText mCodeField;
    @InjectView(R.id.field_password)
    EditText mPasswordField;
    @InjectView(R.id.label_error)
    TextView mErrorLabel;
    @InjectView(R.id.btn_send)
    ProgressButton mSendButton;
    @InjectView(R.id.btn_done)
    ProgressButton mDoneButton;
    private Handler mHandler;
    private Runnable mRunningTask;

    private TextWatcher mTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateRegistryButtonState();
        }
    };

    public static void show(Context context) {
        Intent intent = new Intent(context, FindPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.account_act_find_password);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mPhoneField.addTextChangedListener(mTextChangedListener);
        mCodeField.addTextChangedListener(mTextChangedListener);
        mPasswordField.addTextChangedListener(mTextChangedListener);
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onDoneButtonClick();
                }
                return true;
            }
        });

        updateRegistryButtonState();
        setErrorText(null);
    }

    @OnClick(R.id.btn_send)
    void onSendVerifyCodeButtonClick() {
        String phone = mPhoneField.getText().toString().trim();
        DeviceInfoUtils.defaultHelper().hideAndShowKeyboard(this);
        if (phone.length() != 11) {
            setErrorText("请输入正确的手机号码");
        } else {
            setErrorText(null);
            mSendButton.setIsProcessing(true);
            AccountURLFetcher.sendFindPasswordVerifyCode(phone, new BaseURLFetcher.SimpleCallback() {
                @Override
                public void onCallback(boolean isSuccess, String errorOrNil) {
                    if (isSuccess) {
                        mSendButton.setEnabled(false);
                        performCountDownTask();
                        setErrorText("发送成功");
                    } else {
                        setErrorText(errorOrNil);
                    }
                    mSendButton.setIsProcessing(false);
                }
            });
        }
    }

    @OnClick(R.id.btn_done)
    void onDoneButtonClick() {
        String phone = mPhoneField.getText().toString().trim();
        String code = mCodeField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        Utils.hideKeyboard(new EditText[]{mPhoneField, mCodeField, mPasswordField});
        setErrorText(null);
        mDoneButton.setIsProcessing(true);
        AccountURLFetcher.resetPassword(phone, code, password, new BaseURLFetcher.SimpleCallback() {
            @Override
            public void onCallback(boolean isSuccess, String errorOrNil) {
                mDoneButton.setIsProcessing(false);
                if (!isSuccess) {
                    setErrorText(errorOrNil);
                } else {
                    setErrorText("找回密码成功");
                }
            }
        });
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClick() {
        finish();
    }

    private void setErrorText(String errorOrNil) {
        if (TextUtils.isEmpty(errorOrNil)) {
            mErrorLabel.setVisibility(View.INVISIBLE);
        } else {
            mErrorLabel.setText(errorOrNil);
            mErrorLabel.setVisibility(View.VISIBLE);
        }
    }

    private void updateRegistryButtonState() {
        String phone = mPhoneField.getText().toString().trim();
        String code = mCodeField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        boolean isAllFilled = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(password);
        mDoneButton.setEnabled(isAllFilled);
    }

    private void performCountDownTask() {
        Runnable task = new Runnable() {
            private int remainingSecond = Constants.SEND_VERIFY_CODE_DURATION_IN_SECOND;

            @Override
            public void run() {
                remainingSecond--;
                if (remainingSecond > 0) {
                    mSendButton.setText("获取验证码(" + remainingSecond + ")");
                    mHandler.postDelayed(this, 1000L);
                } else {
                    mSendButton.setText("获取验证码");
                    mSendButton.setEnabled(true);
                }
            }
        };
        if (mRunningTask != null) {
            mHandler.removeCallbacks(mRunningTask);
        }
        mRunningTask = task;
        mRunningTask.run();
    }
}
