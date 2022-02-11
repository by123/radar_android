package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.brotherhood.o2o.ui.adapter.OverseaChoosePhotoAdapter;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.DisplayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OverseaChoosePhotoActivity extends BaseActivity {

    @ViewInject(id = R.id.rvChoosePhoto)
    private RecyclerView mRecyclerView;

    private OverseaChoosePhotoAdapter mAdapter;
    private static final int NUMBER_COLUMNS = 4;
    private static final int mItemSpace = DisplayUtil.dp2px(3);
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CROP_FROM_PHOTO_CODE = 2;
    private List<String> mPathList = new ArrayList<>();
    private String mImagePath;
    private Uri mImageUri;
    private TextView mTvSend;
    public int mBtnType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_photo_layout;
    }

    public static void show(Context context){
        Intent it = new Intent(context, OverseaChoosePhotoAdapter.class);
        context.startActivity(it);
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    public void confirm(){
        if (mAdapter == null){
            return;
        }
        if (!mAdapter.hasChecked()){
            ColorfulToast.orange(OverseaChoosePhotoActivity.this, getString(R.string.choose_picture), Toast.LENGTH_SHORT);
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
                .setBaseTitle(R.string.select_image, R.color.black)
                .addTextItem(R.id.abRightText, R.string.confirm_btn)
                .hideHorizontalDivide();

        /*if (mBtnType == 1){
            getActionBarController().addTextItem(R.id.abRightText, R.string.confirm_btn);
        }else {
            getActionBarController().addTextItem2(R.id.abRightText, R.string.confirm_btn);
        }*/
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new PhotoDecoration(NUMBER_COLUMNS, mItemSpace, false));
        mPathList.addAll(DeviceUtil.getImageFromAlbum(this));
        mAdapter = new OverseaChoosePhotoAdapter(this, mPathList);
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
        mAdapter.setOnPhotoClickListener( new OnPhotoClickListener() {
            @Override
            public void onPhotoClick(ImageView imageView, int position) {
                if (position == 0){//拍照
                    mImagePath = System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
                    mImageUri = Uri.fromFile(new File(DirManager.getExternalStroageDir(Constants.PHOTO_OUT_DIR), mImagePath));
                    /**
                     把照片路径插入图库数据库表中
                     //String filename = DateUtil.parseTimeToString(System.currentTimeMillis(), "yyyy_MM_dd_HH_mm_ss");
                     //ContentValues values = new ContentValues();
                     //values.put(MediaStore.Images.Media.TITLE, filename);
                     //mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                     */
                    DeviceUtil.startCamera(OverseaChoosePhotoActivity.this, mImageUri, CAMERA_REQUEST_CODE);

                }else {//显示预览大图
                    //todo
                    String path = mPathList.get(position - 1);
                    ImgPreviewActivity.show(OverseaChoosePhotoActivity.this, path);
                    //Intent intent = new Intent(ChoosePhotoActivity.this, PhotoPreviewActivity.class);
                    //intent.putExtra("path", path);//非必须
                    //intent.putExtra("position", position);
                    //int[] location = new int[2];
                    //imageView.getLocationOnScreen(location);
                    //intent.putExtra("locationX", location[0]);//必须
                    //intent.putExtra("locationY", location[1]);//必须
                    //
                    //intent.putExtra("width", imageView.getWidth());//必须
                    //intent.putExtra("height", imageView.getHeight());//必须
                    //startActivity(intent);
                    //overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
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
        if (TextUtils.isEmpty(path) || !new File(path).exists()){
            ColorfulToast.orange(OverseaChoosePhotoActivity.this, getString(R.string.choose_photo_notfound), Toast.LENGTH_SHORT);
            return;
        }
        mImagePath = DirManager.getExternalStroageDir(Constants.PHOTO_CROP_DIR) + "/" + System.currentTimeMillis() + Constants.PHOTO_EXTENSION;
        Uri outputUri = Uri.fromFile(new File(mImagePath));
        DeviceUtil.startCropPhoto(OverseaChoosePhotoActivity.this, mImageUri, outputUri, CROP_FROM_PHOTO_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
            case R.id.abRightText:
                confirm();
                break;
        }
    }
}
