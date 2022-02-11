package com.brotherhood.o2o.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.ui.ImgPreviewActivity;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnPhotoCheckedListener;
import com.brotherhood.o2o.listener.OnPhotoClickListener;
import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.adapter.ChoosePhotoAdapter;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.DisplayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChoosePhotoActivity extends BaseActivity {

    @ViewInject(id = R.id.rvChoosePhoto)
    private RecyclerView mRecyclerView;

    private ChoosePhotoAdapter mAdapter;
    private static final int NUMBER_COLUMNS = 4;
    private static final int mItemSpace = DisplayUtil.dp2px(3);
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CROP_FROM_PHOTO_CODE = 2;
    private List<String> mPathList = new ArrayList<>();
    private String mImagePath;
    private Uri mImageUri;

    private boolean mHideCamera;
    public int mBtnType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_photo_layout;
    }

    /**
     * @param context
     * @param requestCode
     * @param hideCamera  是否隐藏拍照项
     */
    public static void show(Activity context, int requestCode, boolean hideCamera) {
        Intent it = new Intent(context, ChoosePhotoActivity.class);
        it.putExtra(BundleKey.CHOOSE_PHOTO_HIDECAMERA_KEY, hideCamera);
        context.startActivityForResult(it, requestCode);
    }

    /**
     * @param context
     * @param requestCode
     * @param hideCamera  是否隐藏拍照项
     */
    public static void show(Activity context, int requestCode, boolean hideCamera, int btnType) {
        Intent it = new Intent(context, ChoosePhotoActivity.class);
        it.putExtra(BundleKey.CHOOSE_PHOTO_HIDECAMERA_KEY, hideCamera);
        it.putExtra(BundleKey.CHOOSE_PHOTO_BTNTYPE_KEY, btnType);
        context.startActivityForResult(it, requestCode);
    }

    /**
     * @param context
     * @param requestCode
     * @param btnType       发送按钮是否有背景
     */
    public static void showResult(Activity context, int requestCode, int btnType, boolean hideCamera) {
        Intent it = new Intent(context, ChoosePhotoActivity.class);
        it.putExtra(BundleKey.CHOOSE_PHOTO_BTNTYPE_KEY, btnType);
        it.putExtra(BundleKey.CHOOSE_PHOTO_HIDECAMERA_KEY, hideCamera);
        context.startActivityForResult(it, requestCode);
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    public void confirm() {
        if (mAdapter == null) {
            return;
        }
        if (!mAdapter.hasChecked()) {
            ColorfulToast.orange(ChoosePhotoActivity.this, getString(R.string.choose_not_check_any_image), Toast.LENGTH_SHORT);
            return;
        }
        mImagePath = mAdapter.getCheckedImagePath();
        mImageUri = Uri.fromFile(new File(mImagePath));
        clipPhoto();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBtnType = getIntent().getIntExtra(BundleKey.CHOOSE_PHOTO_BTNTYPE_KEY, -1);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.select_photo, R.color.black)
                .hideHorizontalDivide();

        if (mBtnType == 1) {
            getActionBarController().addTextItem2(R.id.abRightText, R.string.choose_btn_send);
        } else {
            getActionBarController().addTextItem(R.id.abRightText, R.string.confirm);
        }
        mHideCamera = getIntent().getBooleanExtra(BundleKey.CHOOSE_PHOTO_HIDECAMERA_KEY, false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new PhotoDecoration(NUMBER_COLUMNS, mItemSpace, false));
        mPathList.addAll(DeviceUtil.getImageFromAlbum(this));
        mAdapter = new ChoosePhotoAdapter(this, mPathList);
        if (mHideCamera) {
            mAdapter.hideCamera();
        }
        mRecyclerView.setAdapter(mAdapter);
        initEvent();
    }

    private void initEvent() {
        mAdapter.setOnPhotoCheckedListener(new OnPhotoCheckedListener() {
            @Override
            public void onCheckedChanged(int position) {
                mAdapter.setChecked(position);
            }
        });
        mAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onPhotoClick(ImageView imageView, int position) {
                if (!mHideCamera) {
                    if (position == 0) {//拍照
                        mImagePath = System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
                        mImageUri = Uri.fromFile(new File(DirManager.getExternalStroageDir(Constants.PHOTO_OUT_DIR), mImagePath));
                        /**
                         把照片路径插入图库数据库表中
                         //String filename = DateUtil.parseTimeToString(System.currentTimeMillis(), "yyyy_MM_dd_HH_mm_ss");
                         //ContentValues values = new ContentValues();
                         //values.put(MediaStore.Images.Media.TITLE, filename);
                         //mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                         */
                        DeviceUtil.startCamera(ChoosePhotoActivity.this, mImageUri, CAMERA_REQUEST_CODE);
                    } else {//显示预览大图
                        String path = mPathList.get(position - 1);
                        ImgPreviewActivity.show(ChoosePhotoActivity.this, path);
                    }
                } else {
                    String path = mPathList.get(position);
                    ImgPreviewActivity.show(ChoosePhotoActivity.this, path);
                }
            }
        });
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
                Intent backIntent = new Intent();
                backIntent.putExtra(BundleKey.CHOOSE_HEAD_PHOTO_KEY, mImagePath);
                setResult(RESULT_OK, backIntent);
                finish();
                break;
        }
    }

    /**
     * 图片裁剪
     */
    private void clipPhoto() {
        String path = mImageUri.getPath();

        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            ColorfulToast.orange(ChoosePhotoActivity.this, getString(R.string.choose_photo_notfound), Toast.LENGTH_SHORT);
            return;
        }
        mImagePath = DirManager.getExternalStroageDir(Constants.PHOTO_CROP_DIR) + "/" +System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
        Uri outputUri = Uri.fromFile(new File(mImagePath));
        DeviceUtil.startCropPhoto(ChoosePhotoActivity.this, mImageUri, outputUri, CROP_FROM_PHOTO_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRightText:
                confirm();
                break;
        }
    }
}
