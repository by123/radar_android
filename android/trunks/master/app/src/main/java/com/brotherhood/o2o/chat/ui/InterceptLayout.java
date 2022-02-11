package com.brotherhood.o2o.chat.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.brotherhood.o2o.chat.utils.SkipProguardInterface;

public class InterceptLayout extends LinearLayout implements
		SkipProguardInterface {

	public InterceptLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptLayout(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mListener != null) {
			return mListener.onIntercept(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	private InterceptTouchListener mListener;

	public void setInterceptListener(InterceptTouchListener l) {
		mListener = l;
	}

	public static interface InterceptTouchListener {

		public boolean onIntercept(MotionEvent ev);
	}

}
