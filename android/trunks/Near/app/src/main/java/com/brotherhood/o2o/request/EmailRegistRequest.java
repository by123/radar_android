package com.brotherhood.o2o.request;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.brotherhood.o2o.bean.ResponseResult;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.request.base.BaseUploadRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by billy.shi on 2016/1/5.
 */
public class EmailRegistRequest extends BaseUploadRequest<ResponseResult<String>> {


    public EmailRegistRequest(String filePath, Map<String, String> params, OnBaseResponseListener<ResponseResult<String>> listener) {
        super(Constants.URL_POST_EMAIL_REGIST, "avatar", filePath, params, "email", listener);
    }

    public static EmailRegistRequest createEmailRegistRequest(String email, String userName, String password, String headPhotoPath, String gender, OnOKHttpResponseListener listener) {

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("nickname", userName);

        params.put("type", Constants.LOGIN_TYPE_EMAIL); // 登陆类型email
        params.put("gender", gender);

        return new EmailRegistRequest(headPhotoPath, params, listener);
    }

}






   /* public EmailRegistRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_EMAIL_REGIST, Method.POST, true, bodyMap, baseResponseListener);
        Log.i("", "-------Constants.URL_POST_EMAIL_REGIST---------" + Constants.URL_POST_EMAIL_REGIST);
    }


    public static EmailRegistRequest createEmailRegistRequest(String email, String userName, String password, String headPhotoPath, String gender,OnBaseResponseListener listener) {

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("nickname", userName);


        params.put("avatar", headPhotoPath);
        params.put("type", Constants.LOGIN_TYPE_EMAIL); // 登陆类型email
        params.put("gender", gender);

        return new EmailRegistRequest(params, listener);
    }*/



