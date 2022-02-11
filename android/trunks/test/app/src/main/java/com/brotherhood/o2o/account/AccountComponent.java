package com.brotherhood.o2o.account;

import android.content.Context;

import com.brotherhood.o2o.account.controller.LoginActivity;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class AccountComponent {
    private static AccountComponent sInstance;

    private AccountComponent() {
    }

    public static AccountComponent shareComponent() {
        if (sInstance == null)
            sInstance = new AccountComponent();
        return sInstance;
    }

    /**
     * 显示登录页面
     *
     * @param context 上下文
     */
    public void showLoginPage(Context context) {
        LoginActivity.show(context);
    }
}
