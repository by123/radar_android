package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.db.service.IMDBSystemMsgService;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class SystemMsgDetailActivity extends BaseActivity {

    public static final String SYSTEM_MSG_TITLE = "system_msg_title";
    public static final String SYSTEM_MSG_CONTENT = "system_msg_content";
    public static final String SYSTEM_MSG_ID= "system_msg_id";

    @ViewInject(id = R.id.tvSystemMsgTitle)
    private TextView tvSystemMsgTitle;

    @ViewInject(id = R.id.tvSystemMsgContent)
    private TextView tvSystemMsgContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_msg_detail_layout;
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
        getActionBarController().setBaseTitle(R.string.system_msg, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white);
        String title = getIntent().getStringExtra(SYSTEM_MSG_TITLE);
        tvSystemMsgTitle.setText(title);
        String context = getIntent().getStringExtra(SYSTEM_MSG_CONTENT);
        tvSystemMsgContent.setText(context);

        long msgId = getIntent().getLongExtra(SYSTEM_MSG_ID, -1);
        IMDBSystemMsgService.updateMsgToRead(msgId);
    }


    public static void show(Context context,long msgId, String title, String contenxt) {
        Intent intent = new Intent(context, SystemMsgDetailActivity.class);
        intent.putExtra(SYSTEM_MSG_ID, msgId);
        intent.putExtra(SYSTEM_MSG_TITLE, title);
        intent.putExtra(SYSTEM_MSG_CONTENT, contenxt);
        context.startActivity(intent);
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
}
