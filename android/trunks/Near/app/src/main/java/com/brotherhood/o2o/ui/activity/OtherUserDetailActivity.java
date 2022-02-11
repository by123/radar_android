package com.brotherhood.o2o.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.CollectionBean;
import com.brotherhood.o2o.bean.account.CollectionWrapper;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.account.WrapperUserInfo;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.helper.ChatSenderHelper;
import com.brotherhood.o2o.chat.ui.AddFriendVerifyActivity;
import com.brotherhood.o2o.chat.ui.ChatActivity;
import com.brotherhood.o2o.chat.ui.ImgPreviewActivity;
import com.brotherhood.o2o.chat.ui.fragment.ImageDownloadFragment;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.BaseAdapterHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.QuickAdapter;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetOtherCollectionRequest;
import com.brotherhood.o2o.request.GetUserInfoRequest;
import com.brotherhood.o2o.request.LikeUserRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.CommonHorizontalDecoration;
import com.brotherhood.o2o.ui.widget.dialog.BottomChooseDialog;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.DistanceFormatUtil;
import com.brotherhood.o2o.util.ViewUtil;
import com.skynet.library.message.MessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 他人详情
 */
public class OtherUserDetailActivity extends BaseActivity {

    public static final int RESULT_DELETE_MY_FRIEND = 1000;

    @ViewInject(id = R.id.ivOtherDetailIcon, clickMethod = "preViewHead")
    private ImageView mIvIcon;

    @ViewInject(id = R.id.tvOtherDetailName)
    private TextView mTvNickname;

    @ViewInject(id = R.id.tvOtherDetailSignature)
    private TextView mTvSignature;

    @ViewInject(id = R.id.tvOtherDetailAge)
    private TextView mTvAge;

    @ViewInject(id = R.id.tvOtherDetailDistance)
    private TextView mTvDistance;

    @ViewInject(id = R.id.vsOtherDetailLocation)
    private ViewStub mVsLocation;

    @ViewInject(id = R.id.llOtherDetailLike, clickMethod = "pressLike")
    private LinearLayout mLlLike;

    @ViewInject(id = R.id.tvOtherDetailLike)
    private TextView mTvLike;

    @ViewInject(id = R.id.ivOtherDetailLike)
    private ImageView mIvLike;

    @ViewInject(id = R.id.rlOtherDetailHead)
    private RelativeLayout mRlHead;

    private LinearLayout mLlLocation;

    @ViewInject(id = R.id.llOtherDetailChat, clickMethod = "chatWithUser")
    private LinearLayout mLlChat;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @ViewInject(id = R.id.llOtherDetailAddFriend, clickMethod = "addFriend")
    private LinearLayout mLlAddFriend;

    private QuickAdapter<CollectionBean> mFoodAdapter;

    private TextView mTvEatTitle;
    private RecyclerView mRecyclerView;

    @ViewInject(id = R.id.vsOtherCollectFood)
    private ViewStub mVsCollectFood;

    @ViewInject(id = R.id.llOtherNoCollect)
    private LinearLayout mLlNoCollect;

    private RelativeLayout mRlLocation;//
    private TextView mTvLocation;//
    private RelativeLayout mRlStar;//
    private TextView mTvStar;//
    private TextView mTvLocationTitle;//
    private String mOhterUid;
    private boolean mShowChatBtn;

    private GetUserInfoRequest mUserInfoRequest;
    private LikeUserRequest mLikeUserRequest;
    private GetOtherCollectionRequest mCollectFoodRequest;//想吃
    private GetOtherCollectionRequest mCollectJoyRequest;//后期待接入  想玩
    private GetOtherCollectionRequest mCollectMovieRequest;//后期待接入  想看
    private BottomChooseDialog mDialog;
    private UserInfo mOtherInfo;
    private List<CollectionBean> mFoodList = new ArrayList<>();

    public static void show(Context context, String uid) {
        Intent it = new Intent(context, OtherUserDetailActivity.class);
        it.putExtra(BundleKey.OTHER_USER_DETAIL_KEY, uid);
        context.startActivity(it);
    }

    public static void show(Context context, String uid, boolean showChatBtn) {
        Intent it = new Intent(context, OtherUserDetailActivity.class);
        it.putExtra(BundleKey.OTHER_USER_DETAIL_KEY, uid);
        it.putExtra(BundleKey.OTHER_USER_DETAIL_KEY_SHOW_CHAT, showChatBtn);
        context.startActivity(it);
    }

    public static void show(Activity context, String uid, boolean showChatBtn, int requestCode) {
        Intent it = new Intent(context, OtherUserDetailActivity.class);
        it.putExtra(BundleKey.OTHER_USER_DETAIL_KEY, uid);
        it.putExtra(BundleKey.OTHER_USER_DETAIL_KEY_SHOW_CHAT, showChatBtn);
        context.startActivityForResult(it, requestCode);
    }


