package com.brotherhood.o2o.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.widget.ActionBar;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.ViewUtil;

/**
 * ActionBar控制器
 */
public class ActionBarController{

	public static final int LEFT_TYPE = 1;
	public static final int CENTER_TYPE = 2;

	private ActionBar mActionBar;
	private Context mContext;
	private Activity mActivity;
	private View mAlphaView;
	private View.OnClickListener mOnClickListener;
	private int mType;

	public ActionBarController(Activity activity, int type, View.OnClickListener onClickListener) {
		if (type == LEFT_TYPE){
			mActionBar = (ActionBar) activity.findViewById(R.id.abLeftBase);
		}else if (type == CENTER_TYPE){
			mActionBar = (ActionBar) activity.findViewById(R.id.abCenterBase);
		}
		mContext = activity;
		mType = type;
		mActivity = activity;
		mOnClickListener = onClickListener;
		init();
	}

	public ActionBarController(View view, int type, View.OnClickListener onClickListener) {
		if (type == LEFT_TYPE){
			mActionBar = (ActionBar) view.findViewById(R.id.abLeftBase);
		}else if (type == CENTER_TYPE){
			mActionBar = (ActionBar) view.findViewById(R.id.abCenterBase);
		}
		mContext = view.getContext();
		mType = type;
		mOnClickListener = onClickListener;
		init();

	}

	private void init() {
		mAlphaView = ViewUtil.findView(mActionBar, R.id.abAlpha);
		ViewUtil.findViewAttachOnclick(mActionBar, R.id.abBack, mOnClickListener);
	}

	/***
	 * 添加带图标的item
	 * @param iconId
	 * @param viewId
	 * @return
	 */
	public ActionBarController addIconItem(int viewId, int iconId) {
		ImageView imageView = new ImageView(mContext);
		ViewUtil.setViewBackground(imageView, R.drawable.common_view_btn_bg);
		imageView.setPadding(DisplayUtil.dp2px(5), 0, DisplayUtil.dp2px(5), 0);
		imageView.setLayoutParams(createMenuItemLayoutParams());
		imageView.setOnClickListener(mOnClickListener);
		imageView.setId(viewId);
		imageView.setImageResource(iconId);
		imageView.setScaleType(ImageView.ScaleType.CENTER);
		mActionBar.getRightLayout().addView(imageView);
		return this;
	}

	public ActionBarController setHeadBackgroundColor(@ColorRes int colorId){
		mActionBar.setTitleBackgroundColor(colorId);
		return this;
	}
	/**
	 * 添加带文字的item
	 * @param stringId @StringRes
	 * @param viewId
	 * @return
	 */
	public ActionBarController addTextItem(int viewId, @StringRes int stringId){
		TextView textView = new TextView(mContext);
		textView.setGravity(Gravity.CENTER);
		final Resources resources = mContext.getResources();
		textView.setLayoutParams(createMenuItemLayoutParams3());
		textView.setTextSize(15);
		textView.setSingleLine(true);
		textView.setMinWidth((int) mContext.getResources().getDimension(R.dimen.common_titlebar_height));
		textView.setTextColor(resources.getColor(R.color.near_main_orange_color));
		//textView.setTextColor(resources.getColor(R.color.white));
		textView.setId(viewId);
		textView.setPadding(DisplayUtil.dp2px(5), 0, DisplayUtil.dp2px(5), 0);
		textView.setText(stringId);
		textView.setOnClickListener(mOnClickListener);
		ViewUtil.setViewBackground(textView, R.drawable.common_view_btn_bg);
		mActionBar.getRightLayout().addView(textView);
		return this;
	}

	/**
	 * 添加带橙色背景的文字item
	 * @param stringId @StringRes
	 * @param viewId
	 * @return
	 */
	public ActionBarController addTextItem2(int viewId, @StringRes int stringId){
		TextView textView = new TextView(mContext);
		textView.setGravity(Gravity.CENTER);
		final Resources resources = mContext.getResources();
		textView.setLayoutParams(createMenuItemLayoutParams2());
		textView.setTextSize(15);
		textView.setTextColor(resources.getColor(R.color.white));
		textView.setId(viewId);
		textView.setText(stringId);
		textView.setOnClickListener(mOnClickListener);
		ViewUtil.setViewBackground(textView, R.drawable.choose_send_btn_bg);
		mActionBar.getRightLayout().addView(textView);
		mActionBar.getRightLayout().setPadding(0, 0, DisplayUtil.dp2px(10), 0);
		return this;
	}



	/**
	 * 设置分割线颜色
	 * @param colorId
	 * @return
	 */
	public ActionBarController setDivideColor(@ColorRes int colorId){
		if (mType == LEFT_TYPE){
			mActionBar.setDivideColor(colorId);
		}
		return this;
	}

	/**
	 * 隐藏头部下边缘分割线
	 * @return
	 */
	public ActionBarController hideHorizontalDivide(){
		mActionBar.hideHorizontalDivide();
		return this;
	}

	/**
	 * 修改回退按钮图标
	 * @param drawableId
	 */
	public ActionBarController setBackImage(@DrawableRes int drawableId){
		mActionBar.setBackImage(drawableId);
		return this;
	}

	/**
	 * 修改带文字item的文字
	 * @param stringId
	 * @param viewId
	 */
	public ActionBarController setItemText(@StringRes int stringId, int viewId){
		View rightView = mActionBar.getRightLayout().getChildAt(0);
		if (rightView instanceof TextView && rightView.getId() == viewId){
			((TextView) rightView).setText(stringId);
		}
		return this;
	}

	/**
	 * 设置右边按钮的显示和隐藏
	 * @param isVisible
	 */
	public ActionBarController setItemVisible(boolean isVisible){
		View rightView = mActionBar.getRightLayout().getChildAt(0);
		if (isVisible){
			rightView.setVisibility(View.VISIBLE);
		}else {
			rightView.setVisibility(View.GONE);
		}
		return this;
	}

	/**
	 * 设置标题
	 * @param stringId
	 * @return
	 */
	public ActionBarController setBaseTitle(@StringRes int stringId, @ColorRes int colorId) {
		mActionBar.setTitle(stringId);
		mActionBar.setTitleTextColor(colorId);
		return this;
	}

    /**
     * 设置标题
     * @param title
     * @return
     */
    public ActionBarController setBaseTitle(String title, @ColorRes int colorId) {
        mActionBar.setTitle(title);
		mActionBar.setTitleTextColor(colorId);
        return this;
    }

	/**
	 * 设置背景透明度
	 * @param alpha
	 * @return
	 */
	public ActionBarController setAlpha(float alpha) {
		mAlphaView.setAlpha(alpha);
		return this;
	}

	public ActionBarController setActivity(Activity activity) {
		mActivity = activity;
		return this;
	}

	public ActionBar getActionBar() {
		return mActionBar;
	}

	/**
	 * 固定宽高48dp
	 * @return
	 */
	private LinearLayout.LayoutParams createMenuItemLayoutParams() {
		int size = (int) mContext.getResources().getDimension(R.dimen.common_titlebar_height);
		return new LinearLayout.LayoutParams(size, size);
	}

	private LinearLayout.LayoutParams createMenuItemLayoutParams2() {
		int width = DisplayUtil.dp2px(50);
		int height = DisplayUtil.dp2px(34);
		return new LinearLayout.LayoutParams(width, height);
	}

	private LinearLayout.LayoutParams createMenuItemLayoutParams3() {
		int height = (int) mContext.getResources().getDimension(R.dimen.common_titlebar_height);
		int width = (int) mContext.getResources().getDimension(R.dimen.titlebar_right_text_width);
		return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
	}
}
