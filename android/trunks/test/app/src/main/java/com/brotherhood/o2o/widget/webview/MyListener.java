package com.brotherhood.o2o.widget.webview;


public class MyListener implements PullToRefreshLayout.OnRefreshListener {

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}

}
