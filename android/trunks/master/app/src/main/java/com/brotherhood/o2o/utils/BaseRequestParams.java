package com.brotherhood.o2o.utils;

import com.brotherhood.o2o.config.Constants;
import com.loopj.android.http.RequestParams;

/**
 * Created by by.huang on 2015/7/21.
 */
public class BaseRequestParams extends RequestParams {
    public BaseRequestParams() {
        put("pf", "near");
        put("from_pf", Constants.LOGIN_TYOE);
    }

}
