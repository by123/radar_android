package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.listener.observerview.ScrollCallBack;
import com.brotherhood.o2o.listener.observerview.Scrollable;
import com.brotherhood.o2o.listener.observerview.Scroller;

/**
 * ListView that its scroll position can be observed.
 */
public class ObservableListView extends ListView implements Scrollable {

	private int mPrevFirstVisiblePosition;
	private int mPrevFirstVisibleChildHeight = -1;
	private int mPrevScrolledChildrenHeight;
	private int mPrevScrollY;
	private int mScrollY;

	private int pos = 0;
	private int top = 0;
	private float lastHeaderTransY;

	private float headerHeight;

	private SparseIntArray mChildrenHeights;

	private ScrollCallBack callBack;
	private OnScrollListener mOriginalScrollListener;
	private OnScrollListener mScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOriginalScrollListener != null) {
				mOriginalScrollListener.onScrollStateChanged(view, scrollState);
			}

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mOriginalScrollListener != null) {
				mOriginalScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}

			onScrollChanged();
		}
	};

	public ObservableListView(Context context) {
		super(context);
		init();
	}

	public ObservableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollView);
		headerHeight = a.getDimensionPixelSize(R.styleable.ScrollView_headerHeight, 0);

		a.recycle();
		init();
	}

	public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollView);
		headerHeight = a.getDimensionPixelSize(R.styleable.ScrollView_headerHeight, 0);

		a.recycle();
		init();
	}

	public void setHeaderHeight(float headerHeight) {
		this.headerHeight = headerHeight;
	}

	
	@Override
	public void onRestoreInstanceState(Parcelable state) {

		SavedState ss = (SavedState) state;
		mPrevFirstVisiblePosition = ss.prevFirstVisiblePosition;
		mPrevFirstVisibleChildHeight = ss.prevFirstVisibleChildHeight;
		mPrevScrolledChildrenHeight = ss.prevScrolledChildrenHeight;
		mPrevScrollY = ss.prevScrollY;
		mScrollY = ss.scrollY;
		top = ss.top;
		pos = ss.pos;
		lastHeaderTransY = ss.lastHeaderTransY;
		mChildrenHeights = ss.childrenHeights;
		super.onRestoreInstanceState(ss.getSuperState());

	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.prevFirstVisiblePosition = mPrevFirstVisiblePosition;
		ss.prevFirstVisibleChildHeight = mPrevFirstVisibleChildHeight;
		ss.prevScrolledChildrenHeight = mPrevScrolledChildrenHeight;
		ss.prevScrollY = mPrevScrollY;
		ss.scrollY = mScrollY;
		ss.top = top;
		ss.pos = pos;
		ss.lastHeaderTransY = lastHeaderTransY;
		ss.childrenHeights = mChildrenHeights;
		return ss;
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOriginalScrollListener = l;

	}

	public void setScrollCallBack(ScrollCallBack callBack) {
		this.callBack = callBack;
	}

	private void setLastHeaderTransY(float lastHeaderTransY) {
		this.lastHeaderTransY = lastHeaderTransY;
	}

	public void create() {
		pos = getFirstVisiblePosition();
		View firstVisibleChild = getChildAt(0);
		if (firstVisibleChild != null) {
			top = firstVisibleChild.getTop();
		}
	}

	@Override
	public void scrollVerticallyTo(float transY) {

		float distance = transY - lastHeaderTransY;

		lastHeaderTransY = transY;
		this.top += distance;

		setSelection(this.pos);
		setSelectionFromTop(this.pos, this.top);

	}

	@Override
	public int getCurrentScrollY() {
		return mScrollY;
	}

	private void init() {
		mChildrenHeights = new SparseIntArray();

		super.setOnScrollListener(mScrollListener);
	}

	private void onScrollChanged() {
		if (getChildCount() > 0) {
			int firstVisiblePosition = getFirstVisiblePosition();
			for (int i = getFirstVisiblePosition(), j = 0; i <= getLastVisiblePosition(); i++, j++) {
				if (mChildrenHeights.indexOfKey(i) < 0 || getChildAt(j).getHeight() != mChildrenHeights.get(i)) {
					mChildrenHeights.put(i, getChildAt(j).getHeight());
				}
			}

			View firstVisibleChild = getChildAt(0);
			if (firstVisibleChild != null) {
				if (mPrevFirstVisiblePosition < firstVisiblePosition) {
					// 向下滚动
					int skippedChildrenHeight = 0;
					if (firstVisiblePosition - mPrevFirstVisiblePosition != 1) {
						for (int i = firstVisiblePosition - 1; i > mPrevFirstVisiblePosition; i--) {
							if (0 < mChildrenHeights.indexOfKey(i)) {
								skippedChildrenHeight += mChildrenHeights.get(i);
							} else {
								skippedChildrenHeight += firstVisibleChild.getHeight();
							}
						}
					}
					mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight + skippedChildrenHeight;
					mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
				} else if (firstVisiblePosition < mPrevFirstVisiblePosition) {
					// 向上滚动
					int skippedChildrenHeight = 0;
					if (mPrevFirstVisiblePosition - firstVisiblePosition != 1) {
						for (int i = mPrevFirstVisiblePosition - 1; i > firstVisiblePosition; i--) {
							if (0 < mChildrenHeights.indexOfKey(i)) {
								skippedChildrenHeight += mChildrenHeights.get(i);
							} else {
								skippedChildrenHeight += firstVisibleChild.getHeight();
							}
						}
					}
					mPrevScrolledChildrenHeight -= firstVisibleChild.getHeight() + skippedChildrenHeight;
					mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
				} else if (firstVisiblePosition == 0) {
					mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
				}
				if (mPrevFirstVisibleChildHeight < 0) {
					mPrevFirstVisibleChildHeight = 0;
				}
				mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.getTop();
				mPrevFirstVisiblePosition = firstVisiblePosition;

				mScrollY = mScrollY < 0 ? 0 : mScrollY;

				int distance = mScrollY - mPrevScrollY;
				if (distance != 0) {

					callBack.onScroll(distance);
				}

				mPrevScrollY = mScrollY;
			}
		}
	}

	static class SavedState extends BaseSavedState {
		int prevFirstVisiblePosition;
		int prevFirstVisibleChildHeight = -1;
		int prevScrolledChildrenHeight;
		int prevScrollY;
		int scrollY;

		int pos = 0;
		int top = 0;
		float lastHeaderTransY;

		SparseIntArray childrenHeights;

		private SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			prevFirstVisiblePosition = in.readInt();
			prevFirstVisibleChildHeight = in.readInt();
			prevScrolledChildrenHeight = in.readInt();
			prevScrollY = in.readInt();
			scrollY = in.readInt();
			pos = in.readInt();
			top = in.readInt();
			lastHeaderTransY = in.readFloat();
			childrenHeights = new SparseIntArray();
			final int numOfChildren = in.readInt();
			if (0 < numOfChildren) {
				for (int i = 0; i < numOfChildren; i++) {
					final int key = in.readInt();
					final int value = in.readInt();
					childrenHeights.put(key, value);
				}
			}

		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(prevFirstVisiblePosition);
			out.writeInt(prevFirstVisibleChildHeight);
			out.writeInt(prevScrolledChildrenHeight);
			out.writeInt(prevScrollY);
			out.writeInt(scrollY);
			out.writeInt(pos);
			out.writeInt(top);
			out.writeFloat(lastHeaderTransY);
			final int numOfChildren = childrenHeights == null ? 0 : childrenHeights.size();
			out.writeInt(numOfChildren);
			if (0 < numOfChildren) {
				for (int i = 0; i < numOfChildren; i++) {
					out.writeInt(childrenHeights.keyAt(i));
					out.writeInt(childrenHeights.valueAt(i));
				}
			}
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
		setLastHeaderTransY(lastHeaderY);
		create();
	}

	@Override
	public Scroller needTrans(float distance, float headerTransY) {

		if (this == null || !this.isShown()) {
			return null;
		}

		float transY = getCurrentScrollY();

		if (this.getFirstVisiblePosition() > 1) {

			if (distance > 0) {//外部head没有完全滚动消失 但是listview已经滚动很长距离 ， 继续向上滚动时外部head跟着滚动

				transY = distance - headerTransY;

			} else {

				return null;
			}

		}

		if (this.getFirstVisiblePosition() == 1) {
			if (this.getChildAt(0).getTop() < (headerHeight + headerTransY)) {
				//外部head没有完全滚动消失 但是listview已经滚动一段距离 ， 继续向上滚动时外部head跟着滚动
				if (distance > 0) {
					transY = distance - headerTransY;
				} else {
					return null;
				}

			}
		}

		if (this.getFirstVisiblePosition() == 0) {

			if (this.getChildAt(1) != null && this.getChildAt(1).getTop() < (headerHeight + headerTransY)) {
				//listview 已经滚动出来 但是listview 的head 和外部布局的head 滚动距离不一致，向下滚动时不滚动外部head

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

}
