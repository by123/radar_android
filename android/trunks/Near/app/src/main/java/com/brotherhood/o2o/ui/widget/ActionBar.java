package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.util.ViewUtil;

/**
 * 通用头部
 */
public class ActionBar extends LinearLayout {
	private LinearLayout mRightLayout;
	private ImageView mIvBack;
	private TextView mTvTitle;
	private View mViDivide;
	private View mViBackground;
	private View mHorizontalDivide;
	private int mActionType = ActionBarController.CENTER_TYPE;

	public ActionBar(final Context context) {
		this(context, null);
	}

	public ActionBar(final Context context,final AttributeSet attrs) {
		super(context, attrs);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);
			mActionType = a.getInteger(R.styleable.ActionBar_headType, ActionBarController.CENTER_TYPE);
		}
		setupViews(context);
	}
	private void setupViews(Context context) {
		final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (mActionType == ActionBarController.LEFT_TYPE){
			mInflater.inflate(R.layout.action_bar_left_view, this);
			mViDivide = findViewById(R.id.abDivide);
		}else {
			mInflater.inflate(R.layout.action_bar_center_view, this);
		}
		mHorizontalDivide = findViewById(R.id.abHorizontalDivide);
		mViBackground = findViewById(R.id.abAlpha);
		mRightLayout = (LinearLayout) findViewById(R.id.abRight);
		mIvBack = (ImageView) findViewById(R.id.abBack);
		mTvTitle = (TextView) findViewById(R.id.abTitle);
	}

	public void setBackImage(@DrawableRes int resourceId){
		mIvBack.setImageResource(resourceId);
	}
	public void setTitleBackgroundColor(@ColorRes int colorId){
		mViBackground.setBackgroundColor(getResources().getColor(colorId));
	}
	public void setTitle(int stringId){
		mTvTitle.setText(stringId);
	}
	public void setTitleTextColor(@ColorRes int colorId){
		mTvTitle.setTextColor(getResources().getColor(colorId));
	}
    public void setTitle(String string){
		mTvTitle.setText(string);
    }
	public void hideHorizontalDivide(){
		mHorizontalDivide.setVisibility(View.GONE);
	}

	public void setDivideColor(@ColorRes int colorId){
		if (mViDivide == null){
			LogManager.e("=================action bar divide view is null===============");
			return;
		}
		ViewUtil.setViewBackground(mViDivide, colorId);
	}

	public LinearLayout getRightLayout() {
		return mRightLayout;
	}

}
