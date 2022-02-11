package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.SearchUserBean;
import com.brotherhood.o2o.chat.ui.adapter.AddFriendsAdapter;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.request.SearchUserRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.AddFriendEditView;
import com.brotherhood.o2o.util.DisplayUtil;

import java.util.List;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class AddFriendActivity extends BaseActivity {

    @ViewInject(id = R.id.addFriendEdit)
    private AddFriendEditView addFriendEdit;

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    private AddFriendsAdapter adapter;

    private boolean isSearching;

    private String name;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_friend_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected void setViewForState() {
        super.setViewForState();
        mStateView.setViewForState(StateViewManager.getEmpty(this, R.string.put_user_name_search, R.mipmap.ic_msg_bigsearch_line_normal), MultiStateView.VIEW_STATE_LOADING);
    }


    @Override
    protected boolean showLoading() {
        return true;
    }

    private void init() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        adapter = new AddFriendsAdapter(this);
        rvList.setAdapter(adapter);

        addFriendEdit.setCallBack(new AddFriendEditView.CallBack() {
            @Override
            public void back() {
                finish();
            }

            @Override
            public void search(String nickname) {
                if (!isSearching) {
                    name = nickname;
                    isSearching = true;
                    adapter.clearAll();
                    senRequest(nickname);
                    DisplayUtil.hideKeyboard(AddFriendActivity.this);
                    mStateView.setViewForState(R.layout.loading_view, MultiStateView.VIEW_STATE_LOADING);
                    showLoadingView();
                }
            }
        });
    }

    private void senRequest(final String nickname) {
        SearchUserRequest request = SearchUserRequest.createSearchUserRequest(nickname, new OnResponseListener<List<SearchUserBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<SearchUserBean> searchUserBeans, boolean cache) {
                adapter.addAll(searchUserBeans);
                isSearching = false;
                if (searchUserBeans.isEmpty()) {
                    showEmptyView(nickname);
                } else {
                    showContentView();
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                showErrorView();
                isSearching = false;
            }
        });
        request.sendRequest();
    }


    public static void show(Context context) {
        Intent intent = new Intent(context, AddFriendActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.tvEmpty || viewId == R.id.tvRetry) {
            isSearching = true;
            adapter.clearAll();
            senRequest(name);
            DisplayUtil.hideKeyboard(AddFriendActivity.this);
            mStateView.setViewForState(R.layout.loading_view, MultiStateView.VIEW_STATE_LOADING);
            showLoadingView();
        }
    }

    protected void showEmptyView(String nickname) {
        mStateView.setViewForState(StateViewManager.getEmpty(AddFriendActivity.this, getResources().getString(R.string.not_find_friend, nickname), R.mipmap.ic_msg_bigsearch_line_normal), MultiStateView.VIEW_STATE_EMPTY);
        super.showEmptyView();
    }


    //protected void showLoadingView() {
    //
    //    super.showLoadingView();
    //}
}
