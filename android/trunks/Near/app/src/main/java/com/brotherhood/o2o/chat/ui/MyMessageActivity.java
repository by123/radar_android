package com.brotherhood.o2o.chat.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;
import com.brotherhood.o2o.chat.ui.adapter.MyMessageAdapter;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.DividerDecoration;
import com.brotherhood.o2o.ui.widget.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/22.
 */
public class MyMessageActivity extends BaseActivity {

    private static final int MSGLIMIT = 15;

    private int currentMessagePage = 0;

    private IMLatestMsgDao imLatestMsgDao;

    private MyMessageAdapter adapter;

    private MsgReceiver mMsgReceiver;

    private LinkedList<IMLatestMsgBean> latestMsgs;

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_message_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        getActionBarController().setBaseTitle(R.string.my_message, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white).addIconItem(R.id.abRight, R.mipmap.ic_msg_add_normal);

        adapter = new MyMessageAdapter(this);
        rvList.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);
        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        rvList.addItemDecoration(headersDecor);
        rvList.addItemDecoration(new DividerDecoration(this));
        //rvList.setItemAnimator(new DefaultItemAnimator());

        registerReceiver();

    }


    @Override
    protected void onResume() {
        super.onResume();
        queryAllLatestMsg();
    }

    private void queryAllLatestMsg() {
        IMDBLatestMsgService.queryAllLatestMsg(MSGLIMIT, currentMessagePage, new IMDBLatestMsgService.DBListener() {
            @Override
            public void onResult(Object obj) {
                adapter.addAll((LinkedList) obj);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRight:
                AddFriendActivity.show(MyMessageActivity.this);
                break;
        }
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }


    private void registerReceiver() {
        mMsgReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG);
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG_MULTI);
        registerReceiver(mMsgReceiver, filter);
    }


    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            queryAllLatestMsg();
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
