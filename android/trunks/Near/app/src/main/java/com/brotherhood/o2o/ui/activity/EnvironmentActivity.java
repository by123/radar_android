package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

/**
 * Created by billy.shi on 2016/1/18.
 */
public class EnvironmentActivity extends BaseActivity {

    @ViewInject(id = R.id.btChangeEnv)
    private Button mBtChangeEnv;

    @ViewInject(id = R.id.btShowUid)
    private Button mBtShowUid;

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
        return R.layout.activity_environment_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.tan)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.change_environment, R.color.black);

        mBtChangeEnv.setOnClickListener(this);
        mBtShowUid.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
      switch (v.getId()) {
          case R.id.abBack:
              finish();
              break;

          case R.id.btChangeEnv:
              Intent intent = new Intent(this, EnvChangeActivity.class);
              startActivity(intent);
              break;

          case R.id.btShowUid:
              mBtShowUid.setText(AccountManager.getInstance().getUser().mUid);
              break;
      }
    }


}
