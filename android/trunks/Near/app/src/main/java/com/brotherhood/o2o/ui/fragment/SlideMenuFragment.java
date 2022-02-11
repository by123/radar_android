package com.brotherhood.o2o.ui.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.Profile;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.chat.ui.MyFriendsActivity;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.ui.activity.AboutUsActivity;
import com.brotherhood.o2o.ui.activity.MyCollectActivity;
import com.brotherhood.o2o.ui.activity.MyEventActivity;
import com.brotherhood.o2o.ui.activity.UserDetailActivity;
import com.brotherhood.o2o.ui.activity.VisitorListActivity;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.util.DisplayUtil;

/**
 * 侧边栏
 */
public class SlideMenuFragment extends BaseFragment {

    @ViewInject(id = R.id.tvSlideMenuName)
    private TextView mTvNickname;

    @ViewInject(id = R.id.tvSlideMenuVisitor)
    private TextView mTvVisitorCount;

    @ViewInject(id = R.id.tvSlideMenuFriend)
    private TextView mTvFriendCount;

    @ViewInject(id = R.id.ivSlideMenuUser, clickMethod = "userDetail")
    private ImageView mIvHead;

    // @ViewInject(id = R.id.rlSlideMenuActive, clickMethod = "myEvents")
    // private RelativeLayout mRlMyEvent;

    @ViewInject(id = R.id.rlSlideMenuCollect, clickMethod = "myCollectList")
    private RelativeLayout mRlMyCollect;

    @ViewInject(id = R.id.rlSlideMenuAbout, clickMethod = "aboutUs")
    private RelativeLayout mRlAboutUs;

    @ViewInject(id = R.id.llSlideMenuVisitor, clickMethod = "myVisitors")
    private LinearLayout mLlVisitor;

    @ViewInject(id = R.id.llSlideMenuFriend, clickMethod = "myFriends")
    private LinearLayout mLlFriends;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_slide_menu;
    }

    /**
     * 用户详情
     *
     * @param view
     */
    public void userDetail(View view) {
        UserDetailActivity.show(getActivity());
    }

    /**
     * 我的访客
     *
     * @param view
     */
    public void myVisitors(View view) {
        VisitorListActivity.show(getActivity());
    }

    /**
     * 我的好友
     *
     * @param view
     */
    public void myFriends(View view) {
        Intent intent = new Intent(getActivity(), MyFriendsActivity.class);
        startActivity(intent);
    }

    /**
     * 我的活动
     *
     * @param view
     */
    public void myEvents(View view) {
        MyEventActivity.show(getActivity());
        Intent it = new Intent();
    }

    /**
     * 我的收藏
     *
     * @param view
     */
    public void myCollectList(View view) {
        MyCollectActivity.show(getContext());
    }

    /**
     * 关于我们
     *
     * @param view
     */
    public void aboutUs(View view) {
        AboutUsActivity.show(getContext());
    }

    @Override
    public void loadData() {
        // initData();
    }

    private void initData() {
        UserInfo userInfo = AccountManager.getInstance().getUser();
        if (userInfo == null) {
            LogManager.e("=========slide menu userinfo is null==========");
            return;
        }
        String headUrl = userInfo.mIcon;
        LogManager.e("=========headUrl==========:" + headUrl);
        if (!TextUtils.isEmpty(headUrl)) {
            ImageLoaderManager.displayCircleBorderImageByUrl(getActivity(), mIvHead, headUrl, DisplayUtil.dp2px((float) 2.5), R.mipmap.ic_msg_default);

        }
        if (!TextUtils.isEmpty(userInfo.mNickName)) {
            mTvNickname.setText(userInfo.mNickName);
        }
        Profile profile = userInfo.mProfile;
        if (profile != null) {
            mTvVisitorCount.setText(String.valueOf(profile.mVisitTotal));
            mTvFriendCount.setText(String.valueOf(profile.mFriendTotal));
        }

    }

    @Override
    protected void attachAllMessage() {
        super.attachAllMessage();
        attachMessage(Message.Type.USER_LOGIN_SUCCESS);
        attachMessage(Message.Type.USER_LOGIN_FAILED);
        attachMessage(Message.Type.USER_DATA_CHANGE);
        attachMessage(Message.Type.USER_LOGOUT_SUCCESS);
        //attachMessage(Message.Type.MSG_DELETE_MY_FRIEND);
        //attachMessage(Message.Type.MSG_ADD_MY_FRIEND);
        attachMessage(Message.Type.MSG_MY_FRIEND_UPDATA);

        attachMessage(Message.Type.MSG_VISITOR_TOTAL);
    }


    @Override
    public void onReceiveMessage(Message message) {
        super.onReceiveMessage(message);
        UserInfo user = AccountManager.getInstance().getUser();
        switch (message.type) {
            case USER_LOGIN_SUCCESS://登录成功
            case USER_DATA_CHANGE://用户数据发生变化
                //initData();
                break;
            case USER_LOGIN_FAILED://登录失败

                break;
            case USER_LOGOUT_SUCCESS://注销

                break;
            case MSG_MY_FRIEND_UPDATA://好友更新
                ((TextView) findView(R.id.tvSlideMenuFriend)).setText(String.valueOf(AccountManager.getInstance().getUser().mProfile.mFriendTotal));
                AccountManager.getInstance().updateUser(user);
                break;
            //case MSG_DELETE_MY_FRIEND://删除好友
            //    Profile profileDelete = AccountManager.getInstance().getUser().mProfile;
            //    profileDelete.mFriendTotal--;
            //    ((TextView) findView(R.id.tvSlideMenuFriend)).setText(String.valueOf(profileDelete.mFriendTotal));
            //    break;
            //case MSG_ADD_MY_FRIEND://加好友
            //    Profile profileAdd = AccountManager.getInstance().getUser().mProfile;
            //    profileAdd.mFriendTotal++;
            //    ((TextView) findView(R.id.tvSlideMenuFriend)).setText(String.valueOf(profileAdd.mFriendTotal));
            //    break;
            case MSG_VISITOR_TOTAL:
                ((TextView) findView(R.id.tvSlideMenuVisitor)).setText(String.valueOf(AccountManager.getInstance().getUser().mProfile.mVisitTotal));
                AccountManager.getInstance().updateUser(user);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected boolean isTraceFragment() {
        return false;
    }
}
