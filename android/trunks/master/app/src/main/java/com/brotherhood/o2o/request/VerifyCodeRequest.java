package com.brotherhood.o2o.request;

import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.model.ResponseResult;
import com.brotherhood.o2o.request.base.BaseAppRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改电话验证码
 * Created by jl.zhang on 2015/12/3.
 */
public class VerifyCodeRequest extends BaseAppRequest<ResponseResult<String>> {

    public VerifyCodeRequest(Map<String, String> bodyMap, OnBaseResponseListener<ResponseResult<String>> baseResponseListener) {
        super(Constants.URL_POST_VERIFY_CODE, Method.POST, true, bodyMap, baseResponseListener);
    }

    public static VerifyCodeRequest createVerifyCodeRequest(String phoneNo, OnResponseListener<String> listener){
        Map<String,String> params = new HashMap();
        params.put("cell_phone", phoneNo);
        VerifyCodeRequest request = new VerifyCodeRequest(params, listener);
        return request;
    }
}
