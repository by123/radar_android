package com.brotherhood.o2o.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnCommonResponseListener;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.EmailRegistRequest;
import com.brotherhood.o2o.request.wrapper.AccountWrapperRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.BitmapUtil;
import com.brotherhood.o2o.util.DialogUtil;

import java.io.File;

/**
 * Created by billy.shi on 2015/12/28.
 */
public class EmailRegistActivity extends BaseActivity {

    @ViewInject(id = R.id.llCreateCompleted, clickMethod = "emailComplete")
    private LinearLayout mEmailLogin;

    @ViewInject(id = R.id.ivAddPhoto, clickMethod = "addPhoto")
    private ImageView mAddPhoto;

    /*@ViewInject(id = R.id.loginWithFacebook, clickMethod = "emailToFacebook")
    private LinearLayout mLoginWithFacebook;*/

    @ViewInject(id = R.id.etRegistEmail)
    private EditText mRegistEmail;

    @ViewInject(id = R.id.etRegistPassword)
    private EditText mRegistPassword;

    @ViewInject(id = R.id.etRegistUsername)
    private EditText mRegistUsername;

    @ViewInject(id = R.id.rbMale)
    private RadioButton mRbMale;

    @ViewInject(id = R.id.rbFemale)
    private RadioButton mRbFemale;

    private static final int HEAD_PHOTO_REQUEST_CODE = 1;
    private String mHeadPhotoPath;
    private EmailRegistRequest mEmailRegistRequest;
    private  boolean canClick = true;
    private String TAG = "EmailRegistActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_regist_layout;
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
                .setBackImage(R.mipmap.login_phone_close)
                .setDivideColor(R.color.tan)
                .setHeadBackgroundColor(R.color.transparent)
                .setBaseTitle(R.string.create_account, R.color.white)
                .hideHorizontalDivide();

        //设置监听
        mRbMale.setOnClickListener(this);
        mRbFemale.setOnClickListener(this);

    }

    /**
     * 添加头像
     * @param view
     */
    public void addPhoto(View view) {
        Intent chooseIntent = new Intent(this, OverseaChoosePhotoActivity.class);
        startActivityForResult(chooseIntent, HEAD_PHOTO_REQUEST_CODE);
    }

    /**
     * 点击注册完成
     * @param view
     */
    public void emailComplete(View view) {
        if(canClick) { //注册只能点击一次
            canClick = false;
        }

        LogManager.v(TAG, "commit email regist");
        final String email =  mRegistEmail.getText().toString().replace(" ", "");
       final  String password = mRegistPassword.getText().toString();
        String userName = mRegistUsername.getText().toString().trim();
        String gender = ""; //"0"男   "1"女

        if(mRbMale.isChecked()) {
            gender = "0";
        } else if(mRbFemale.isChecked()) {
            gender = "1";
        }

       // String emailRegex = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
       // String emailRegex = "/^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$/i";
        String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if(!email.matches(emailRegex)) {
            ColorfulToast.redNoIcon(this, getString(R.string.email_error), Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(password) || password.length() < 6) {
            ColorfulToast.redNoIcon(this, getString(R.string.password_toast), Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(userName) || userName.getBytes().length > 20) {
            ColorfulToast.redNoIcon(this, getString(R.string.username_toast), Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(gender)) {
            ColorfulToast.redNoIcon(this, getString(R.string.choose_gender), Toast.LENGTH_LONG);
            return;
        }


        if(TextUtils.isEmpty(mHeadPhotoPath)) {
            ColorfulToast.redNoIcon(this, getString(R.string.add_photo), Toast.LENGTH_LONG);
            return;
        }

        //都不为空，提交数据

        final Dialog dialog_regist = DialogUtil.createLoadingDialog(this, getString(R.string.creating_account));
        final Dialog dialog_login = DialogUtil.createLoadingDialog(this, getString(R.string.create_success_login));
        mEmailRegistRequest = EmailRegistRequest.createEmailRegistRequest(email, userName, password, mHeadPhotoPath, gender, new OnOKHttpResponseListener() {
           @Override
           public void onSuccess(int code, String msg, Object o, boolean cache) {
               //注册成功， 登陆到雷达界
               //调用登陆方法--登陆到雷达
               dialog_regist.dismiss();
               dialog_login.show();
               DefaultSharePrefManager.putString(SharePrefConstant.LAST_LOGIN_EMAIL, email);
               Constants.LOGIN_TYPE = Constants.LOGIN_TYPE_EMAIL;
               DefaultSharePrefManager.putString(SharePrefConstant.PREFER_LOGIN_TYPE, Constants.LOGIN_TYPE);
               AccountWrapperRequest.loginWithEmailCallback(EmailRegistActivity.this, email, password, new OnCommonResponseListener<String>() {
                   @Override
                   public void onSuccess(String data) {
                       //跳转到雷达界面
//                       MainActivity.show(EmailRegistActivity.this);
                   }

                   @Override
                   public void onFailed(String errorMsg) {
                       dialog_login.dismiss();
                       EmailLoginActivity.show(EmailRegistActivity.this);
                       finish();
                       //提示登录失败
                       //ColorfulToast.orangeNoIcon(EmailRegistActivity.this, errorMsg, Toast.LENGTH_LONG);
                   }
               });
           }

           @Override
           public void onFailure(int code, String msg) {
               dialog_regist.dismiss();
               ColorfulToast.orange(EmailRegistActivity.this, msg, Toast.LENGTH_LONG);
             /* switch (code){

                   case 3001: //Email was wrong format
                       ColorfulToast.orange(EmailRegistActivity.this, msg, Toast.LENGTH_LONG);

                       break;
                   case 3116://邮箱已注册

                      ColorfulToast.orange(EmailRegistActivity.this, msg, Toast.LENGTH_LONG);
                       break;

                   case 3002://注册失败
                       ColorfulToast.orange(EmailRegistActivity.this, "Regist failure", Toast.LENGTH_LONG);
                       break;

                    default://连网失败 code = -1
                        ColorfulToast.orange(EmailRegistActivity.this, msg, Toast.LENGTH_LONG);
                        break;
               }*/
           }

       });
        mEmailRegistRequest.postAsyn(true);
        dialog_regist.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case HEAD_PHOTO_REQUEST_CODE:
                String path = data.getStringExtra(BundleKey.CHOOSE_HEAD_PHOTO_KEY);
                if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                    return;
                }
                mHeadPhotoPath = path;
               // mHeadPhotoPath = BitmapUtil.getResizedBitmapFromFile(EmailRegistActivity.this, mHeadPhotoPath);
                mHeadPhotoPath = BitmapUtil.getResizedBitmapFromFile(EmailRegistActivity.this, mHeadPhotoPath);
                ImageLoaderManager.displayCircleImageByFile(EmailRegistActivity.this, mAddPhoto, new File(mHeadPhotoPath), R.mipmap.head_photo_default);

                break;
        }
    }

    /**
     * 在Email注册界面转向Facebook登陆
     * @param view
     */
    public void loginWithFacebook(View view) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }
}