    public void preViewHead(View view) {
        if (mOtherInfo == null) {
            return;
        }
        //头像预览
        if (!TextUtils.isEmpty(mOtherInfo.mIcon)) {
            Intent intent = new Intent(OtherUserDetailActivity.this, ImgPreviewActivity.class);
            intent.putExtra(ImgPreviewActivity.FILE_PATH, mOtherInfo.mIcon);
            intent.putExtra(ImgPreviewActivity.PREVIEW_TYPE, ImageDownloadFragment.FILE_URL_TYPE);
            startActivity(intent);
            overridePendingTransition(R.anim.scale_out, 0);
        }
    }

    @Override
    protected boolean showLoading() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_other_user_detail_layout;
    }

    @Override
    protected boolean addOverlayActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    /**
     * 点赞
     *
     * @param view
     */
    public void pressLike(View view) {
        if (mOtherInfo == null || mOtherInfo.mIsLike == 1) {
            ColorfulToast.orange(OtherUserDetailActivity.this, getString(R.string.other_detail_has_liked), Toast.LENGTH_SHORT);
            return;
        }
        if (mLikeUserRequest == null) {
            mLikeUserRequest = LikeUserRequest.createLikeRequest(mOhterUid, new OnResponseListener<String>() {
                @Override
                public void onSuccess(int code, String msg, String s, boolean cache) {
                    ColorfulToast.green(OtherUserDetailActivity.this, getString(R.string.like_success), Toast.LENGTH_SHORT);
                    mIvLike.setImageResource(R.mipmap.liked);
                    mOtherInfo.mIsLike = 1;
                    if (mOtherInfo != null) {
                        mTvLike.setText(getString(R.string.other_detail_like, mOtherInfo.mLikeCount + 1));
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    ColorfulToast.orange(OtherUserDetailActivity.this, getString(R.string.like_failed), Toast.LENGTH_SHORT);
                }
            });
        }
        mLikeUserRequest.sendRequest();
    }

    /**
     * 聊天
     *
     * @param view
     */
    public void chatWithUser(View view) {
        ChatActivity.show(this, mOtherInfo.mUid, mOtherInfo.mNickName,
                mOtherInfo.mIcon, ChatSenderHelper.ChatMode.MODE_PRIVATE);
    }

    /**
     * 添加好友
     *
     * @param view
     */
    public void addFriend(View view) {
        AddFriendVerifyActivity.show(this, Long.valueOf(mOhterUid));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_white)
                .setDivideColor(R.color.white)
                .setBaseTitle(R.string.user_detail_title, R.color.white)
                .addIconItem(R.id.abRightImage, R.mipmap.other_user_detail_operate)
                .hideHorizontalDivide();
        mOhterUid = getIntent().getStringExtra(BundleKey.OTHER_USER_DETAIL_KEY);
        mShowChatBtn = getIntent().getBooleanExtra(BundleKey.OTHER_USER_DETAIL_KEY_SHOW_CHAT, true);
        mLlChat.setVisibility(mShowChatBtn ? View.VISIBLE : View.GONE);
        getUserInfo();
        getCollectFoodInfo();
    }

    private int headHeight;

    private void updateEmptyView() {
        if (mLlNoCollect.getVisibility() != View.VISIBLE){
            return;
        }
        headHeight = 0;
        mRlHead.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRlHead.getViewTreeObserver().removeOnPreDrawListener(this);
                headHeight = mRlHead.getMeasuredHeight();
                computeHeight();
                return true;
            }
        });

        if(mLlLocation != null){
            mLlLocation.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mLlLocation.getViewTreeObserver().removeOnPreDrawListener(this);
                    headHeight += mLlLocation.getMeasuredHeight();
                    computeHeight();
                    return true;
                }
            });
        }
    }

    private void computeHeight(){
        int height = DisplayUtil.getScreenHeight(OtherUserDetailActivity.this) - DisplayUtil.getBarHeight
                (OtherUserDetailActivity.this) - DisplayUtil.dp2px(48) - DisplayUtil.dp2px(10) - headHeight;

        if (height < DisplayUtil.dp2px(100)){
            height = DisplayUtil.dp2px(100);
        }
        ViewGroup.LayoutParams params = mLlNoCollect.getLayoutParams();
        params.height = height;
        if (mLlNoCollect != null){
            mLlNoCollect.setLayoutParams(params);
            mLlNoCollect.requestLayout();
        }
    }

    /**
     * 获取收藏的食物
     */
    private void getCollectFoodInfo() {
        if (mCollectFoodRequest == null) {
            mCollectFoodRequest = getOtherCollectRequest(2, new OnResponseListener<CollectionWrapper>() {
                @Override
                public void onSuccess(final int code, String msg, CollectionWrapper collectionWrapper, boolean cache) {
                    if (collectionWrapper == null || collectionWrapper.mCount <= 0) {
                        mLlNoCollect.setVisibility(View.VISIBLE);
                        updateEmptyView();
                        return;
                    }
                    for (CollectionBean bean : collectionWrapper.mCollectList) {//过滤非forcesqure数据
                        if (bean.mPlatform == 3) {
                            mFoodList.add(bean);
                        }
                    }
                    if (mFoodList.isEmpty()) {
                        mLlNoCollect.setVisibility(View.VISIBLE);
                        updateEmptyView();
                        return;
                    }
                    mVsCollectFood.inflate();
                    mTvEatTitle = (TextView) findViewById(R.id.tvOtherDetailEat);
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvOtherDetailEat);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(OtherUserDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    mRecyclerView.addItemDecoration(new CommonHorizontalDecoration(20, 5, 0, 5, 0));
                    mTvEatTitle.setText(getString(R.string.other_user_detail_eat, collectionWrapper.mCount));

                    mFoodAdapter = new QuickAdapter<CollectionBean>(OtherUserDetailActivity.this, R.layout.collect_food_item) {
                        @Override
                        protected void onBindViewHolder(BaseAdapterHelper helper, final CollectionBean collectionBean, int position) {
                            helper.displayRoundImageByUrl(R.id.ivCollectIcon, collectionBean.mCollectPhoto, R.mipmap.img_default, 5);
                            helper.setText(R.id.tvCollectName, collectionBean.mCollectName);
                            helper.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(collectionBean.mCollectId)) {
                                        return;
                                    }
                                    OverseaFoodDetailActivity.show(OtherUserDetailActivity.this, collectionBean.mCollectId);
                                }
                            });
                        }
                    };
                    mRecyclerView.setAdapter(mFoodAdapter);
                    mFoodAdapter.addAll(mFoodList);
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        }
        if (mCollectFoodRequest == null) {
            return;
        }
        mCollectFoodRequest.sendRequest();
    }

    private void getUserInfo() {
        if (TextUtils.isEmpty(mOhterUid)) {
            return;
        }
        if (mUserInfoRequest == null) {
            mUserInfoRequest = GetUserInfoRequest.createGetUserInfoRequest(mOhterUid, new OnResponseListener<WrapperUserInfo>() {
                @Override
                public void onSuccess(int code, String msg, WrapperUserInfo wrapperUserInfo, boolean cache) {
                    UserInfo userInfo = wrapperUserInfo.mOtherInfo;
                    if (userInfo == null) {
                        LogManager.e("=========other detail userinfo is null==========");
                        showErrorView();
                        return;
                    }
                    mOtherInfo = userInfo;
                    initUserInfo(userInfo);
                    showContentView();
                }

                @Override
                public void onFailure(int code, String msg) {
                    showErrorView();
                    ColorfulToast.orange(OtherUserDetailActivity.this, msg, Toast.LENGTH_SHORT);
                }
            });
        }
        mUserInfoRequest.sendRequest();
    }

    /**
     * 初始化他人用户信息
     *
     * @param userInfo
     */
    private void initUserInfo(UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.mIcon)) {
            ImageLoaderManager.displayCircleBorderImageByUrl(OtherUserDetailActivity.this, mIvIcon, userInfo.mIcon, DisplayUtil.dp2px(2), R.mipmap
                    .ic_msg_default);
        }
        if (!TextUtils.isEmpty(userInfo.mResidence) || !TextUtils.isEmpty(userInfo.mBirthday)) {
            mVsLocation.inflate();
            mLlLocation = (LinearLayout) findViewById(R.id.llOtherDetailInfoRoot);
            if (!TextUtils.isEmpty(userInfo.mResidence)) {
                mTvLocationTitle = (TextView) findViewById(R.id.tvDetailLocationTitle);
                mTvLocationTitle.setText(R.string.other_detail_location_title);
                mRlLocation = (RelativeLayout) findViewById(R.id.rlUserDetailLocation);
                mTvLocation = (TextView) findViewById(R.id.tvUserDetailLocation);
                mRlLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(userInfo.mResidence);
            }
            if (!TextUtils.isEmpty(userInfo.mBirthday)) {
                mRlStar = (RelativeLayout) findViewById(R.id.rlUserDetailStar);
                mTvStar = (TextView) findViewById(R.id.tvUserDetailStar);
                mRlStar.setVisibility(View.VISIBLE);
                String star = DateUtil.parseConstellation(Long.valueOf(userInfo.mBirthday));
                mTvStar.setText(star);
            }
        }

        int gender = userInfo.mGenger;
        if (gender == 0) {
            mTvAge.setBackgroundResource(R.drawable.sex_male_rect_bg);
            ViewUtil.setTextViewDrawableLeft(mTvAge, R.mipmap.sex_male_white);
        } else if (gender == 1) {
            mTvAge.setBackgroundResource(R.drawable.sex_female_rect_bg);
            ViewUtil.setTextViewDrawableLeft(mTvAge, R.mipmap.sex_female_white);
        }

        String signature = userInfo.mSignature;
        if (TextUtils.isEmpty(signature)) {
            mTvSignature.setVisibility(View.GONE);
        } else {
            mTvSignature.setVisibility(View.VISIBLE);
            mTvSignature.setText(signature);
        }
        mTvNickname.setText(userInfo.mNickName);

        LocationInfo myLocationInfo = LocationManager.getInstance().getCachedCurrentAddressOrNil();
        if (myLocationInfo != null && userInfo.mLocationInfo != null) {
            LatLng userPoint = new LatLng(userInfo.mLocationInfo.mLatitude, userInfo.mLocationInfo.mLongitude);
            LatLng myPoint = new LatLng(myLocationInfo.mLatitude, myLocationInfo.mLongitude);
            double distance = DistanceUtil.getDistance(userPoint, myPoint);
            String meter = DistanceFormatUtil.format(OtherUserDetailActivity.this, distance);
            if (distance < 50) {
                meter = "<50m";
            }
            mTvDistance.setText(meter);
        }
        if (!TextUtils.isEmpty(userInfo.mBirthday) && !userInfo.mBirthday.equals("0")) {
            try {
                int age = DateUtil.parseAge(Long.valueOf(userInfo.mBirthday));
                mTvAge.setText(String.valueOf(age));
            } catch (Exception e) {
                LogManager.e("============other age compute error==========");
            }
        } else {
            mTvAge.setText("");
        }
        if (userInfo.mLikeCount == 0){
            mTvLike.setText(getString(R.string.other_detail_like_zero));
        }else {
            mTvLike.setText(getString(R.string.other_detail_like, userInfo.mLikeCount));
        }
        if (userInfo.mIsLike == 0) {
            mIvLike.setImageResource(R.mipmap.like);
        } else {
            mIvLike.setImageResource(R.mipmap.liked);
        }

        if (userInfo.isFriend == 1) {

            ViewUtil.toggleView(mLlAddFriend, false);
        }
        updateEmptyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abRightImage:
                if (mDialog == null) {
                    if (mOtherInfo == null) {
                        return;
                    }
                    if (mOtherInfo.isFriend == 1) {
                        mDialog = new BottomChooseDialog(OtherUserDetailActivity.this, BottomChooseDialog.DialogType.FRIEND_OPERATE);
                    } else {
                        mDialog = new BottomChooseDialog(OtherUserDetailActivity.this, BottomChooseDialog.DialogType.NOT_FRIEND_OPERATE);
                    }
                    mDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.dialogFirstLine:
                                    IDSIMManager.getInstance().delfriend(mOhterUid, new MessageManager.HttpCallBack() {

                                        @Override
                                        public void onSuc(Object o) {
                                            IMDBLatestMsgService.deleteMsg(Long.valueOf(mOhterUid));
                                            NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_DELETE_MY_FRIEND, mOhterUid);
                                            ColorfulToast.green(OtherUserDetailActivity.this, getString(R.string.delete_friend_suc), 0);
                                            setResult(RESULT_DELETE_MY_FRIEND);
                                            AccountManager.getInstance().getUser().mProfile.mFriendTotal--;
                                            NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_MY_FRIEND_UPDATA, null);
                                            finish();
                                        }

                                        @Override
                                        public void onFail(Object o) {
                                        }
                                    });

                                    break;
                                case R.id.dialogSecondLine://举报
                                    ReportActivity.show(OtherUserDetailActivity.this, mOhterUid);
                                    break;
                            }
                            mDialog.dismiss();
                        }
                    });
                }
                mDialog.show();
                break;
            case R.id.abBack:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserInfoRequest != null) {
            mUserInfoRequest.cancel();
        }
        if (mLikeUserRequest != null) {
            mLikeUserRequest.cancel();
        }
        if (mCollectFoodRequest != null) {
            mCollectFoodRequest.cancel();
        }
    }

    /**
     * 获取他人收藏数据
     *
     * @param type     收藏类型：1、想玩，2、想吃，3、想看（可选，默认为1）
     * @param listener
     * @return
     */
    public GetOtherCollectionRequest getOtherCollectRequest(int type, OnResponseListener<CollectionWrapper> listener) {
        if (TextUtils.isEmpty(mOhterUid)) {
            return null;
        }
        GetOtherCollectionRequest collectRequest = GetOtherCollectionRequest.createOtherCollectRequest(mOhterUid, type, listener);
        return collectRequest;
    }
}
