package com.brotherhood.o2o.chat.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.MyFriendBean;
import com.brotherhood.o2o.chat.db.service.IMDBNewFriendsService;
import com.brotherhood.o2o.chat.ui.adapter.MyFriendsAdapter;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetMyFriendsRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.DividerDecoration;
import com.brotherhood.o2o.ui.widget.MsgHintView;
import com.brotherhood.o2o.ui.widget.SideBar;
import com.brotherhood.o2o.ui.widget.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class MyFriendsActivity extends BaseActivity {

    @ViewInject(id = R.id.rvList)
    private RecyclerView rvList;

    @ViewInject(id = R.id.sidebar)
    private SideBar mSideBar;

    @ViewInject(id = R.id.abBack, clickMethod = "onClick")
    private ImageView abBack;

    @ViewInject(id = R.id.ryHasNewFriend, clickMethod = "onClick")
    private ImageView ryHasNewFriend;

    @ViewInject(id = R.id.msgHintView)
    private MsgHintView msgHintView;

    //@ViewInject(id = R.id.tvNoFriends)
    //private TextView tvNoFriends;

    private MyFriendsAdapter adapter;

    @Override
    protected boolean showLoading() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_friends_layout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        IMDBNewFriendsService.queryAllUnReadNum(new IMDBNewFriendsService.DBListener() {
            @Override
            public void onResult(Object obj) {
                msgHintView.hasMsg((long) obj);
            }
        });


    }

    private void init() {

        mStateView.setViewForState(StateViewManager.getEmpty(this, R.string.my_friend_empty, R.mipmap.ic_msg_no_friend_normal), MultiStateView.VIEW_STATE_EMPTY);

        adapter = new MyFriendsAdapter(this, mStateView);
        rvList.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);


        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        rvList.addItemDecoration(headersDecor);
        rvList.addItemDecoration(new DividerDecoration(this));


        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    rvList.scrollToPosition(position);
                }

            }
        });

        getFriends();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.ryHasNewFriend:
                NewFriendsActivity.show(MyFriendsActivity.this);
                break;
        }
        int viewId = v.getId();
        if (viewId == R.id.tvEmpty || viewId == R.id.tvRetry) {
            getFriends();
        }
    }

    @Override
    protected void attachAllMessage() {
        super.attachAllMessage();
        attachMessage(Message.Type.MSG_DELETE_MY_FRIEND);
    }

    @Override
    public void onReceiveMessage(Message message) {
        super.onReceiveMessage(message);
        if (message.type == Message.Type.MSG_DELETE_MY_FRIEND) {
            String uid = (String) message.data;
            if (!TextUtils.isEmpty(uid)) {
                adapter.remove(uid);
            }
        }
    }

    public void getFriends() {

        GetMyFriendsRequest request = GetMyFriendsRequest.createMyFriendsRequest(new OnResponseListener<List<MyFriendBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<MyFriendBean> myFriendBeans, boolean cache) {
                if (myFriendBeans.isEmpty()) {
                    showEmptyView();
                    //tvNoFriends.setVisibility(View.VISIBLE);
                } else {
                    //rvList.setVisibility(View.VISIBLE);
                    //mSideBar.setVisibility(View.VISIBLE);
                    showContentView();
                }

                AccountManager.getInstance().getUser().mProfile.mFriendTotal = myFriendBeans.size();
                NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_MY_FRIEND_UPDATA, null);

                //MyFriendBean bean = new MyFriendBean();
                //bean.setNickname("阿的");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("3的");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("腌的");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("吖房东");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("a");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("b");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("不到");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("不大");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("他我");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("企鹅");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("塌");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("54吧");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("阿斯顿");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("6y6");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("口语");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("儿童");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("utrecht");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("太热");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("i与");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("阿斯顿");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("倒萨");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("几个");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("阿斯顿");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("破碎");
                //myFriendBeans.add(bean);
                //
                //
                //bean = new MyFriendBean();
                //bean.setNickname("请问");
                //myFriendBeans.add(bean);
                //
                //bean = new MyFriendBean();
                //bean.setNickname("很过分");
                //myFriendBeans.add(bean);


                adapter.addAll(myFriendBeans);
            }

            @Override
            public void onFailure(int code, String msg) {
                showErrorView();
            }
        });
        request.sendRequest();
    }
}
