package com.brotherhood.o2o.personal;

import android.content.Context;
import android.content.Intent;

import com.brotherhood.o2o.personal.controller.AboutActivity;
import com.brotherhood.o2o.personal.controller.FeedbackActivity;
import com.brotherhood.o2o.personal.controller.SystemMsgActivity;
import com.brotherhood.o2o.personal.controller.UserInfoActivity;

/**
 * Created by by.huang on 2015/6/3.
 */
public class PersonalComponent {
    private static PersonalComponent sInstance;

    private PersonalComponent() {
    }

    public static PersonalComponent shareComponent() {
        if (sInstance == null)
            sInstance = new PersonalComponent();
        return sInstance;
    }

    public void showUserInfoPage(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    public void showAboutPage(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public void showFeedbackPage(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    public void showMessagePage(Context context) {
        Intent intent = new Intent(context, SystemMsgActivity.class);
        context.startActivity(intent);
    }

}
