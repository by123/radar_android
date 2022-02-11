package com.brotherhood.o2o.surprise.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/30.
 */
public class MySurpriseActivity extends BaseActivity {

    @InjectView(R.id.pager_surprise)
    ViewPager mViewPager;

    @OnClick(R.id.btn_left)
    void OnLeftBtnClick() {
        finish();
    }

    @InjectView(R.id.txt_reward)
    TextView mRewardTxt;

    @InjectView(R.id.line_reward)
    View mRewardLine;

    @InjectView(R.id.txt_coupon)
    TextView mCouponTxt;

    @InjectView(R.id.line_coupon)
    View mCouponLine;

    @OnClick(R.id.layout_reward)
    void OnReWardLayoutClick() {
        onSelectLeft();
        mViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.layout_coupon)
    void OnCouponLayoutClick() {
        onSelectRight();
        mViewPager.setCurrentItem(1);
    }

    private static final String EXTRA_TYPE = "extra_type";
    public static final int TYPE_REWARD = 0;
    public static final int TYPE_COUPON = 1;

    public static void show(Context context, int type) {
        Intent intent = new Intent(context, MySurpriseActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surprise_act_my_surprise);
        ButterKnife.inject(this);
        initView();
        int type = getIntent().getIntExtra(EXTRA_TYPE, -1);
        mViewPager.setCurrentItem(type);
    }

    private void initView() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return new ItemRewardFragment();
                } else {
                    return new CouponRewardFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "实物";
                }

                return "title - " + position;
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    onSelectLeft();
                } else {
                    onSelectRight();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void onSelectLeft() {
        mRewardTxt.setTextColor(getResources().getColor(R.color.text_red));
        mRewardLine.setVisibility(View.VISIBLE);
        mCouponTxt.setTextColor(getResources().getColor(R.color.text_gray));
        mCouponLine.setVisibility(View.INVISIBLE);
    }

    private void onSelectRight() {
        mRewardTxt.setTextColor(getResources().getColor(R.color.text_gray));
        mRewardLine.setVisibility(View.INVISIBLE);
        mCouponTxt.setTextColor(getResources().getColor(R.color.text_red));
        mCouponLine.setVisibility(View.VISIBLE);
    }
}
