package com.brotherhood.o2o.chat.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.MyGroupBean;
import com.brotherhood.o2o.bean.MyGroupListBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.ui.adapter.MyGroupListAdapter;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.skynet.library.message.MessageManager;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class MyGroupListActivity extends BaseActivity {

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    private MyGroupListAdapter adapter;

    private MsgReceiver mMsgReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_state_recycler_layout;
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected void setViewForState() {
        super.setViewForState();
        mStateView.setViewForState(StateViewManager.getEmpty(this, R.string.no_group, R.mipmap.ic_msg_creat_group_normal), MultiStateView.VIEW_STATE_EMPTY);

    }


    @Override
    protected boolean showLoading() {
        return true;
    }

    private void init() {

        getActionBarController().setBaseTitle(R.string.my_group_chat, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        adapter = new MyGroupListAdapter(this);
        rvList.setAdapter(adapter);

        getGroupList();
        registerReceiver();
    }

    private void getGroupList() {
        IDSIMManager.getInstance().getMyGroupList(new MessageManager.HttpCallBack() {
            @Override
            public void onSuc(Object o) {
                JSONObject j = (JSONObject) o;
                MyGroupListBean bean = JSON.parseObject(j.toString(), MyGroupListBean.class);
                List<MyGroupBean> myGroupList = bean.getMyCn();
                if (myGroupList.size() > 0) {
                    adapter.addAll(myGroupList);
                    showContentView();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFail(Object o) {
                showErrorView();
            }
        });
    }


    private void registerReceiver() {
        mMsgReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDSIMManager.ACTION_IM_UPDATE_GROUP_NAME_MSG);
        filter.addAction(IDSIMManager.ACTION_IM_QUIT_GROUP);
        registerReceiver(mMsgReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
        int viewId = v.getId();
        if (viewId == R.id.tvEmpty || viewId == R.id.tvRetry) {
            getGroupList();
        }
    }

    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(IDSIMManager.ACTION_IM_UPDATE_GROUP_NAME_MSG)) {
                String gid = intent.getStringExtra(IDSIMManager.KEY_SEND_UID);
                String name = intent.getStringExtra(IDSIMManager.KEY_GROUP_NAME);
                adapter.changeName(gid, name);

            } else if (action.equals(IDSIMManager.ACTION_IM_QUIT_GROUP)) {
                String gid = intent.getStringExtra(IDSIMManager.KEY_GROUP_ID);
                if (!TextUtils.isEmpty(gid)) {
                    adapter.remove(gid);
                }
            }


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMsgReceiver != null) {
            unregisterReceiver(mMsgReceiver);
        }
    }
}