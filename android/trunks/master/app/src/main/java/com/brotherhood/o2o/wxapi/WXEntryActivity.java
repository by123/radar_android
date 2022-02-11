package com.brotherhood.o2o.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.brotherhood.o2o.account.helper.AccountURLFetcher;
import com.brotherhood.o2o.model.account.LoginUserInfo;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.extensions.DGCPassWrapper;
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

    private static class WXLoginEventHandler implements BaseURLFetcher.Callback<LoginUserInfo> {
        @Override
        public void onCallback(LoginUserInfo dataOrNil, String errorOrNil) {
            if (dataOrNil == null) {
                Log.e("AAA", "登录失败");
            } else {
                Log.e("AAA", "登录成功");
            }
        }
    }

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
            // handle login callback
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            if (resp.errCode == 0) {
                // user agree
                Log.e("AAA", "userAgree");
                String code = resp.code;
                AccountURLFetcher.loginWithWechatCallback(code, new WXLoginEventHandler());
            } else if (resp.errCode == -4) {
                // user refuse to auth
                Log.e("AAA", "userRefuse");
            } else if (resp.errCode == -2) {
                // user cancel
                Log.e("AAA", "userCancel");
            }

            finish();
        }
    }
}
