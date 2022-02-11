package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.account.VerifyCode;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnCommonResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.wrapper.AccountWrapperRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.account.SendPhoneVerifyCodeButton;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.FastBlur;
import com.brotherhood.o2o.util.ViewUtil;
import com.brotherhood.o2o.wrapper.DGCPassWrapper;
import com.dd.CircularProgressButton;
import com.tencent.mm.sdk.openapi.IWXAPI;

/**
 * Created by by.huang on 2015/7/17.
 */
public class SplashLoginActivity extends BaseActivity {

    @ViewInject(id = R.id.llLoginPhoneBtn, clickMethod = "onPhoneLogin")
    private LinearLayout mLlLoginPhone;

    @ViewInject(id = R.id.llLoginWechatBtn, clickMethod = "onWeChatLogin")
    private LinearLayout mLlLoginWeChat;

    @ViewInject(id = R.id.toOverseas, clickMethod = "toOverseas")
    private Button mToOverseas;

    @ViewInject(id = R.id.tvLoginPrivacy, clickMethod = "toServiceAgreement")
    private TextView mTvPrivacy;

    @ViewInject(id = R.id.ivLoginBg)
    private ImageView mImageView;

    //手机登录部分布局
    @ViewInject(id = R.id.ivCommonBackBtn, clickMethod = "back")
    private ImageView mIvBack;

    @ViewInject(id = R.id.btnLoginSendVerifyCode, clickMethod = "requestVerifyCode")
    private SendPhoneVerifyCodeButton btnSendVerifyCode;

    @ViewInject(id = R.id.etLoginPhoneCountry)
    private EditText mEtPhoneCountry;

    @ViewInject(id = R.id.etLoginPhoneNo)
    private EditText mEtPhoneNumber;

    @ViewInject(id = R.id.etLoginVerifyCode)
    private EditText mEtVerifyCode;

    //@ViewInject(id = R.id.btnLogin, clickMethod = "login")
    //private LoginButton mLoginButton;

    @ViewInject(id = R.id.cpbLogin, clickMethod = "login")
    private CircularProgressButton cpbLogin;

    @ViewInject(id = R.id.llLoginPhoneRoot)
    private LinearLayout mLlRoot;

    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;

