package com.brotherhood.o2o.controller;

import android.app.Activity;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.listener.OnPullToRefreshListener;
import com.brotherhood.o2o.util.ViewUtil;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 下拉刷新控制器
 * Created with Android Studio
 * API详解 <a href="https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/blob/master/README-cn.md">android-Ultra-Pull-To-Refresh </a>
 */

public class PtrClassicController {
	private Builder mBuilder;

	private PtrClassicController(Builder builder) {
		mBuilder = builder;
	}

	public PtrClassicController refreshComplete() {
		mBuilder.refreshComplete();
		return this;
	}

	public PtrClassicController autoRefresh() {
		mBuilder.autoRefresh();
		return this;
	}


	public static class Builder{
		private PtrClassicFrameLayout mPtrClassicLayout;
		private float mResistance = 1.7f;
		private float mRatioOfHeaderHeightToRefresh = 1.2f;
		private int mDurationToClose = 200;
		private int mDurationToCloseHeader = 1000;
		private OnPullToRefreshListener mRefreshListener;

		public Builder(Activity activity) {
			mPtrClassicLayout = ViewUtil.findView(activity, R.id.flPtrClassic);
		}

		public Builder(View contentView) {
			mPtrClassicLayout = ViewUtil.findView(contentView, R.id.flPtrClassic);
		}


		//public Builder setResistance(float resistance) {
		//	mResistance = resistance;
		//	return this;
		//}
		//
		//public Builder setRatioOfHeaderHeightToRefresh(float ratio) {
		//	mRatioOfHeaderHeightToRefresh = ratio;
		//	return this;
		//}
		//
		//public Builder setDurationToClose(int duration) {
		//	mDurationToClose = duration;
		//	return this;
		//}
		//
		//public Builder setDurationToCloseHeader(int duration) {
		//	mDurationToCloseHeader = duration;
		//	return this;
		//}

		public Builder setOnPullToRefreshListener(OnPullToRefreshListener refreshListener) {
			mRefreshListener = refreshListener;
			return this;
		}

		public Builder refreshComplete(){
			mPtrClassicLayout.refreshComplete();
			return this;
		}

		public Builder autoRefresh(){
			mPtrClassicLayout.autoRefresh();
			return this;
		}

		private Builder build(){
			mPtrClassicLayout.setResistance(mResistance);
			mPtrClassicLayout.setRatioOfHeaderHeightToRefresh(mRatioOfHeaderHeightToRefresh);
			mPtrClassicLayout.setDurationToClose(mDurationToClose);
			mPtrClassicLayout.setDurationToCloseHeader(mDurationToCloseHeader);
			mPtrClassicLayout.setPullToRefresh(false);
			mPtrClassicLayout.setKeepHeaderWhenRefresh(true);

			mPtrClassicLayout.setPtrHandler(new PtrHandler() {
				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					if(mRefreshListener != null){
						mRefreshListener.onRefreshBegin(frame);
					}
				}

				@Override
				public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
					return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
				}
			});

			return this;
		}

		public PtrClassicController builder() {
			this.build();
			return new PtrClassicController(this);
		}

	}

}
