package com.brotherhood.o2o.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.model.account.UserInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.UriUtils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.AnimCircleView;
import com.brotherhood.o2o.ui.widget.dialog.CaptureDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/20.
 */
public class PerfectUserInfoActivity extends BaseActivity {


    @InjectView(R.id.img_left)
    ImageView mLeftImg;

    @InjectView(R.id.img_right)
    ImageView mRightImg;

    @InjectView(R.id.txt_title)
    TextView mTitleTxt;

    @InjectView(R.id.img_title)
    ImageView mTitleImg;

    @InjectView(R.id.edit_nickname)
    EditText mNicknameEdit;

    @InjectView(R.id.img_takephoto)
    SimpleDraweeView mTakePhoto;

    @InjectView(R.id.img_male)
    ImageView mMaleImg;

    @InjectView(R.id.img_female)
    ImageView mFemaleImg;

    @InjectView(R.id.circleview1)
    AnimCircleView mAnimCircleView1;

    @InjectView(R.id.circleview2)
    AnimCircleView mAnimCircleView2;

    @OnClick(R.id.img_left)
    void onCloseImgClick() {
        finish();
        MainActivity.show(PerfectUserInfoActivity.this);
    }

    @OnClick(R.id.img_right)
    void onConfirmImgClick() {
        String nickname = mNicknameEdit.getText().toString();
        long uid = Long.parseLong(AccountComponent.shareComponent().getLoginUserInfoOrNil().mUid);
        if (gender == -1) {
            Utils.showShortToast("请选择性别");
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            Utils.showShortToast("昵称不能为空哦");
            return;
        }
        if (TextUtils.isEmpty(avatar)) {
            Utils.showShortToast("请上传头像");
            return;
        }
        String phone = AccountComponent.shareComponent().getLoginUserInfoOrNil().mPhone;
        UserInfo userInfo = new UserInfo(uid, nickname, null, avatar,phone, gender, Constants.LOGIN_TYPE_M);
        Constants.IS_UPDATED = true;
        AccountComponent.shareComponent().UpLoadUserInfo(this, userInfo, Constants.UPLOAD_ALL);
    }

    @OnClick(R.id.img_male)
    void onMaleClick() {
        gender = 0;
        mMaleImg.setImageResource(R.drawable.ic_head_male_click);
        mFemaleImg.setImageResource(R.drawable.ic_head_female_normal);
    }

    @OnClick(R.id.img_female)
    void onFemaleClick() {
        gender = 1;
        mMaleImg.setImageResource(R.drawable.ic_head_male_normal);
        mFemaleImg.setImageResource(R.drawable.ic_head_female_click);
    }

    @OnClick(R.id.img_takephoto)
    void onTakePhotoClick() {
        new CaptureDialog(this).show();
    }

    /**
     * sex = 0 男
     * sez =1 女
     */
    private int gender = -1;
    private String avatar;

    public static void show(Context context) {
        Intent intent = new Intent(context, PerfectUserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_act_perfect_usreinfo);
        ButterKnife.inject(this);
        initTitle();
        startCircleViewAnim(mAnimCircleView1, 0);
        startCircleViewAnim(mAnimCircleView2, 1000);

    }

    private void initTitle() {

        mLeftImg.setImageResource(R.drawable.selector_img_close);
        mRightImg.setImageResource(R.drawable.selector_img_confirm);
        mTitleImg.setVisibility(View.GONE);
        mTitleTxt.setText(R.string.perferctuserinfo_title);
        mTitleTxt.setTextColor(getResources().getColor(R.color.black));
    }


    private void startCircleViewAnim(View view, long time) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_X, 1f, 1.8f);
        scaleXAnim.setRepeatMode(ObjectAnimator.RESTART);
        scaleXAnim.setRepeatCount(ObjectAnimator.INFINITE);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, Constants.SCALE_Y, 1f, 1.8f);
        scaleYAnim.setRepeatMode(ObjectAnimator.RESTART);
        scaleYAnim.setRepeatCount(ObjectAnimator.INFINITE);
        animatorSet.playTogether(scaleXAnim, scaleYAnim, ObjectAnimator.ofFloat(view, Constants.ALPHA, 1f, 0f));
        animatorSet.setDuration(2000);
        animatorSet.setStartDelay(time);
        animatorSet.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = AccountComponent.shareComponent().getHeadUri();
            if (requestCode == Constants.REQUEST_CODE_CAPTURE_CAMEIA) {
                if (fileUri != null) {
                    String imgpath = fileUri.getPath();
                    ByLogout.out("拍照返回->" + imgpath);
                    avatar = imgpath;
                    ImageLoader.getInstance().setImageLocal(mTakePhoto, imgpath);
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
                    avatar = uploadPhotPathExt;
                    ImageLoader.getInstance().setImageLocal(mTakePhoto, uploadPhotPathExt);
                }
            }
        }
    }


}
