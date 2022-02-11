package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.adapter.NearbyPagerAdapter;
import com.brotherhood.o2o.ui.fragment.OverseaFoodListFragment;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.util.FastBlur;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 周边服务（美食列表）
 */
public class NearbyServiceActivity extends BaseActivity {

    @ViewInject(id = R.id.vpFoodList)
    private ViewPager mViewPager;

    private FragmentManager fragmentManager;
    private NearbyPagerAdapter mPagerAdapter;
    private List<BaseFragment> mPagerList = new ArrayList<>();
    private int mDrawableId;
    private BitmapDrawable mBlurDrawable;
    private View mViBlur;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nearby_food_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    public static void show(Context context, @DrawableRes int drawableId){
        Intent it = new Intent(context, NearbyServiceActivity.class);
        it.putExtra(BundleKey.NEARBY_BACKGROUND_IMAGE_ID, drawableId);

        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_white)
                .setDivideColor(R.color.white)
                .setBaseTitle(R.string.near_by_food, R.color.white)
                .addIconItem(R.id.abRightImage, R.mipmap.near_search_image)
                .setHeadBackgroundColor(R.color.food_list_bg_color)
                .hideHorizontalDivide();
        mDrawableId = getIntent().getIntExtra(BundleKey.NEARBY_BACKGROUND_IMAGE_ID, 0);
        if (mDrawableId != 0){
            mViBlur = getWindow().getDecorView();
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(mDrawableId);
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()){
                mBlurDrawable = new BitmapDrawable(getResources(), FastBlur.blur(bitmap, mViBlur));
            }
            if (mBlurDrawable != null){
                ViewUtil.setViewBackground(mViBlur, mBlurDrawable);
            }
            ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                view.setBackgroundColor(getResources().getColor(R.color.food_list_bg_color));
            }
        }
        fragmentManager = getSupportFragmentManager();
        mPagerList.add(new OverseaFoodListFragment());
        mPagerAdapter = new NearbyPagerAdapter(fragmentManager, mPagerList);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBlurDrawable != null){
            if (mBlurDrawable != null){
                ViewUtil.setViewBackground(mViBlur, null);
            }
            mBlurDrawable = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
            case R.id.abRightImage:
                OverseaSearchFoodActivity.show(NearbyServiceActivity.this, mDrawableId);
                break;
        }
    }
}