    private VerifyCode mVerifyCode;
    private String TAG = "SplashLoginActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_login_layout;
    }

    /**
     * 微信登录
     * @param view
     */
    public void onWeChatLogin(View view) {
        Constants.LOGIN_TYPE = Constants.LOGIN_TYPE_WX;
        DefaultSharePrefManager.putString(SharePrefConstant.PREFER_LOGIN_TYPE, Constants.LOGIN_TYPE);
        IWXAPI api = DGCPassWrapper.createWXAPI(this);
        DGCPassWrapper.loginWithWechat(api);
    }

    /**
     * 手机登录
     * @param view
     */
    public void login(View view) {

        String phone = mEtPhoneNumber.getText().toString().replace(" ", "");
        DefaultSharePrefManager.putString(SharePrefConstant.LAST_LOGIN_PHONE, phone);
        String verifyCode = mEtVerifyCode.getText().toString().replace(" ", "");
        if (!TextUtils.isDigitsOnly(phone) || phone.length() != 11) {
            DisplayUtil.showToast(SplashLoginActivity.this, getString(R.string.login_phone_regular_error));
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            DisplayUtil.showToast(SplashLoginActivity.this, getString(R.string.login_verifycode_notnull));
            return;
        }

        if(mVerifyCode == null) {
            ColorfulToast.orangeNoIcon(this, getResources().getString(R.string.get_verify_code), Toast.LENGTH_LONG);
            return;
        }

        Constants.LOGIN_TYPE = Constants.LOGIN_TYPE_M;
        DefaultSharePrefManager.putString(SharePrefConstant.PREFER_LOGIN_TYPE, Constants.LOGIN_TYPE);

        //显示加载中
        //final Dialog dialog = DialogUtil.createLoadingDialog(SplashLoginActivity.this, "加载中...");
        //dialog.show();
        cpbLogin.setIndeterminateProgressMode(true);
        cpbLogin.setProgress(50);
        if (mVerifyCode.mType == VerifyCode.TYPE_LOGIN) {//登录成功，跳转到雷达页
            AccountWrapperRequest.loginWithVerifyCode(this, phone, verifyCode, new OnCommonResponseListener<String>() {
                @Override
                public void onSuccess(String data) {
//                    MainActivity.show(SplashLoginActivity.this);
//                    finish();
                }

                @Override
                public void onFailed(String errorMsg) {
                    //dialog.dismiss();
                    cpbLogin.setProgress(0);
                    ColorfulToast.orange(SplashLoginActivity.this, errorMsg, Toast.LENGTH_SHORT);
                }
            });
        } else if (mVerifyCode.mType == VerifyCode.TYPE_REGISTRY) {//注册成功，跳转到完善资料页
            AccountWrapperRequest.registerWithVerifyCode(phone, verifyCode, new OnCommonResponseListener<String>() {
                @Override
                public void onSuccess(String data) {
                    AccountWrapperRequest.getUserInfo(SplashLoginActivity.this, null, new OnResponseListener<UserInfo>() {
                        @Override
                        public void onSuccess(int code, String msg, UserInfo userInfo, boolean cache) {

                        }

                        @Override
                        public void onFailure(int code, String msg) {

                        }
                    });
                    //SetPersonInfoActivity.show(SplashLoginActivity.this);
                    //finish();
                }

                @Override
                public void onFailed(String errorMsg) {
                    //dialog.dismiss();
                    cpbLogin.setProgress(0);
                    ColorfulToast.orange(SplashLoginActivity.this, errorMsg, Toast.LENGTH_SHORT);
                }
            });
        }
    }

    public void onPhoneLogin(View view) {
        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache(true);
        //获取当前窗口快照，相当于截屏
        final Bitmap bmp = mImageView.getDrawingCache();
        FastBlur.applyBlur(SplashLoginActivity.this, bmp, mLlRoot);
        mLlRoot.setAnimationCacheEnabled(false);
        TranslateAnimation animation = getShowPhoneLoginAnim();
        mLlRoot.startAnimation(animation);
        mLlRoot.setVisibility(View.VISIBLE);

        //回显最后一次登陆手机号
        String lastPhone = DefaultSharePrefManager.getString(SharePrefConstant.LAST_LOGIN_PHONE,"");
        if(!TextUtils.isEmpty(lastPhone)) {
            mEtPhoneNumber.setText(lastPhone);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvPrivacy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvPrivacy.getPaint().setAntiAlias(true);//抗锯齿
        //dealPhoneLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (ViewUtil.isVisible(mLlRoot)) {
                back(null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SplashLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 处理手机登录界面、逻辑
     */
    private void dealPhoneLogin() {
        initEditActionListener(mEtPhoneNumber);
        initEditActionListener(mEtVerifyCode);
        mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //String phoneNo = mEtPhoneNumber.getText().toString().replace(" ", "");
                //String verifyCode = mEtVerifyCode.getText().toString().replace(" ", "");
                //if (!TextUtils.isEmpty(phoneNo) && !TextUtils.isEmpty(verifyCode)) {
                //    mLoginButton.setLoginAble(true);
                //} else {
                //    mLoginButton.setLoginAble(false);
                //}
            }
        });

        mEtVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //String phoneNo = mEtPhoneNumber.getText().toString().replace(" ", "");
                //String verifyCode = mEtVerifyCode.getText().toString().replace(" ", "");
                //if (!TextUtils.isEmpty(phoneNo) && !TextUtils.isEmpty(verifyCode)) {
                //    cpbEmailLogin.setLoginAble(true);
                //} else {
                //    mLoginButton.setLoginAble(false);
                //}
            }
        });
    }

    private void initEditActionListener(final EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = editText.getText().toString().replace(" ", "");
                    editText.setSelection(key.length());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取手机登录界面展示动画
     *
     * @return
     */
    private TranslateAnimation getShowPhoneLoginAnim() {
        if (mShowAction == null) {
            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
        }
        return mShowAction;
    }

    /**
     * 获取手机登录界面隐藏动画
     *
     * @return
     */
    private TranslateAnimation getHidePhoneLoginAnim() {
        if (mHiddenAction == null) {
            mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    2.0f);
            mHiddenAction.setDuration(500);
        }
        return mHiddenAction;
    }

    /**
     * 获取验证码
     *
     * @param view
     */
    public void requestVerifyCode(View view) {
        final String phoneNumber = mEtPhoneNumber.getText().toString();
        if(phoneNumber.length() != 11) {
            ColorfulToast.orangeNoIcon(this, getResources().getString(R.string.phone_number_wrong), Toast.LENGTH_SHORT);
            return;
        }
        if (btnSendVerifyCode.getTag() == null || !((boolean) btnSendVerifyCode.getTag())) {
            //final String phoneNumber = mEtPhoneNumber.getText().toString();
            if (!TextUtils.isEmpty(phoneNumber)) {
                btnSendVerifyCode.setTag(true);
                btnSendVerifyCode.countTimeStart();
                AccountWrapperRequest.sendLoginOrRegistryVerifyCode(phoneNumber, new OnCommonResponseListener<VerifyCode>() {
                    @Override
                    public void onSuccess(VerifyCode data) {
                        //ColorfulToast.orangeNoIcon(SplashLoginActivity.this, "获取验证码成功", Toast.LENGTH_SHORT);
                        LogManager.w(TAG, data.mType+"-------------");
                        mVerifyCode = data;
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        ColorfulToast.orange(SplashLoginActivity.this, errorMsg, Toast.LENGTH_SHORT);
                    }
                });
            } else {
                DisplayUtil.showToast(SplashLoginActivity.this, getResources().getString(R.string.updatephone_phone_null));
            }
        }

        //验证码EditText获取焦点
        String tempCode = mEtVerifyCode.getText().toString();
        mEtVerifyCode.requestFocus();
        if (tempCode != null) {
            mEtVerifyCode.setSelection(tempCode.length());
        }

    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        TranslateAnimation animation = getHidePhoneLoginAnim();
        mLlRoot.startAnimation(animation);
        mLlRoot.setVisibility(View.GONE);
        DisplayUtil.hideKeyboard(SplashLoginActivity.this);
    }

    /**
     * 切换海外版
     *
     * @param view
     */
    public void toOverseas(View view) {
        DefaultSharePrefManager.putBoolean(SharePrefConstant.LOGIN_VERSION, true);
        DeviceUtil.rebootApp(SplashLoginActivity.this);
    }

    /**
     * 跳到服务协议
     * @param view
     */
    public void toServiceAgreement(View view) {
        ServiceAgreementActivity.show(SplashLoginActivity.this);
    }

}
