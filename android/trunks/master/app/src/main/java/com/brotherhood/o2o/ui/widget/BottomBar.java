package com.brotherhood.o2o.ui.widget;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.database.PreferenceHelper;
import com.brotherhood.o2o.config.Constants;

/**
 * Created by by.huang on 2015/6/3.
 */
public class BottomBar {

    private static int mCurrent = 0;
    private static View[] views = new View[3];

    public interface OnSelectItemListener {
        void OnSelectedItem(int position);
    }

    public static void addBottomBar(Activity activity, final OnSelectItemListener listener) {
        if (activity == null) {
            return;
        }
        try {
            final View mItem1 = activity.findViewById(R.id.bottombar_item1);
            setValues(mItem1, R.drawable.ic_category_normal, activity.getString(R.string.bottom_category));
            views[0] = mItem1;
            setOnTouchListener(mItem1, 0, R.drawable.ic_category_press, listener);

            final View mItem2 = activity.findViewById(R.id.bottombar_item2);
            setValues(mItem2, R.drawable.ic_explore_normal, activity.getString(R.string.bottom_explore));
            views[1] = mItem2;
            setOnTouchListener(mItem2, 1, R.drawable.ic_explore_press, listener);

            final View mItem3 = activity.findViewById(R.id.bottombar_item3);
            setValues(mItem3, R.drawable.ic_personal_normal, activity.getString(R.string.bottom_personal));
            views[2] = mItem3;
            setOnTouchListener(mItem3, 2, R.drawable.ic_personal_press, listener);

            mCurrent = PreferenceHelper.sharePreference(MyApplication.mApplication).getInt(Constants.PREFER_BOTTOMBAR, 0);
            if (mCurrent == 0) {
                pressed(mItem1, R.drawable.ic_category_press);
            } else if (mCurrent == 1) {
                pressed(mItem2, R.drawable.ic_explore_press);
            } else {
                pressed(mItem3, R.drawable.ic_personal_press);
            }
        } catch (Exception e) {
        }
    }

    private static void setOnTouchListener(View view, final int current, final int resId, final OnSelectItemListener listener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    normalAll();
                    pressed(view, resId);
                    mCurrent = current;
                    PreferenceHelper.sharePreference(MyApplication.mApplication).setInt(Constants.PREFER_BOTTOMBAR, mCurrent);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    listener.OnSelectedItem(current);
                }
                return true;
            }
        });
    }

    private static void setValues(final View rootView, int resId, String text) {
        final ImageView mImageView = (ImageView) rootView.findViewById(R.id.img_item);
        final TextView mTextView = (TextView) rootView.findViewById(R.id.txt_item);
        mImageView.setImageResource(resId);
        mTextView.setText(text);
    }


    private static void pressed(View rootView, int resId) {
        final ImageView mImageView = (ImageView) rootView.findViewById(R.id.img_item);
        final TextView mTextView = (TextView) rootView.findViewById(R.id.txt_item);
        mTextView.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.main_red));
        mImageView.setImageResource(resId);
    }

    private static void normalAll() {
        if (views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                final ImageView mImageView = (ImageView) views[i].findViewById(R.id.img_item);
                final TextView mTextView = (TextView) views[i].findViewById(R.id.txt_item);
                mTextView.setTextColor(MyApplication.mApplication.getResources().getColor(R.color.gray));
                if (i == 0) {
                    mImageView.setImageResource(R.drawable.ic_category_normal);
                }
                if (i == 1) {
                    mImageView.setImageResource(R.drawable.ic_explore_normal);
                }
                if (i == 2) {
                    mImageView.setImageResource(R.drawable.ic_personal_normal);
                }
            }
        }
    }
}
