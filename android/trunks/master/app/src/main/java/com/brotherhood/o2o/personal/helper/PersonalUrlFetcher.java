package com.brotherhood.o2o.personal.helper;

import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.utils.BaseRequestParams;
import com.brotherhood.o2o.config.Constants;

/**
 * Created by by.huang on 2015/7/24.
 */
public class PersonalUrlFetcher {

    private static PersonalUrlFetcher mHelper;
    private static byte[] sync = new byte[0];

    public static PersonalUrlFetcher getInstance() {
        if (mHelper == null) {
            synchronized (sync) {
                if (mHelper == null) {
                    mHelper = new PersonalUrlFetcher();
                }
            }
        }
        return mHelper;
    }

    /**
     * 提交反馈
     * @param content
     * @param listener
     */
    public void submitFeedback(String content, HttpClient.OnHttpListener listener) {
        BaseRequestParams params = new BaseRequestParams();
        params.put("content", content);
        HttpClient.getInstance().post_v2(Constants.URL_POST_FEEDBACK, params, listener);
    }
}
