package com.brotherhood.o2o.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.brotherhood.o2o.listener.OnCommonResponseListener;
import com.brotherhood.o2o.request.wrapper.AccountWrapperRequest;
import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.wrapper.DGCPassWrapper;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by ZhengYi on 15/7/1.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    IWXAPI mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = DGCPassWrapper.createWXAPI(this);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("AAA", "onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("AAA", "onResponse");
        if (baseResp instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            if (resp.errCode == 0) {//用户同意授权
                Log.e("AAA", "userAgree");
                String code = resp.code;
                AccountWrapperRequest.loginWithWechatCallback(this, code, new OnCommonResponseListener<String>() {
                    @Override
                    public void onSuccess(String data) {
//                        MainActivity.show(WXEntryActivity.this);
//                        finish();
                    }

                    @Override
                    public void onFailed(String errorMsg) {

                    }
                });
            } else if (resp.errCode == -4) {//用户拒绝授权
                // user refuse to auth
                Log.e("AAA", "userRefuse");
            } else if (resp.errCode == -2) {//用户取消授权
                // user cancel
                Log.e("AAA", "userCancel");
            }
            finish();
        }
    }
}
