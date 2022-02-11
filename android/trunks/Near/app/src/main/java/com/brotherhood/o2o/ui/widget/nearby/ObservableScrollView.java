package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.listener.observerview.ScrollCallBack;
import com.brotherhood.o2o.listener.observerview.Scrollable;
import com.brotherhood.o2o.listener.observerview.Scroller;

/**
 * ScrollView that its scroll position can be observed.
 */
public class ObservableScrollView extends ScrollView implements Scrollable {

	private int mPrevScrollY;
	private int mScrollY;

	private boolean mFirstScroll;

	private ScrollCallBack callBack;

	private float lastHeaderTransY;

	private float headerHeight = 0;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollView);

		headerHeight = a.getDimensionPixelSize(R.styleable.ScrollView_headerHeight, 0);

		a.recycle();
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollView);

		headerHeight = a.getDimensionPixelSize(R.styleable.ScrollView_headerHeight, 0);

		a.recycle();
	}
	
	public void setHeaderHeight(float headerHeight) {
		this.headerHeight = headerHeight;
	}


	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		mPrevScrollY = ss.prevScrollY;
		mScrollY = ss.scrollY;
		super.onRestoreInstanceState(ss.getSuperState());
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.prevScrollY = mPrevScrollY;
		ss.scrollY = mScrollY;
		return ss;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (callBack != null) {
			mScrollY = t;

			if (mFirstScroll) {
				mFirstScroll = false;
			}

			mScrollY = mScrollY < 0 ? 0 : mScrollY;

			int distance = mScrollY - mPrevScrollY;
			if (distance != 0) {

				callBack.onScroll(distance);

			}
			mPrevScrollY = mScrollY;
		}
	}

	@Override
	public void scrollVerticallyTo(float transY) {

		float distance = transY - lastHeaderTransY;

		lastHeaderTransY = transY;

		scrollBy(0, (int) (0 - distance));
	}

	@Override
	public int getCurrentScrollY() {
		return mScrollY;
	}

	static class SavedState extends BaseSavedState {
		int prevScrollY;
		int scrollY;

		/**
		 * Called by onSaveInstanceState.
		 */
		SavedState(Parcelable superState) {
			super(superState);
		}

		/**
		 * Called by CREATOR.
		 */
		private SavedState(Parcel in) {
			super(in);
			prevScrollY = in.readInt();
			scrollY = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(prevScrollY);
			out.writeInt(scrollY);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	@Override
	public void setLastHeaderY(float lastHeaderY) {

		this.lastHeaderTransY = lastHeaderY;

	}

	@Override
	public Scroller needTrans(float distance, float headerTransY) {

		if (this == null || !this.isShown()) {
			return null;
		}

		float transY = getCurrentScrollY();

		if (transY > headerHeight) {

			if (distance > 0) {

				transY = distance - headerTransY;

			} else {

				return null;
			}
		}

		if (transY < headerHeight) {

			ViewGroup group = (ViewGroup) this.getChildAt(0);

			View first = group.getChildAt(1);

			if (first != null && transY + headerTransY >= 0) {

				if (distance <= 0) {

					return null;
				} else {
					transY = distance - headerTransY;
				}

			}
		}

		Scroller scroller = new Scroller();
		scroller.setTransY(transY);

		return scroller;
	}

	@Override
	public void setScrollCallBack(ScrollCallBack callBack) {
		this.callBack = callBack;
	}

}
