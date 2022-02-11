package com.brotherhood.o2o.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.chat.model.ChatListBean;
import com.brotherhood.o2o.chat.utils.ChatAPI;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.ui.widget.deletelistview.DeleteListView;
import com.brotherhood.o2o.ui.widget.deletelistview.SlideView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/7/9.
 */

public class ChaListActivity extends Activity implements SlideView.OnSlideListener {


    private ChatListAdapter mAdapter;

    @InjectView(R.id.listview)
    DeleteListView mListView;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    public static void show(Context context) {
        Intent intent = new Intent(context, ChaListActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chatlist);
        ButterKnife.inject(this);
        registerReceiver();
        initView();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ChatCompent.RECEIVE_NEW_MSG);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ChatCompent.RECEIVE_NEW_MSG)) {
                long mReceiverUid = intent.getLongExtra(ChatCompent.EXTRA_RECEIVER, -1);
                if (mReceiverUid != -1 && mAdapter != null) {
                    List<ChatListBean> datas = mAdapter.getDatas();
                    List<ChatListBean> temps = new ArrayList<>();
                    temps.addAll(datas);
                    ChatListBean mChatListBean = ChatCompent.shareCompent(ChaListActivity.this).queryChatList(mReceiverUid);
                    if (datas.size() > 0) {
                        for (ChatListBean data : temps) {
                            if (data.mId == mChatListBean.mId) {
                                if (TextUtils.isEmpty(data.mAvatarUrl)) {
                                    AccountComponent.shareComponent().saveUserAvatar(mReceiverUid);
                                }
                                mChatListBean = ChatCompent.shareCompent(ChaListActivity.this).queryChatList(mReceiverUid);
                                datas.remove(data);
                                datas.add(mChatListBean);
                            }
                        }
                    } else {
                        if (TextUtils.isEmpty(mChatListBean.mAvatarUrl)) {
                            AccountComponent.shareComponent().saveUserAvatar(mReceiverUid);
                        }
                        datas.add(mChatListBean);
                    }
                    mAdapter.updateDatas(datas);
                }
            }
        }
    };


    private void initView() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        final List<ChatListBean> datas = ChatCompent.shareCompent(ChaListActivity.this).queryChatList();
        ChatListBean systemdata = new ChatListBean();
        for (ChatListBean data : datas) {
            if (data.mTargetId == Constants.SYSTEM_ID) {
                systemdata = data;
            }
            ByLogout.out(data.mTargetId+"");
        }
        if (systemdata != null) {
            datas.remove(systemdata);
        }
        ByLogout.out("数据->" + datas.size());
        mAdapter = new ChatListAdapter(ChaListActivity.this, datas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                datas.get(i).mUnread = 0;
                mAdapter.updateDatas(datas);
                ChatListBean data = datas.get(i);
                ChatCompent.shareCompent(ChaListActivity.this).updateHasRead(data.mTargetId);
                ChatAPI.get(ChaListActivity.this).openSingleChatUI(ChaListActivity.this, data.mTargetId, data.mAvatarUrl, data.mNickName,data.mGender);
            }
        });
    }

    @Override
    public void onSlide(View view, int status) {

    }

}
