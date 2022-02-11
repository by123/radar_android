package com.brotherhood.o2o.surprise;

import android.content.Context;
import android.content.Intent;

import com.brotherhood.o2o.surprise.controller.MySurpriseActivity;

/**
 * Created by ZhengYi on 15/6/30.
 */
public class SurpriseComponent {
    private static SurpriseComponent sInstance;

    private SurpriseComponent() {
    }

    public static SurpriseComponent shareComponent() {
        if (sInstance == null)
            sInstance = new SurpriseComponent();
        return sInstance;
    }

    public void showMySurprisePage(Context context) {
        Intent intent = new Intent(context, MySurpriseActivity.class);
        context.startActivity(intent);
    }
}
