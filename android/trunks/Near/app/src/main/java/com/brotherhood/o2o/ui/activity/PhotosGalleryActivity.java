package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.adapter.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laimo.li on 2016/1/22.
 */
public class PhotosGalleryActivity extends BaseActivity {

    public static final String KEY_PHOTOS = "key_photos";
    public static final String KEY_CURRENT_PHOTO = "key_current_photo";

    private List<String> photos;

    @ViewInject(id = R.id.photosGalleryViewPager)
    private ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photos_gallery_layout;
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

        photos = getIntent().getStringArrayListExtra(KEY_PHOTOS);
        int position = getIntent().getIntExtra(KEY_CURRENT_PHOTO, 0);

        GalleryAdapter adapter = new GalleryAdapter(this, photos);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActionBarController().setBaseTitle((position + 1) + "/" + photos.size(), R.color.white);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(position);

        getActionBarController().setDivideColor(R.color.white).setBackImage(R.mipmap.back_image_white).setBaseTitle((position + 1) + "/" + photos.size(), R.color.white)
                .setHeadBackgroundColor(R.color.black).hideHorizontalDivide();
    }


    public static void show(Context context, ArrayList<String> photos, int position) {
        Intent intent = new Intent(context, PhotosGalleryActivity.class);
        intent.putStringArrayListExtra(KEY_PHOTOS, photos);
        intent.putExtra(KEY_CURRENT_PHOTO, position);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
    }
}
