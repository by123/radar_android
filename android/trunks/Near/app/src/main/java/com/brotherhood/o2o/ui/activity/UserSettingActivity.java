package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.request.UpdateUserInfoRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.dialog.BottomChooseDialog;
import com.brotherhood.o2o.ui.widget.pickview.OptionsPickerView;
import com.brotherhood.o2o.ui.widget.pickview.TimePickerView;
import com.brotherhood.o2o.util.BitmapUtil;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.LocationPickerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserSettingActivity extends BaseActivity {

    @ViewInject(id = R.id.ivUserSetIcon)
    private ImageView mIvIcon;

    @ViewInject(id = R.id.tvUserSetName)
    private TextView mTvNickname;

    @ViewInject(id = R.id.tvUserSetSex)
    private TextView mTvSex;

    @ViewInject(id = R.id.tvUserSetDate)
    private TextView mTvDate;

    @ViewInject(id = R.id.tvUserSetLocation)
    private TextView mTvLocation;

    @ViewInject(id = R.id.tvUserSetSignature)
    private TextView mTvSignature;

    @ViewInject(id = R.id.tvUserSetLogout, clickMethod = "logout")
    private TextView mTvLogout;

    @ViewInject(id = R.id.rlUserSetPhoto, clickMethod = "setPhoto")
    private RelativeLayout mRlPhoto;

    @ViewInject(id = R.id.rlUserSetName, clickMethod = "setNickname")
    private RelativeLayout mRlName;

    @ViewInject(id = R.id.rlUserSetSex, clickMethod = "setSex")
    private RelativeLayout mRlSex;

    @ViewInject(id = R.id.rlUserSetBirthday, clickMethod = "setBirthday")
    private RelativeLayout mRlBirthday;

    @ViewInject(id = R.id.rlUserSetLocation, clickMethod = "setUserLocation")
    private RelativeLayout mRlLocation;

    @ViewInject(id = R.id.rlUserSetSignature, clickMethod = "setSignature")
    private RelativeLayout mRlSignature;

    private BottomChooseDialog mPhotoChooseDialog;
    private BottomChooseDialog mSexChooseDialog;
    private BottomChooseDialog mExitDialog;
    private static final int CAMERA_REQUEST_CODE = 1;//拍照请求码
    private static final int CROP_FROM_PHOTO_CODE = 2;//裁剪请求码
    private static final int ALBUM_REQUEST_CODE = 3;//系统相册请求码
    private static final int NICKNAME_REQUEST_CODE = 4;//昵称请求码
    private static final int SIGNATURE_REQUEST_CODE = 5;//个性签名请求码
    private String mImagePath;
    private Uri mImageUri;
    private TimePickerView mPickDateView;
    private OptionsPickerView mPvOptions;
    private List<String> mProvinceList = new ArrayList<>();
    private List<List<String>> mCityList = new ArrayList<>();
    private BottomChooseDialog mDialog;
    private boolean pickDateViewShow = false;


    public static void show(Context context) {
        Intent it = new Intent(context, UserSettingActivity.class);
        context.startActivity(it);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_setting_layout;
    }

    @Override
    protected boolean addActionBar() {
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
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.user_set_title, R.color.black);
        init();
        datePicker();
        locationPicker();
    }

    /**
     * 日期选择器
     */
    private void datePicker() {
        mPickDateView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        mPickDateView.setRange(calendar.get(Calendar.YEAR) - 100, calendar.get(Calendar.YEAR) + 100);
        mPickDateView.setTime(new Date());
        mPickDateView.setCyclic(false);
        mPickDateView.setCancelable(true);
        //时间选择后回调
        mPickDateView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mTvDate.setText(DateUtil.parseJavaTimeToString(date.getTime(), "yyyy/MM/dd"));
                modifyUserInfo(null, null, String.valueOf(date.getTime() / 1000), null, null, null);
            }
        });
    }

    /**
     * 地区选择器
     */
    private void locationPicker() {
       mPvOptions = new OptionsPickerView(this);
        String[] provinceArray = getResources().getStringArray(R.array.province);
        //第一级
        mProvinceList.addAll(LocationPickerUtil.getProvinces(this));
        //第二级
        mCityList.addAll(LocationPickerUtil.getCitys(this));
        for (int i = 0; i < mProvinceList.size() - 4; i++) {
            mCityList.add(new ArrayList<String>());
        }
        //二级联动效果
        mPvOptions.setPicker(mProvinceList, mCityList, true);
        //设置选择的三级单位
        //pwOptions.setLabels("省", "市", "区");
        mPvOptions.setCyclic(false, false, false);//设置循环滚动
        mPvOptions.setSelectOptions(0, 0);//设置默认选中的二级项目
        mPvOptions.setCancelable(true);
        //监听确定选择按钮
        mPvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String province = "";
                if (mProvinceList.size() > options1) {
                    province = mProvinceList.get(options1);
                }
                String city = "";
                if (mCityList.size() > options1 && mCityList.get(options1).size() > option2) {
                    city = mCityList.get(options1).get(option2);
                }
                String location = "";
                if (TextUtils.isEmpty(city)) {
                    location = province;
                } else {
                    location = province + "\t" + city;
                }
                mTvLocation.setText(location);
                modifyUserInfo(null, null, null, location, null, null);
            }
        });
    }

    private void init() {
        UserInfo userInfo = AccountManager.getInstance().getUser();
        if (userInfo == null) {
            LogManager.e("=========slide menu userinfo is null==========");
            return;
        }
        String headUrl = userInfo.mIcon;
        if (!TextUtils.isEmpty(headUrl)) {
            ImageLoaderManager.displayCircleImageByUrl(UserSettingActivity.this, mIvIcon, headUrl, R.mipmap.ic_msg_default);
        }
        if (!TextUtils.isEmpty(userInfo.mNickName)) {
            mTvNickname.setText(userInfo.mNickName);
        }
        if (userInfo.mGenger == 0) {
            mTvSex.setText(getString(R.string.sex_male));
        } else if (userInfo.mGenger == 1) {
            mTvSex.setText(getString(R.string.sex_female));
        }
        String birthday = userInfo.mBirthday;
        if (!TextUtils.isEmpty(birthday) && !birthday.equals("0")) {
            String dateStr = DateUtil.parseUnixTimeToString(Long.valueOf(birthday), "yyyy/MM/dd");
            mTvDate.setText(dateStr);
        }
        if (!TextUtils.isEmpty(userInfo.mResidence)) {
            mTvLocation.setText(userInfo.mResidence);
        }
        if (!TextUtils.isEmpty(userInfo.mSignature)) {
            mTvSignature.setText(userInfo.mSignature);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (pickDateViewShow) {
            mPickDateView.dismiss();
            pickDateViewShow = false;
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CAMERA_REQUEST_CODE://拍照
                //mImageUri = data.getData();
                if (mImageUri != null) {
                    clipPhoto();
                }
                break;
            case CROP_FROM_PHOTO_CODE://裁剪完成后回传头像路径
                mImagePath = BitmapUtil.getResizedBitmapFromFile(UserSettingActivity.this, mImagePath);
                ImageLoaderManager.displayCircleImageByFile(UserSettingActivity.this, mIvIcon, new File(mImagePath), R.mipmap.ic_msg_default);
                modifyUserInfo(null, null, null, null, null, mImagePath);//上传头像
                break;
            case ALBUM_REQUEST_CODE://系统相册
                if (data == null) {
                    break;
                }
                mImagePath = data.getStringExtra(BundleKey.CHOOSE_HEAD_PHOTO_KEY);
                if (TextUtils.isEmpty(mImagePath)) {
                    break;
                }
                mImagePath = BitmapUtil.getResizedBitmapFromFile(UserSettingActivity.this, mImagePath);
                ImageLoaderManager.displayCircleImageByFile(UserSettingActivity.this, mIvIcon, new File(mImagePath), R.mipmap.ic_msg_default);
                modifyUserInfo(null, null, null, null, null, mImagePath);//上传头像
                break;
            case SIGNATURE_REQUEST_CODE:
                if (data == null) {
                    break;
                }
                String signature = data.getStringExtra(BundleKey.MODIFY_RESULT_KEY);
                mTvSignature.setText(signature);
                break;
            case NICKNAME_REQUEST_CODE:
                if (data == null) {
                    break;
                }
                String nickname = data.getStringExtra(BundleKey.MODIFY_RESULT_KEY);
                mTvNickname.setText(nickname);
                break;
        }
    }

    /**
     * 图片裁剪
     */
    private void clipPhoto() {
        String path = mImageUri.getPath();
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            ColorfulToast.orange(UserSettingActivity.this, getString(R.string.choose_photo_notfound), Toast.LENGTH_SHORT);
            return;
        }
        mImagePath = DirManager.getExternalStroageDir(Constants.PHOTO_CROP_DIR) + "/" +System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
        Uri outputUri = Uri.fromFile(new File(mImagePath));
        DeviceUtil.startCropPhoto(UserSettingActivity.this, mImageUri, outputUri, CROP_FROM_PHOTO_CODE);
    }

    /**
     * 退出登录
     *
     * @param view
     */
    public void logout(View view) {
        mDialog = new BottomChooseDialog(UserSettingActivity.this, BottomChooseDialog.DialogType.LOGOUT);
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialogSecondLine:
                        //退出登陆
                        AccountManager.getInstance().logout();
                        DeviceUtil.rebootApp(UserSettingActivity.this);
                        finish();
                        break;
                    case R.id.dialogThirdLine://取消
                        mDialog.dismiss();
                        break;
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * 修改个性签名
     *
     * @param view
     */
    public void setSignature(View view) {
        ModifyTextActivity.show(UserSettingActivity.this, ModifyTextActivity.MODIFY_TYPE_SIGNATURE, getString(R.string.user_set_signature), mTvSignature
                .getText().toString(), SIGNATURE_REQUEST_CODE);
    }

    /**
     * 设置地区
     *
     * @param view
     */
    public void setUserLocation(View view) {
        mPvOptions.show();
    }

    /**
     * 生日
     *
     * @param view
     */
    public void setBirthday(View view) {
        mPickDateView.show();
        pickDateViewShow = true;
    }

    /**
     * 昵称
     *
     * @param view
     */
    public void setNickname(View view) {
        ModifyTextActivity.show(UserSettingActivity.this, ModifyTextActivity.MODIFY_TYPE_NICKNAME, getString(R.string.userinfofragment_nickname), mTvNickname
                .getText().toString(), NICKNAME_REQUEST_CODE);
    }

    /**
     * 头像
     *
     * @param view
     */
    public void setPhoto(View view) {
        if (mPhotoChooseDialog == null) {
            mPhotoChooseDialog = new BottomChooseDialog(UserSettingActivity.this, BottomChooseDialog.DialogType.PHOTO_CHOOSE);
            mPhotoChooseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dialogFirstLine://拍照
                            mImagePath = System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
                            mImageUri = Uri.fromFile(new File(DirManager.getExternalStroageDir(Constants.PHOTO_OUT_DIR), mImagePath));
                            DeviceUtil.startCamera(UserSettingActivity.this, mImageUri, CAMERA_REQUEST_CODE);
                            break;
                        case R.id.dialogSecondLine:
                            ChoosePhotoActivity.show(UserSettingActivity.this, ALBUM_REQUEST_CODE, true, -1);
                            break;
                        case R.id.dialogThirdLine:
                            break;
                    }
                    mPhotoChooseDialog.dismiss();
                }
            });
        }
        mPhotoChooseDialog.show();
    }

    /**
     * 性别
     *
     * @param view
     */
    public void setSex(View view) {
        if (mSexChooseDialog == null) {
            mSexChooseDialog = new BottomChooseDialog(UserSettingActivity.this, BottomChooseDialog.DialogType.SEX_CHOOSE);
            mSexChooseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dialogSecondLine://男
                            mTvSex.setText(getString(R.string.sex_male));
                            modifyUserInfo(null, String.valueOf(0), null, null, null, null);
                            break;
                        case R.id.dialogThirdLine://女
                            mTvSex.setText(getString(R.string.sex_female));
                            modifyUserInfo(null, String.valueOf(1), null, null, null, null);
                            break;
                    }
                    mSexChooseDialog.dismiss();
                }
            });
        }
        mSexChooseDialog.show();
    }

    private void modifyUserInfo(String name, String gender, String birthday, String location, String signature, String iconPath) {

        UpdateUserInfoRequest request = UpdateUserInfoRequest.createUpdateUserInfoRequest(name, gender, birthday, location, signature, iconPath, new
                OnOKHttpResponseListener<UserInfo>() {
                    @Override
                    public void onSuccess(int code, String msg, UserInfo userInfo, boolean cache) {
                        if (userInfo != null) {
                            LogManager.i("set_birth_success", code + "::::::::::::::::::::::::::::::::::::::::::::::" + msg);
                            AccountManager.getInstance().updateUser(userInfo, true);

                            ColorfulToast.green(UserSettingActivity.this, getString(R.string.put_group_name_suc), 0);
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogManager.i("set_birth_fail", code + ":::::::::::::::::::::::::::::::::::::::::::::::::::::::" + msg);
                        ColorfulToast.orange(UserSettingActivity.this, msg, Toast.LENGTH_SHORT);
                    }
                });
        request.postAsyn(true);
    }
}
