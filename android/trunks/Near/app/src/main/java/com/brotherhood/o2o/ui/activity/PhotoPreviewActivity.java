package com.brotherhood.o2o.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.widget.account.SmoothImageView;

import java.io.File;

/**
 * 图片预览
 * 1、Bitmap的缩放，因为缩略图和详情图的缩放比例肯定不一样
 * 2、Bitmap位置的平移，因为缩略图的位置是不确定的，我们要使他平移到中间
 * 3、Bitmap的切割，因为CENTER_CROP是切割过得，而FIT_CENTER是没有切割的，那么两幅图显示的内容区域是不同的，所以也要显示区域的平滑变换。
 */
public class PhotoPreviewActivity extends AppCompatActivity {

    private SmoothImageView mSmoothImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getStringExtra("path");
        int position = getIntent().getIntExtra("position", 0);
        int mLocationX = getIntent().getIntExtra("locationX", 0);
        int mLocationY = getIntent().getIntExtra("locationY", 0);
        int mWidth = getIntent().getIntExtra("width", 0);
        int mHeight = getIntent().getIntExtra("height", 0);

        mSmoothImageView = new SmoothImageView(this);
        mSmoothImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        mSmoothImageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mSmoothImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(mSmoothImageView);

        File file = new File(path);
        ImageLoaderManager.displayImageByFile(this, mSmoothImageView, file);
        mSmoothImageView.transformIn();
        mSmoothImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothImageView.transformOut();
            }
        });

        mSmoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == SmoothImageView.STATE_TRANSFORM_OUT){
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        mSmoothImageView.transformOut();
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
