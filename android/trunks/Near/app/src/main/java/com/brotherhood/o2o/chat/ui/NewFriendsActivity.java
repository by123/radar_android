package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IMContants;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.db.service.IMDBNewFriendsService;
import com.brotherhood.o2o.chat.model.IMApplyInfoBean;
import com.brotherhood.o2o.chat.ui.adapter.NewFriendsAdapter;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class NewFriendsActivity extends BaseActivity {

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    private NewFriendsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_friends_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected boolean showLoading() {
        return true;
    }

    private void init() {

        getActionBarController().setBaseTitle(R.string.new_friend, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white);

        mStateView.setViewForState(StateViewManager.getEmpty(this, R.string.new_friend_empty, R.mipmap.ic_msg_bell_normal), MultiStateView.VIEW_STATE_EMPTY);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        adapter = new NewFriendsAdapter(this,mStateView);
        rvList.setAdapter(adapter);

        IMDBNewFriendsService.queryAllApplyInfo(new IMDBNewFriendsService.DBListener() {
            @Override
            public void onResult(Object obj) {

                LinkedList<IMApplyInfoBean> list = (LinkedList<IMApplyInfoBean>) obj;
                adapter.addAll(list);
                if (list.size() > 0) {
                    showContentView();
                }else{
                    showEmptyView();
                }

            }
        });
        IMDBNewFriendsService.updateAllToHasRead();
        IMDBLatestMsgService.updateLatestMsgToHasRead(IMContants.ACK_ID);
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
    }


    public static void show(Context context) {
        Intent intent = new Intent(context, NewFriendsActivity.class);
        context.startActivity(intent);
    }

}
