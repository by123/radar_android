package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.brotherhood.o2o.lib.recyclerScrollListener.OnRecylerViewScrollImpl;
import com.brotherhood.o2o.task.TaskExecutor;

/**
 * Created with Android Studio
 */

public class BaseRecyclerView extends RecyclerView implements OnRecylerViewScrollImpl.OnScrollCallback {
	private OnRecylerViewScrollImpl mScrollListener;
	private OnRecylerViewScrollImpl.OnScrollCallback mOnScrollCallback;
	private boolean mHasScrollListener;

	public BaseRecyclerView(Context context) {
		this(context, null, 0);

	}

	public BaseRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BaseRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setHasFixedSize(true);
		setLayoutManager(new LinearLayoutManager(getContext()));
	}

	public BaseRecyclerView attachOnScrollCallback(OnRecylerViewScrollImpl.OnScrollCallback onScrollCallback){
		mOnScrollCallback = onScrollCallback;
		if (mScrollListener == null) {
			mScrollListener = new OnRecylerViewScrollImpl();
		}else{
			removeOnScrollListener(mScrollListener);
		}
		addOnScrollListener(mScrollListener);
		mScrollListener.setOnScrollCallback(this);
		mHasScrollListener = true;
		return this;

	}


	@Override
	protected void onDetachedFromWindow() {
		if (mHasScrollListener) {
			this.removeOnScrollListener(mScrollListener);
			mScrollListener = null;
		}
		super.onDetachedFromWindow();
	}

	@Override
	public void onScrollBottom(final RecyclerView recyclerView, final int newState) {
		if (mScrollListener != null) {
			TaskExecutor.scheduleTaskOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mOnScrollCallback != null) {
						mOnScrollCallback.onScrollBottom(recyclerView, newState);
					}
				}
			}, 200);//延迟200是为了底部Loading多转

		}
	}

	@Override
	public void onlastVisibleItemPosition(int position) {
		if (mScrollListener != null) {
			mOnScrollCallback.onlastVisibleItemPosition(position);
		}
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		if (mOnScrollCallback != null) {
			mOnScrollCallback.onScrolled(recyclerView, dx, dy);
		}
	}
}
