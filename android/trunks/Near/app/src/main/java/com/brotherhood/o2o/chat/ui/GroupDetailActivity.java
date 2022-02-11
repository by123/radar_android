package com.brotherhood.o2o.chat.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.GroupUserBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBGroupService;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.model.IMGroupInfoBean;
import com.brotherhood.o2o.chat.ui.adapter.GroupDetailAvtarAdapter;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.request.GetGroupMembersRequest;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.dialog.BottomChooseDialog;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.ViewUtil;
import com.skynet.library.message.MessageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by laimo.li on 2015/12/29.
 */
public class GroupDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String GROUP_ID = "group_id";
    public static final int RESULT_DELETE_QUIT_GROUP = 1000;
    public static final String ACTION_SHOW_NAME = "ACTION_SHOW_NAME";
    public static final String ACTION_DELETE_TABLE = "ACTION_DELETE_TABLE";
    public static final String KEY_SHOW_USER_NAME = "KEY_SHOW_USER_NAME";

    public IMGroupInfoBean groupInfo;

    private GroupDetailAvtarAdapter adapter;

    @ViewInject(id = R.id.gvAvatar)
    private GridView gvAvatar;

    @ViewInject(id = R.id.tvGroupName)
    private TextView tvGroupName;

    @ViewInject(id = R.id.tvCreateTime)
    private TextView tvCreateTime;

    @ViewInject(id = R.id.tbSetToggleBtn)
    private ToggleButton tbSetToggleBtn;

    @ViewInject(id = R.id.llGroupName, clickMethod = "onClick")
    private LinearLayout llGroupName;

    @ViewInject(id = R.id.tvEmpty, clickMethod = "onClick")
    private TextView tvEmpty;

    @ViewInject(id = R.id.tvDeleteAndQuit, clickMethod = "onClick")
    private TextView tvDeleteAndQuit;

    private MsgReceiver mMsgReceiver;

    private BottomChooseDialog emptyialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_detail_layout;
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
    protected boolean showLoading() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        changeTitle(0);

        tbSetToggleBtn.setOnCheckedChangeListener(this);

        groupInfo = new IMGroupInfoBean();
        groupInfo.gid = getIntent().getStringExtra(GROUP_ID);

        adapter = new GroupDetailAvtarAdapter(this);
        gvAvatar.setAdapter(adapter);

        gvAvatar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupUserBean bean = (GroupUserBean) adapter.getItem(position);
                String myUid = AccountManager.getInstance().getUser().mUid;
                if (!myUid.equals(bean.getId()) && !myUid.equals(GroupDetailAvtarAdapter.KICKUID)) {
                    OtherUserDetailActivity.show(GroupDetailActivity.this, bean.getId(), true);
                }
            }
        });


        IMDBGroupService.queryGroupInfo(groupInfo.gid, new IMDBGroupService.DBListener() {
            @Override
            public void onResult(Object obj) {
                IMGroupInfoBean info = (IMGroupInfoBean) obj;
                if (info != null) {
                    groupInfo = info;
                    changeTitle(groupInfo.memberCount);
                    setGroupInfo();
                }
                getGroupMembers();
                getGroupInfo();
            }
        });

        mMsgReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDSIMManager.ACTION_IM_UPDATE_GROUP_NAME_MSG);
        registerReceiver(mMsgReceiver, filter);

    }

    private void changeTitle(int size) {
        getActionBarController().setBaseTitle(getResources().getString(R.string.group_chat_info, size), R.color.slide_menu_holo_black);
    }

    private void setGroupInfo() {
        tvGroupName.setText(groupInfo.name);
        tvCreateTime.setText(DateUtil.parseUnixTimeToString(groupInfo.createTime));
        tbSetToggleBtn.setChecked(groupInfo.showMemberName);
        tbSetToggleBtn.setClickable(true);

        if (groupInfo.creatorUid != 0) {
            if (groupInfo.isCreator()) {
                adapter.creatorKick();
            } else {
                ViewUtil.toggleView(tvDeleteAndQuit, true);
            }
            adapter.setCreatorUid(groupInfo.gid, String.valueOf(groupInfo.creatorUid));
        }


    }

    public static void show(Context context, String gid) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(GROUP_ID, gid);
        context.startActivity(intent);
    }

    public static void show(Activity context, String gid, int requestCode) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(GROUP_ID, gid);
        context.startActivityForResult(intent, requestCode);
    }

    public void getGroupInfo() {
        IDSIMManager.getInstance().queryGroupInfo(groupInfo.gid, new MessageManager.HttpCallBack() {


            @Override
            public void onSuc(Object o) {
                try {
                    JSONObject j = (JSONObject) o;
                    String name = j.getString("chnName");
                    long time = j.getLong("Time");
                    long creatorUid = j.getLong("Creator");

                    groupInfo.name = name;
                    groupInfo.createTime = time;
                    groupInfo.creatorUid = creatorUid;

                    IMDBGroupService.add(groupInfo);

                    setGroupInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(Object o) {

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
            case R.id.llGroupName:
                if (!TextUtils.isEmpty(groupInfo.name) && !TextUtils.isEmpty(groupInfo.gid)) {
                    ModifyGroupNameActivity.show(this, groupInfo.name, groupInfo.gid);
                }
                break;
            case R.id.tvDeleteAndQuit:
                deleteAndQuit();
                break;
            case R.id.tvEmpty:
                empty();
                break;

        }
    }

    private void empty() {
        if (TextUtils.isEmpty(groupInfo.gid)) {
            return;
        }
        if (emptyialog == null) {
            emptyialog = new BottomChooseDialog(this, BottomChooseDialog.DialogType.EMPTY_GROUP_CHAT);
            emptyialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dialogSecondLine://确定
                            IMDBGroupService.deleteGroupTable(groupInfo.gid);
                            IMDBLatestMsgService.deleteMsg(Long.valueOf(groupInfo.gid));

                            Intent intent = new Intent(ACTION_DELETE_TABLE);
                            sendBroadcast(intent);

                            ColorfulToast.green(GroupDetailActivity.this, getString(R.string.group_chat_empty_suc), 0);
                            break;
                    }
                    emptyialog.dismiss();
                }
            });
        }
        emptyialog.show();
    }

    private void deleteAndQuit() {
        IDSIMManager.getInstance().quitgroup(groupInfo.gid, new MessageManager.HttpCallBack() {

            @Override
            public void onSuc(Object o) {
                ColorfulToast.green(GroupDetailActivity.this, getString(R.string.group_delete_and_quit_suc), 0);
                setResult(RESULT_DELETE_QUIT_GROUP);
                finish();
            }

            @Override
            public void onFail(Object o) {
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        IMDBGroupService.showMemberName(groupInfo.gid, groupInfo.showMemberName = isChecked);
        Intent intent = new Intent(ACTION_SHOW_NAME);
        intent.putExtra(KEY_SHOW_USER_NAME, isChecked);
        sendBroadcast(intent);
    }

    public void getGroupMembers() {
        GetGroupMembersRequest request = GetGroupMembersRequest.createGroupMembersRequest(groupInfo.gid, new OnResponseListener<List<GroupUserBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<GroupUserBean> avatarBeans, boolean cache) {
                changeTitle(avatarBeans.size());
                adapter.addAll(avatarBeans);
                changeTitle(avatarBeans.size());
                ViewUtil.toggleView(gvAvatar, true);
                showContentView();
            }

            @Override
            public void onFailure(int code, String msg) {
                showContentView();
            }
        });
        request.sendRequest();
    }


    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra(IDSIMManager.KEY_GROUP_NAME);
            if (!TextUtils.isEmpty(name)) {
                tvGroupName.setText(name);
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
