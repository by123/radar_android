package com.brotherhood.o2o.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/2.
 */
public class TestActivity extends Activity {

    @InjectView(R.id.movetab)
    View mMoveTab;

    @InjectView(R.id.tab1)
    View mTab1;

    @InjectView(R.id.tab2)
    View mTab2;

    @InjectView(R.id.tab3)
    View mTab3;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @OnClick(R.id.tab1)
    void onTab1Click() {
        isClickTab=true;
        if (mCurrentPosition == 0) {
            mMoveTab.offsetLeftAndRight(0);
        } else if (mCurrentPosition == 1) {
            mMoveTab.offsetLeftAndRight(-Constants.SCREEN_WIDTH / 3);
        } else {
            mMoveTab.offsetLeftAndRight(-Constants.SCREEN_WIDTH * 2 / 3);
        }
        mCurrentPosition = 0;
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    @OnClick(R.id.tab2)
    void onTab2Click() {
        isClickTab=true;
        if (mCurrentPosition == 0) {
            mMoveTab.offsetLeftAndRight(Constants.SCREEN_WIDTH / 3);
        } else if (mCurrentPosition == 1) {
            mMoveTab.offsetLeftAndRight(0);
        } else {
            mMoveTab.offsetLeftAndRight(-Constants.SCREEN_WIDTH / 3);
        }
        mCurrentPosition = 1;
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    @OnClick(R.id.tab3)
    void onTab3Click() {
        isClickTab=true;
        if (mCurrentPosition == 0) {
            mMoveTab.offsetLeftAndRight(Constants.SCREEN_WIDTH * 2 / 3);
        } else if (mCurrentPosition == 1) {
            mMoveTab.offsetLeftAndRight(Constants.SCREEN_WIDTH / 3);
        } else {
            mMoveTab.offsetLeftAndRight(0);
        }
        mCurrentPosition = 2;
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    private int mCurrentPosition = 0;
    private boolean isClickTab=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ButterKnife.inject(this);
        setScreen();
        initView();
    }

    int left;
    int right;
    private void initView() {
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(Constants.SCREEN_WIDTH / 3, Utils.dip2px(4));
        mMoveTab.setLayoutParams(params);
        mMoveTab.setPadding(20,0,20,0);
        setViewPager();
    }

    private void setViewPager() {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.test3, null);
            TextView textView = (TextView)view.findViewById(R.id.textview);
            textView.setText("第" + i + "页");
            views.add(view);
        }
        mViewPager.setAdapter(new ViewAdapter(views));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
               @Override
               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                   int move = (int) positionOffsetPixels / 3;
                   ByLogout.out("move->"+move);

                   if(position == 0)
                   {
                       left=0;
                       right=Constants.SCREEN_WIDTH/3;
                   }
                   else if(position == 1)
                   {
                       left=Constants.SCREEN_WIDTH/3;
                       right=Constants.SCREEN_WIDTH * 2/3;
                   }
                   else
                   {
                       left=Constants.SCREEN_WIDTH * 2/3;
                       right=Constants.SCREEN_WIDTH;
                   }
                   if (move + left > 0 && move + left < Constants.SCREEN_WIDTH * 2 / 3) {
                       mMoveTab.layout(left + move, mMoveTab.getTop(), right + move, mMoveTab.getBottom());
                   }
                   ByLogout.out(left+"=left");
                   ByLogout.out(right+"=right");

               }

               @Override
               public void onPageSelected(int position) {
                   ByLogout.out("位置->"+position);
                   if(!isClickTab)
                   {
                       if(mCurrentPosition < position)
                       {
                           if (position == 0)
                           {
                               mMoveTab.offsetLeftAndRight(0);
                           }
                           else
                           {
                               mMoveTab.offsetLeftAndRight(Constants.SCREEN_WIDTH / 3);
                           }
                       }
                       else
                       {
                           ByLogout.out("mCurrentPosition->" + mCurrentPosition);
                           if (position == 1|| position == 0)
                           {
                               mMoveTab.offsetLeftAndRight(-Constants.SCREEN_WIDTH / 3);
                           }
                           else
                           {
                               mMoveTab.offsetLeftAndRight(0);
                           }
                       }
                       mCurrentPosition = position;
                   }
                   isClickTab=false;
               }

               @Override
               public void onPageScrollStateChanged(int state) {

               }
           }
        );
    }


    private class ViewAdapter extends PagerAdapter {
        List<View> viewLists;

        public ViewAdapter(List<View> lists) {
            viewLists = lists;
        }

        @Override
        public int getCount() {
            return viewLists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View view, int position, Object object) {
            ((ViewPager) view).removeView(viewLists.get(position));
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView(viewLists.get(position), 0);

            return viewLists.get(position);
        }
    }

    private void setScreen() {
        Constants.SCREEN_WIDTH = Utils.getScreenWidth(this);
        Constants.SCREEN_HEIGHT = Utils.getScreentHeight(this);
    }
}
