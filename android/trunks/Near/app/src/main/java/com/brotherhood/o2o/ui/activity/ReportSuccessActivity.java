package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

/**
 * Created by billy.shi on 2016/1/14.
 */
public class ReportSuccessActivity extends BaseActivity {


    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_success_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.report_title, R.color.black);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;

        }
    }


    public static void show(Context context) {
        Intent intent = new Intent(context, ReportSuccessActivity.class);
        context.startActivity(intent);
    }
}
