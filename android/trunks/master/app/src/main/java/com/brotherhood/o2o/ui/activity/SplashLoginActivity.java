package com.brotherhood.o2o.ui.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.model.account.LoginUserInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.DGCPassWrapper;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.skynet.library.login.net.LoginCallBack;
import com.skynet.library.login.net.LoginError;
import com.skynet.library.login.net.LoginListener;
import com.skynet.library.login.net.LoginReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/17.
 */
public class SplashLoginActivity extends BaseActivity {

    @InjectView(R.id.btn_wechat)
    View mWechatBtn;

    @InjectView(R.id.btn_qq)
    View mQQBtn;

    @InjectView(R.id.btn_others)
    View mOthersBtn;

    @InjectView(R.id.webview)
    WebView mWebView;

    private ProgressDialog mProgressDialog;

    @OnClick(R.id.btn_wechat)
    void OnWechatBtnClick() {
        Constants.LOGIN_TYOE = Constants.LOGIN_TYPE_WX;
        mProgressDialog.show();
        IWXAPI api = DGCPassWrapper.createWXAPI(this);
        DGCPassWrapper.loginWithWechat(api);
    }

    @OnClick(R.id.btn_qq)
    void OnQQBtnClick() {
        Constants.LOGIN_TYOE = Constants.LOGIN_TYPE_QQ;
        mWebView.setTranslationY(mWebView.getHeight());
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("auth://")) {
                    AccountURLFetcher.loginWithQQCallback(url, new LoginByQQCallback());
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

    @OnClick(R.id.btn_others)
    void OnOtherBtnClick() {
        MainActivity.show(this);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_act_splash_login);
        ButterKnife.inject(this);
        initView();
        registerLoginReceiver();
    }

    private void initView() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("正在登录，请稍后...");
    }

    private void registerLoginReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AccountComponent.ACTION_USER_LOGIN);
        filter.addAction(AccountComponent.ACTION_USER_NO_LOGIN);
        registerReceiver(mLoginReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoginReceiver);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SplashLoginActivity.class);
        context.startActivity(intent);
    }

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(AccountComponent.ACTION_USER_LOGIN)) {
                LoginSuccess();
            } else if (action.equalsIgnoreCase(AccountComponent.ACTION_USER_NO_LOGIN)) {
                LoginFail();
            }
        }
    };


    private void LoginFail() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void LoginSuccess() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        Utils.showShortToast("登录成功！");
        finish();
        MainActivity.show(SplashLoginActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = false;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private boolean isLogin = false;

    private class LoginByQQCallback implements BaseURLFetcher.Callback<LoginUserInfo> {

        @Override
        public void onCallback(LoginUserInfo dataOrNil, String errorOrNil) {
            if (dataOrNil == null) {
                ColorfulToast.orange(ContextUtils.context(), errorOrNil, Toast.LENGTH_SHORT);
            } else {
                if (!isLogin) {
                    LoginSuccess();
                }
            }
        }
    }
}
