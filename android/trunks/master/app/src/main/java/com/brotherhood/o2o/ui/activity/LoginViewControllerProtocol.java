package com.brotherhood.o2o.ui.activity;

/**
 * Created by ZhengYi on 15/7/1.
 */
public interface LoginViewControllerProtocol {
    void showQQLoginWebView(String requestURL, Callback callback);

    void hideQQLoginWebView();

    interface Callback {
        void onFinishLogin(String callbackURL);
    }
}
