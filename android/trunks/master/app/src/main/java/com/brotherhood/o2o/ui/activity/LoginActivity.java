package com.brotherhood.o2o.ui.activity;

import android.animation.ObjectAnimator;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.model.account.LoginUserInfo;
import com.brotherhood.o2o.model.account.VerifyCodeInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.DGCPassWrapper;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.ProgressButton;
import com.skynet.library.login.net.LoginCallBack;
import com.skynet.library.login.net.LoginError;
import com.skynet.library.login.net.LoginListener;
import com.skynet.library.login.net.LoginReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.lang.ref.WeakReference;

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
    @InjectView(R.id.field_code)
    EditText mCodeField;
    @InjectView(R.id.btn_login)
    ProgressButton mLoginButton;
    @InjectView(R.id.btn_send)
    Button mSendButton;
    @InjectView(R.id.webview)
    WebView mWebView;

    private Runnable mCountDownTaskOrNil;
    private Handler mHandler;
    private VerifyCodeInfo mVerifyCodeInfoOrNil;

    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.account_act_login);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.selector_img_close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.getVisibility() == View.VISIBLE) {
                    closeQQLoginView();
                    return;
                } else {
                    finish();
                }
            }
        });
        mCodeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginButtonClick();
                }
                return true;
            }
        });

        String lastAccount = loadLoginAccount();
        if (!TextUtils.isEmpty(lastAccount)) {
            mAccountField.setText(lastAccount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.getVisibility() == View.VISIBLE) {
                closeQQLoginView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClick() {
        Utils.hideKeyboard(mAccountField, mCodeField);
        final String account = mAccountField.getText().toString().trim();
        String verifyCode = mCodeField.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToast("手机号码不能为空哦");
        } else if (TextUtils.isEmpty(verifyCode)) {
            showToast("请输入验证码哦");
        } else {
            mLoginButton.setIsProcessing(true);
            Constants.LOGIN_TYOE = Constants.LOGIN_TYPE_M;
            if (mVerifyCodeInfoOrNil == null || mVerifyCodeInfoOrNil.mType == VerifyCodeInfo.TYPE_LOGIN) {
                AccountURLFetcher.login(account, verifyCode, new LoginEventHandler(this, account));
            } else {
                AccountURLFetcher.registry(account, verifyCode, new LoginEventHandler(this, account));
            }
        }
    }

    private static class LoginEventHandler implements BaseURLFetcher.Callback<LoginUserInfo> {
        private WeakReference<LoginActivity> mActRef;
        private String mAccount;

        public LoginEventHandler(LoginActivity activity, String account) {
            mActRef = new WeakReference<>(activity);
            mAccount = account;
        }

        @Override
        public void onCallback(LoginUserInfo dataOrNil, String errorOrNil) {
            if (mActRef.get() == null)
                return;
            LoginActivity act = mActRef.get();
            act.mLoginButton.setIsProcessing(false);
            if (!TextUtils.isEmpty(errorOrNil)) {
                act.showToast(errorOrNil);
            } else {
                assert dataOrNil != null;
                act.saveLoginAccount(mAccount);
                Utils.showShortToast("登录成功");
                act.finish();
            }
        }
    }

    private static class LoginByQQCallback implements BaseURLFetcher.Callback<LoginUserInfo> {

        @Override
        public void onCallback(LoginUserInfo dataOrNil, String errorOrNil) {
            if (dataOrNil == null) {
                ColorfulToast.orange(ContextUtils.context(), errorOrNil, Toast.LENGTH_SHORT);
            } else {
                Utils.showShortToast("登录成功");
            }
        }
    }

    @OnClick(R.id.btn_qq)
    void onQQLoginButtonClick() {
        Constants.LOGIN_TYOE = Constants.LOGIN_TYPE_QQ;
        mWebView.setTranslationY(mWebView.getHeight());
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("auth://")) {
                    AccountURLFetcher.loginWithQQCallback(url, new LoginByQQCallback());
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        ObjectAnimator animator = ObjectAnimator.ofFloat(mWebView, "translationY", 0);
        animator.setDuration(300);
        animator.start();
        LoginReq.getQQLoginUrl(new LoginListener() {
            @Override
            public void onSuccess(LoginCallBack.LoginCallBackInfo loginCallBackInfo) {
                LoginCallBack.LoginSocialInfo info = (LoginCallBack.LoginSocialInfo) loginCallBackInfo;
                mWebView.loadUrl(info.login_url);
            }

            @Override
            public void onFail(LoginError loginError) {
            }
        });
    }

    @OnClick(R.id.btn_wechat)
    void onWechatButtonClick() {
        Constants.LOGIN_TYOE = Constants.LOGIN_TYPE_WX;
        IWXAPI api = DGCPassWrapper.createWXAPI(this);
        DGCPassWrapper.loginWithWechat(api);
        finish();
    }

    @OnClick(R.id.btn_send)
    void onSendButtonClick() {
        String phone = mAccountField.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空哦");
        } else {
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
            AccountURLFetcher.sendLoginOrRegistryVerifyCode(phone, new SendCodeEventHandler(this));
        }
    }

    private static class SendCodeEventHandler implements BaseURLFetcher.Callback<VerifyCodeInfo> {

        private WeakReference<LoginActivity> mActRef;

        public SendCodeEventHandler(LoginActivity activity) {
            mActRef = new WeakReference<>(activity);
        }

        @Override
        public void onCallback(VerifyCodeInfo dataOrNil, String errorOrNil) {
            if (mActRef.get() == null) {
                return;
            }
            LoginActivity act = mActRef.get();

            if (dataOrNil == null) {
                act.showToast(errorOrNil);
                if (act.mCountDownTaskOrNil != null) {
                    act.mHandler.removeCallbacks(act.mCountDownTaskOrNil);
                    act.mCountDownTaskOrNil = null;
                    act.mSendButton.setText("获取验证码");
                    act.setSendButtonEnable(true);
                }
            } else {
                act.mVerifyCodeInfoOrNil = dataOrNil;
            }
        }
    }


    private void closeQQLoginView() {
        if (mWebView.getVisibility() == View.VISIBLE) {
            mWebView.setVisibility(View.INVISIBLE);
        }
    }

    private void saveLoginAccount(String account) {
        getSharedPreferences(null, Context.MODE_APPEND).edit().putString("account.activity.login.account", account).commit();
    }

    private String loadLoginAccount() {
        return getSharedPreferences(null, Context.MODE_APPEND).getString("account.activity.login.account", "");
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

    private void showToast(String errorOrNil) {
        ColorfulToast.orange(this, errorOrNil, Toast.LENGTH_SHORT);
    }
}
