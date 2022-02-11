package com.brotherhood.o2o.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;

/**
 * Created by billy.shi on 2016/1/4.
 */
public class SendEmailActivity extends BaseActivity {

    @ViewInject(id = R.id.etSendEmail)
    private EditText mEtEmail;

    @ViewInject(id = R.id.llSendEmail, clickMethod = "sendEmail")
    private LinearLayout mSendEmail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected boolean addOverlayActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_white)
                .setDivideColor(R.color.tan)
                .setHeadBackgroundColor(R.color.transparent)
                .setBaseTitle(R.string.forgot_password, R.color.white)
                .hideHorizontalDivide();
    }

    /**
     * 发送邮件
     * @param view
     */
    public void sendEmail(View view) {

        String email = mEtEmail.getText().toString().trim();

        String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if(!email.matches(emailRegex)) {
            ColorfulToast.redNoIcon(this, getString(R.string.email_error), Toast.LENGTH_SHORT);
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
            break;
        }
    }
}
