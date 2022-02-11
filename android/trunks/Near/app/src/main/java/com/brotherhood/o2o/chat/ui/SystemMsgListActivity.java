package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.db.service.IMDBSystemMsgService;
import com.brotherhood.o2o.chat.model.IMSystemMsgBean;
import com.brotherhood.o2o.chat.ui.adapter.SystemMsgListAdapter;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class SystemMsgListActivity extends BaseActivity {

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    private SystemMsgListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_msg_list_layout;
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

    private void init() {
        getActionBarController().setBaseTitle(R.string.system_msg, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white).addIconItem(R.id.abRight, R.mipmap.ic_msg_del_message_normal);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        adapter = new SystemMsgListAdapter(this,mStateView);
        rvList.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSystemMsg();
    }

    private void getSystemMsg() {
        IMDBSystemMsgService.queryMsg(new IMDBSystemMsgService.DBListener() {
            @Override
            public void onResult(Object obj) {
                adapter.addAll((LinkedList<IMSystemMsgBean>) obj);
            }
        });
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SystemMsgListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRight:
                IMDBSystemMsgService.deleteAllMsg();
                adapter.deleteAllMsg();
                break;
        }
    }

}
