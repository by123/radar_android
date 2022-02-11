package com.brotherhood.o2o.listener;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 下拉刷新监听
 */
public interface OnPullToRefreshListener {
	void onRefreshBegin(PtrFrameLayout frame);
}