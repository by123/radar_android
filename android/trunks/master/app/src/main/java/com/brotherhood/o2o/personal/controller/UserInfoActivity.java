package com.brotherhood.o2o.personal.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.ui.activity.UpdatePhoneActivity;
import com.brotherhood.o2o.model.account.UserInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.UriUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;
import com.brotherhood.o2o.ui.widget.dialog.CaptureDialog;
import com.brotherhood.o2o.ui.widget.dialog.GenderDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/6/9.
 */
public class UserInfoActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.txt_phone)
    TextView mPhoneTxt;

    @InjectView(R.id.img_avatar)
    SimpleDraweeView mAvatarImg;

    @InjectView(R.id.txt_gender)
    TextView mGenderTxt;

    @InjectView(R.id.txt_nickname)
    TextView mNickNameTxt;

    @InjectView(R.id.layout_input_nickname)
    View mInputNickNameLayout;


    @OnClick(R.id.btn_logout)
    void OnLogoutClick() {
        new BasicDialog(this, new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                dialog.dismiss();
                AccountComponent.shareComponent().logout();
                finish();
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
                dialog.dismiss();
            }
        }).setMainTxt(getString(R.string.userinfofragment_logout_tips)).hideMinorTxt().show();
    }

    @OnClick(R.id.layout_avatar)
    void OnAvatarLayoutClick() {
        new CaptureDialog(this).show();
    }

    @OnClick(R.id.layout_gender)
    void OnGenderLayoutClick() {
        new GenderDialog(this, new GenderDialog.OnGenderDialogListener() {
            @Override
            public void OnMaleClick() {
                mGender = 0;
                mGenderTxt.setText(Utils.getString(R.string.sex_male));
                if (mGender == mUserInfo.mGenger) {
                    return;
                }
                mType = Constants.UPLOAD_GENDER;
                updateUserInfo();
            }

            @Override
            public void OnFemaleClick() {
                mGender = 1;
                mGenderTxt.setText(Utils.getString(R.string.sex_female));
                if (mGender == mUserInfo.mGenger) {
                    return;
                }
                mType = Constants.UPLOAD_GENDER;
                updateUserInfo();
            }
        }).show();
    }

    @OnClick(R.id.layout_nickname)
    void OnNicknameClick() {
        mInputNickNameLayout.setVisibility(View.VISIBLE);
        TextView mTitleTxt = (TextView) mInputNickNameLayout.findViewById(R.id.txt_title);
        final EditText mEditTxt = (EditText) mInputNickNameLayout.findViewById(R.id.edittext);
        View mConfirmBtn = mInputNickNameLayout.findViewById(R.id.btn_confirm);
        mTitleTxt.setText("昵称");
        mEditTxt.setHint("请输入昵称");
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = mEditTxt.getText().toString();
                updateNickName(nickname);
            }
        });
    }

    @OnClick(R.id.layout_phone)
    void OnPhoneLayoutClick() {
        UpdatePhoneActivity.show(this);
    }

    private UserInfo mUserInfo;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_userinfo);
        ButterKnife.inject(this);
        initData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AccountComponent.ACTION_USERINFO_UPDATE);
        registerReceiver(mReceiver, filter);
    }


    private void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUserInfo = AccountComponent.shareComponent().getmUserInfo();
        if (mUserInfo != null) {
            mNickName = mUserInfo.mNickName;
            mGender = mUserInfo.mGenger;
            mPhone = mUserInfo.mPhone;
            if (mUserInfo.mAvatarPath != null) {
                ImageLoader.getInstance().setImageLocal(mAvatarImg, mUserInfo.mAvatarPath);
            } else {
                ImageLoader.getInstance().setImageUrl(mAvatarImg, mUserInfo.mAvatarURL, 1, null, Utils.dip2px(72), Utils.dip2px(72));
            }
            mNickNameTxt.setText(mUserInfo.mNickName);
            mGenderTxt.setText(mUserInfo.mGenderTxt);
            mPhoneTxt.setText(mUserInfo.mPhone);

        }
    }


    private int mGender = 0;
    private String mNickName;
    private String mAvatarPath;
    private String mPhone;

    private void updateUserInfo() {
        Constants.IS_UPDATED = true;
        UserInfo userinfo = AccountComponent.shareComponent().getmUserInfo();
        if (userinfo != null) {
            userinfo.mGenger = mGender;
            userinfo.mNickName = mNickName;
            userinfo.mPhone = mPhone;
            userinfo.mAvatarPath = mAvatarPath;
            AccountComponent.shareComponent().UpLoadUserInfo(UserInfoActivity.this, userinfo, mType);
        }
    }

    private void updateNickName(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            Utils.showShortToast("昵称不能为空哦");
            return;
        }
        if (mUserInfo != null && !nickname.equalsIgnoreCase(mUserInfo.mNickName)) {
            mNickName = nickname;
            mType = Constants.UPLOAD_NICKNAME;
            updateUserInfo();
            mInputNickNameLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mInputNickNameLayout.getVisibility() == View.VISIBLE) {
            mInputNickNameLayout.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = AccountComponent.shareComponent().getHeadUri();
            if (requestCode == Constants.REQUEST_CODE_CAPTURE_CAMEIA) {
                if (fileUri != null) {
                    String imgpath = fileUri.getPath();
                    ByLogout.out("拍照返回->" + imgpath);
                    ImageLoader.getInstance().setImageLocal(mAvatarImg, imgpath);
                    mAvatarPath = imgpath;
                    mType = Constants.UPLOAD_AVATAR;
                    updateUserInfo();
                }
            } else if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
                if (data == null)
                    return;
                Uri originalUri = data.getData();
                if (originalUri != null) {
                    String uploadPhotPath = UriUtils.getPath(this, originalUri);
                    final String uploadPhotPathExt = Utils
                            .RoateImage(uploadPhotPath);
                    if (uploadPhotPathExt == null
                            || uploadPhotPathExt.length() == 0)
                        return;
                    ByLogout.out("相册返回->" + uploadPhotPathExt);
                    ImageLoader.getInstance().setImageLocal(mAvatarImg, uploadPhotPathExt);
                    mAvatarPath = uploadPhotPathExt;
                    mType = Constants.UPLOAD_AVATAR;
                    updateUserInfo();
                }
            }
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(AccountComponent.ACTION_USERINFO_UPDATE)) {
                if(mType == Constants.UPLOAD_PHONE)
                {
                    String phone = intent.getStringExtra("phone");
                    mPhoneTxt.setText(phone);
                }
                else if(mType == Constants.UPLOAD_NICKNAME)
                {
                    String niakname = intent.getStringExtra("nickname");
                    mNickNameTxt.setText(niakname);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
