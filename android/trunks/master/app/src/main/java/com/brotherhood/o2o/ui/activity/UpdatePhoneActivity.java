package com.brotherhood.o2o.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.model.account.UserInfo;
import com.brotherhood.o2o.request.VerifyCodeRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ProgressButton;
import com.brotherhood.o2o.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/31.
 */
public class UpdatePhoneActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.edit_phone)
    EditText mPhoneEdit;

    @InjectView(R.id.edit_verify)
    EditText mVerifyEdit;

    @InjectView(R.id.btn_bind)
    ProgressButton mBindBtn;

    @InjectView(R.id.btn_send)
    Button mSendBtn;

    private VerifyCodeRequest mRequest;

    @OnClick(R.id.btn_send)
    void onSendVerifyClick() {
        String phone = mPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Utils.showShortToast(R.string.updatephone_phone_null);
            return;
        }
        requestVerifyCode(phone);
    }

    @OnClick(R.id.btn_bind)
    void onBindBtnClick() {
        String phone = mPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Utils.showShortToast(R.string.updatephone_phone_null);
            return;
        }
        String verifycode = mVerifyEdit.getText().toString();
        if (TextUtils.isEmpty(verifycode)) {
            Utils.showShortToast(R.string.updatephone_verify_null);
            return;
        }

        if (mCode.equalsIgnoreCase(verifycode)) {
            UserInfo userInfo = AccountComponent.shareComponent().getmUserInfo();
            userInfo.mPhone = phone;
            userInfo.mVerifyCode = verifycode;
            AccountComponent.shareComponent().UpLoadUserInfo(this, userInfo, Constants.UPLOAD_PHONE);
            finish();
        } else {
            Utils.showShortToast(R.string.updatephone_verifycode_error);
        }

    }

    private String mCode;

    public static void show(Context context) {
        Intent intent = new Intent(context, UpdatePhoneActivity.class);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_act_update_phone);
        ButterKnife.inject(this);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.selector_img_close));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void requestVerifyCode(String phone) {
        mRequest = VerifyCodeRequest.createVerifyCodeRequest(phone, new OnResponseListener<String>() {
            @Override
            public void onSuccess(int code, String msg, String verifyCode, boolean cache) {
                //todo 根据接口返回数据结构处理
                //if (Utils.isRequestValid(jsonStr)) {
                //    try {
                //        JSONObject jsonObject = new JSONObject(jsonStr);
                //        mCode = jsonObject.getJSONObject("data").optString("code");
                        Utils.showShortToast(R.string.updatephone_verify_success);
                        mhandler.postDelayed(mRunnable, 0);
                //    } catch (JSONException e) {
                //        e.printStackTrace();
                //    }
                //} else {
                //    Utils.showShortToast(jsonStr);
                //}
            }

            @Override
            public void onFailure(int code, String msg) {
                Utils.showShortToast(R.string.server_error);
            }
        });
        mRequest.sendRequest();
        //AccountURLFetcher.requestVerifyCode(phone, new HttpClient.OnHttpListener() {
        //    @Override
        //    public void OnStart() {
        //
        //    }
        //
        //    @Override
        //    public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
        //
        //
        //    }
        //
        //    @Override
        //    public void OnFail(HttpClient.RequestStatu statu, String resons) {
        //
        //    }
        //});
    }

    private int second = Constants.SEND_VERIFY_CODE_DURATION_IN_SECOND;
    private Handler mhandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            second--;
            if (second == 0) {
                restoreWait();
            } else {
                startWait();
            }
        }
    };

    private void startWait() {
        mSendBtn.setClickable(false);
        mSendBtn.setText(Utils.getString(R.string.updatephone_verify_wait, second + ""));
        mhandler.postDelayed(mRunnable, 1000);
    }

    private void restoreWait() {
        second = Constants.SEND_VERIFY_CODE_DURATION_IN_SECOND;
        mSendBtn.setClickable(true);
        mhandler.removeCallbacks(mRunnable);
        mSendBtn.setText(R.string.updatephone_get_verifycode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRunnable != null && mhandler != null) {
            mhandler.removeCallbacks(mRunnable);
        }
        if (mRequest != null){
            mRequest.cancel();
        }
    }
}
