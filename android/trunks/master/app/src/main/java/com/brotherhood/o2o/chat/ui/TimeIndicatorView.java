package com.brotherhood.o2o.chat.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.brotherhood.o2o.chat.utils.SkipProguardInterface;

public class TimeIndicatorView extends View implements SkipProguardInterface {

	public static interface EventListener {

		public void onEnd();
	}

	private static final int DEFAULT_DURATION = 8000;

	@SuppressLint("NewApi")
	public TimeIndicatorView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public TimeIndicatorView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TimeIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimeIndicatorView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mCancelCalled) {
			mCancelCalled = false;
			canvas.drawColor(Color.TRANSPARENT);
			return;
		}

		if (!mStarted) {
			return;
		}

		int w = getWidth();
		long duration = mDuration;

		long now = SystemClock.uptimeMillis();
		int drawWidth = (int) ((now - mStartTime) * w / (duration));

		mIndicatorDrawable.setBounds(0, 0, drawWidth, getHeight());
		mIndicatorDrawable.draw(canvas);

		if (drawWidth < w) {
			invalidate();
			return;
		}

		if (mListener != null) {
			mListener.onEnd();
		}
	}

	private Drawable mIndicatorDrawable;
	private long mDuration = DEFAULT_DURATION;

	public void setDuration(int millisecs) {
		if (mStarted) {
			throw new IllegalStateException(
					"can not set duration at a started state");
		}
		if (millisecs <= 0) {
			throw new IllegalArgumentException("millisecs can not be negative");
		}
		mDuration = millisecs;
	}

	private boolean mStarted;

	public void setIndicator(Drawable d) {
		mIndicatorDrawable = d;
	}

	public void setEventListener(EventListener l) {
		mListener = l;
	}

	private EventListener mListener;
	private long mStartTime;

	public void start() {
		mStartTime = SystemClock.uptimeMillis();
		mStarted = true;
		invalidate();
	}

	private boolean mCancelCalled;

	public void cancel() {
		if (mStarted) {
			mCancelCalled = true;
			mStarted = false;
			invalidate();
		}
	}
}
