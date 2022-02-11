package com.brotherhood.o2o.ui.fragment.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.PtrClassicController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.lib.recyclerScrollListener.OnRecylerViewScrollImpl;
import com.brotherhood.o2o.listener.OnPullToRefreshListener;
import com.brotherhood.o2o.ui.adapter.LoadMoreRecylerAdatper;
import com.brotherhood.o2o.ui.widget.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 下拉刷新Fragment，封装了下拉刷新事件，以及刷新后页面展示逻辑(支持分页)
 * T为数据实体， A为Adatper
 */

public abstract class PullToRefreshFragment<T, A extends RecyclerView.Adapter> extends BaseFragment implements OnPullToRefreshListener,
        OnRecylerViewScrollImpl.OnScrollCallback, View.OnClickListener {
    public static final int FIRST_PAGE_INDEX = 1;

    @ViewInject(id = R.id.rvBaseRecycler)
    protected BaseRecyclerView mRecyclerView;

    @ViewInject(id = R.id.stateView)
    protected MultiStateView mStateView;

    protected PtrClassicController mPtrClassicController;

    protected List<T> mListData;
    protected int mCurrentPageIndex = FIRST_PAGE_INDEX;

    private boolean mIsPageLoad; //是否分页加载
    private boolean mHasNextPage = true;//是否可以继续分页加载
    protected boolean mIsLoading;//是否正在加载


    @Override
    protected int getLayoutId() {
        return R.layout.pull_refresh_recycler_page;
    }

    @Override
    public void loadData() {
        mListData = new ArrayList<>();
        if (pullToRefresh()) {
            mPtrClassicController = new PtrClassicController.Builder(mView).setOnPullToRefreshListener(this).builder();
        }
        mRecyclerView.setAdapter(createAdapter());
        init();
        loadData(true);
    }

    protected boolean pullToRefresh() {
        return true;
    }

    /**
     * 在子Fragment请求数据之前做一些初始化的操作
     */
    protected void init() {

    }


    protected abstract A createAdapter();


    protected void loadFailure() {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        showErrorView();
        mIsLoading = false;
    }

    /**
     * 加载完成（不需要分页加载)
     */
    protected void loadSuccess(List<T> datas) {
        mIsPageLoad = false;
        mPtrClassicController.refreshComplete();

        if (datas == null) {//加载失败
            showErrorView();
            return;
        }

        if (datas.size() == 0) {//没有数据
            showEmptyView();
        } else {
            showContentView();
            mListData.clear();
            mListData.addAll(datas);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 分页加载完成
     */
    protected void loadSuccessWithPage(int mulTiViewState, boolean hasNext, List<T> listData) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        mIsPageLoad = true;

        if (mCurrentPageIndex == FIRST_PAGE_INDEX) {//加载第一页
            mPtrClassicController.refreshComplete();
            switch (mulTiViewState) {
                case MultiStateView.VIEW_STATE_ERROR://加载失败
                    showErrorView();
                    break;
                case MultiStateView.VIEW_STATE_EMPTY://没有数据
                    showEmptyView();
                    break;
                case MultiStateView.VIEW_STATE_CONTENT://第一页加载成功
                    showContentView();
                    mListData.clear();
                    mListData.addAll(listData);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    if (mRecyclerView.getAdapter() instanceof LoadMoreRecylerAdatper) {
                        if (hasNext) {
                            ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).addFooter(LoadMoreRecylerAdatper.LOAD_ING);
                        } else {
                            ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).addFooter(LoadMoreRecylerAdatper.LOAD_FINISH);
                        }
                    }
                    mHasNextPage = hasNext;
                    updateRequestParams();
                    break;
            }
        } else { //分页加载
            switch (mulTiViewState) {
                case MultiStateView.VIEW_STATE_ERROR://底部分页加载失败
                    if (mRecyclerView.getAdapter() instanceof LoadMoreRecylerAdatper) {
                        ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).setOnFooterClickListener(this);
                        ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).loadFailure();
                    }
                    break;
                case MultiStateView.VIEW_STATE_EMPTY://没有数据
                    if (mRecyclerView.getAdapter() instanceof LoadMoreRecylerAdatper) {
                        ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).loadEnd();
                    }
                    break;
                case MultiStateView.VIEW_STATE_CONTENT://分页加载成功
                    showContentView();
                    if (mRecyclerView.getAdapter() instanceof LoadMoreRecylerAdatper) {
                        ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).loadSuccess(listData, hasNext);
                        updateRequestParams();
                    }
                    mHasNextPage = hasNext;
                    break;
            }
        }
        mIsLoading=false;
    }

    /**
     * 更新请求参数
     */
    protected abstract void updateRequestParams();

    /**
     * 初始化请求参数
     */
    protected abstract void initRequestParams();
    /**
     * 显示错误View并初始化控件
     */
    protected void showErrorView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        View errorView = mStateView.getView(MultiStateView.VIEW_STATE_ERROR);
        if (errorView != null && errorView.findViewById(R.id.tvRetry) != null) {
            errorView.findViewById(R.id.tvRetry).setOnClickListener(this);
        }
    }

    /**
     * 显示空View并初始化控件
     */
    protected void showEmptyView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        View emptyView = mStateView.getView(MultiStateView.VIEW_STATE_EMPTY);
        if (emptyView != null && emptyView.findViewById(R.id.tvEmpty) != null) {
            emptyView.findViewById(R.id.tvEmpty).setOnClickListener(this);
        }
    }

    protected void showContentView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    protected void showLoadingView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    /**
     * 开始下拉刷新
     *
     * @param frame
     */
    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        initRequestParams();
        loadData(false);
    }

    protected void loadData(boolean showLoading) {
        mIsLoading = true;
        if (showLoading)
            showLoadingView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvEmpty) {
            loadData(true);
        } else if (id == R.id.tvRetry) {
            loadData(true);
        } else if (id == R.id.tvListLoadMore && ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).getLoadStuts() == LoadMoreRecylerAdatper.LOAD_ERROR) {
            ((LoadMoreRecylerAdatper) mRecyclerView.getAdapter()).setLoadStuts(LoadMoreRecylerAdatper.LOAD_ING);
            loadData(false);
        }
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    @Override
    public void onScrollBottom(RecyclerView recyclerView, int newState) {
        if (!mIsLoading && mHasNextPage) {
            loadData(false);
        }
    }

    @Override
    public void onlastVisibleItemPosition(int position) {

    }

    public List<T> getListData() {
        return mListData;
    }

    @Override
    public void onDestroy() {
        if (mListData != null) {
            mListData.clear();
            mListData = null;
        }
        super.onDestroy();
    }
}
