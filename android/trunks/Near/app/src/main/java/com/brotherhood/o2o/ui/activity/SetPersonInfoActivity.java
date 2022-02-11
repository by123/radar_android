package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.UpdateUserInfoRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.account.WaveView;
import com.brotherhood.o2o.util.BitmapUtil;
import com.brotherhood.o2o.util.DisplayUtil;

import java.io.File;

/**
 * 完善资料
 */
public class SetPersonInfoActivity extends BaseActivity {

    @ViewInject(id = R.id.ivPersonHeadPhoto, clickMethod = "setHeadPhoto")
    private ImageView mIvHeadPhoto;

    @ViewInject(id = R.id.rbPersonMale)
    private RadioButton mRbMale;

    @ViewInject(id = R.id.rbPersonFemale)
    private RadioButton mRbFemale;

    @ViewInject(id = R.id.tvPersonFinish, clickMethod = "submit")
    private TextView mTvFinish;

    @ViewInject(id = R.id.viPersonWave)
    private WaveView mWaveView;

    @ViewInject(id = R.id.etPersonNickname)
    private EditText mEditText;

    @ViewInject(id = R.id.rgPersonRadioGroup)
    private RadioGroup mRadioGroup;

    private static final int HEAD_PHOTO_REQUEST_CODE = 1;
    private String mHeadPhotoPath;

    private UpdateUserInfoRequest mUpdateUserInfoRequest;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_person_info_layout;
    }

    public static void show(Context context) {
        Intent it = new Intent(context, SetPersonInfoActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWaveView.startRippleAnimation();
        ColorfulToast.orange(this, getString(R.string.person_upload_real_photo), Toast.LENGTH_SHORT);
        init();
        initEvent();
    }

    /**
     * 初始化已存在的用户信息
     */
    private void init() {
        UserInfo user = AccountManager.getInstance().getUser();
        if (user != null){
            if (user.mIcon != null){
                ImageLoaderManager.displayCircleImageByUrl(SetPersonInfoActivity.this, mIvHeadPhoto, user.mIcon, R.mipmap.head_photo_default);
            }
            if (user.mNickName != null){
                mEditText.setText(user.mNickName);
            }
            if (user.mGenger == 0){//男
                mRbFemale.setChecked(false);
                mRbMale.setChecked(true);
            }else if (user.mGenger == 1){//女
                mRbFemale.setChecked(true);
                mRbMale.setChecked(false);
            }
        }
    }

    private void initEvent() {
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mEditText.getText().toString().trim();
                    mEditText.setSelection(key.length());
                    return true;
                }
                return false;
            }
        });
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
                mHeadPhotoPath = BitmapUtil.getResizedBitmapFromFile(SetPersonInfoActivity.this, mHeadPhotoPath);
                ImageLoaderManager.displayRoundImageByFile(SetPersonInfoActivity.this, mIvHeadPhoto, new File(path), DisplayUtil.dp2px(50), R.mipmap.head_photo_default);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveView.stopRippleAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWaveView != null){
            mWaveView.startRippleAnimation();
        }
    }

    /**
     * 设置头像
     *
     * @param view
     */
    public void setHeadPhoto(View view) {
        Intent chooseIntent = new Intent(SetPersonInfoActivity.this, ChoosePhotoActivity.class);
        startActivityForResult(chooseIntent, HEAD_PHOTO_REQUEST_CODE);
    }

    /**
     * 完成
     *
     * @param view
     */
    public void submit(View view) {
        if (TextUtils.isEmpty(mHeadPhotoPath)) {
            ColorfulToast.orange(SetPersonInfoActivity.this, getString(R.string.person_upload_real_headphoto), Toast.LENGTH_SHORT);
            return;
        }
        final String nickName = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nickName)) {
            ColorfulToast.orange(SetPersonInfoActivity.this, getString(R.string.person_nickname_isempty), Toast.LENGTH_SHORT);
            return;
        }

        //登陆模式为phone. 昵称长度不超过10
        if (nickName.length() > 10 && Constants.LOGIN_TYPE.equals(Constants.LOGIN_TYPE_M)) {
            ColorfulToast.orange(SetPersonInfoActivity.this, getString(R.string.person_nickname_formaterror), Toast.LENGTH_SHORT);
            return;
        }
        //登陆模式为email. 昵称长度小于20
        if(nickName.length() < 20 && Constants.LOGIN_TYPE.equals(Constants.LOGIN_TYPE_EMAIL)) {
            ColorfulToast.orange(SetPersonInfoActivity.this, getString(R.string.person_nickname_formaterror), Toast.LENGTH_SHORT);
            return;
        }
        int gender = -1;//0男   1女
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.rbPersonMale:
                gender = 0;
                break;
            case R.id.rbPersonFemale:
                gender = 1;
                break;
        }
        if (gender != 0 && gender != 1) {
            ColorfulToast.orange(SetPersonInfoActivity.this, getString(R.string.person_sex_isempty), Toast.LENGTH_SHORT);
            return;
        }
        final int realGender = gender;
        requestUpdate(nickName, realGender, mHeadPhotoPath);
    }

    /**
     * 完善资料
     * @param nickName
     * @param gender
     * @param iconPath
     */
    private void requestUpdate(final String nickName, int gender, String iconPath){
        if (mUpdateUserInfoRequest == null) {
            mUpdateUserInfoRequest = UpdateUserInfoRequest.createUpdateUserInfoRequest(nickName, String.valueOf(gender), null, null, null, iconPath, new
                            OnOKHttpResponseListener<UserInfo>() {
                                @Override
                                public void onSuccess(int code, String msg, UserInfo userInfo, boolean cache) {
                                    if (userInfo == null) {//完善资料未获取正确数据
                                        ColorfulToast.orange(SetPersonInfoActivity.this, msg, Toast.LENGTH_SHORT);
                                        AccountManager.getInstance().removeUser();
                                        return;
                                    }
                                    AccountManager.getInstance().setUser(userInfo, true);
                                    MainActivity.show(SetPersonInfoActivity.this);
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    LogManager.w("phone_login", code + ":: " +msg);
                                    ColorfulToast.orange(SetPersonInfoActivity.this, msg, Toast.LENGTH_SHORT);
                                }
                            }
            );
        }
        mUpdateUserInfoRequest.postAsyn(true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//完善资料页屏蔽返回键，未完善资料不能回到雷达页
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateUserInfoRequest != null){
            mUpdateUserInfoRequest.cancel();
        }
    }
}
