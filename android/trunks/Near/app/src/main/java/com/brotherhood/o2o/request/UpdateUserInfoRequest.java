package com.brotherhood.o2o.request;

import android.text.TextUtils;

import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.request.base.BaseUploadRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jl.zhang on 2016/1/7.
 */
public class UpdateUserInfoRequest extends BaseUploadRequest<ResponseResult<UserInfo>> {

    public UpdateUserInfoRequest(String filePath, Map<String, String> params, OnBaseResponseListener<ResponseResult<UserInfo>> listener) {
        super(Constants.URL_UPDATE_USERINFO, "avatar", filePath, params, "avatar", listener);
    }


    /**
     * @param nickname 昵称
     * @param gender 性别 0（男）| 1（女）
     * @param phoneNum 手机号码; 格式:1[3578]\d{9},格式不合法会直接提示手机号码不对
     * @param verifyCode 手机校验码（绑定手机号码时，必填）
     * @param birthday 生日
     * @param signature 个性签名
     * @param residence 常住地
     * @param listener
     * @return
     */
    public static UpdateUserInfoRequest createUpdateUserInfoRequest(String nickname, String gender, String birthday, String residence, String signature,
            String headFilePath, OnOKHttpResponseListener<UserInfo> listener){
        Map<String,String> params = new HashMap<>();
        if (!TextUtils.isEmpty(nickname)){
            params.put("nickname", nickname);
        }
        if (!TextUtils.isEmpty(gender)){
            params.put("gender", gender);
        }
        if (!TextUtils.isEmpty(birthday)) {
            params.put("birthday", birthday);
        }
        if (!TextUtils.isEmpty(residence)){
            params.put("residence", residence);
        }
        if (!TextUtils.isEmpty(signature)){
            params.put("signature", signature);
        }

        return new UpdateUserInfoRequest(headFilePath, params, listener);
    }
}
