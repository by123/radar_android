package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.account.Visitor;
import com.brotherhood.o2o.bean.account.WrapperVisitor;
import com.brotherhood.o2o.chat.ui.ImgPreviewActivity;
import com.brotherhood.o2o.chat.ui.fragment.ImageDownloadFragment;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.BaseAdapterHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.QuickAdapter;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetVisitorRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.account.UserDetailDecoration;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情
 */
public class UserDetailActivity extends BaseActivity {

    @ViewInject(id = R.id.rvUserDetailRecyclerView)
    private RecyclerView mRecyclerView;

    @ViewInject(id = R.id.vsUserDetailLocation)
    private ViewStub mViewStubLocation;

    private RelativeLayout mRlLocation;

    private TextView mTvLocation;

    private RelativeLayout mRlStar;

    private TextView mTvStar;

    @ViewInject(id = R.id.tvUserDetailSignature)
    private TextView mTvSignature;

    @ViewInject(id = R.id.tvUserDetailNoVisitor)
    private TextView mTvNoVisitor;

    @ViewInject(id = R.id.tvUserDetailName)
    private TextView mTvNickname;

    @ViewInject(id = R.id.tvUserDetailVisitCount)
    private TextView mTvVisitCount;

    @ViewInject(id = R.id.tvUserDetailLikeCount)
    private TextView mTvLikeCount;

    @ViewInject(id = R.id.ivUserDetailIcon, clickMethod = "preViewHead")
    private ImageView mIvHeadIcon;

    private GetVisitorRequest mVisitorRequest;
    private List<Visitor> mVisitorList = new ArrayList<>();
    private QuickAdapter<Visitor> mAdapter;
    public static void show(Context context){
        Intent it = new Intent(context, UserDetailActivity.class);
        context.startActivity(it);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_detail_layout;
    }

    @Override
    protected boolean addOverlayActionBar() {
        return true;
    }

    public void preViewHead(View view){
        UserInfo userInfo = AccountManager.getInstance().getUser();
        if (userInfo == null){
//            SplashLoginActivity.show(this);
            OverseaSplashLoginActivity.show(this);
            LogManager.e("=========user detail userinfo is null==========");
            return;
        }
        if (!TextUtils.isEmpty(userInfo.mIcon)){
            Intent intent = new Intent(UserDetailActivity.this, ImgPreviewActivity.class);
            intent.putExtra(ImgPreviewActivity.FILE_PATH, userInfo.mIcon);
            intent.putExtra(ImgPreviewActivity.PREVIEW_TYPE, ImageDownloadFragment.FILE_URL_TYPE);
            startActivity(intent);
            overridePendingTransition(R.anim.scale_out, 0);
        }
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    private void initView(){
        getActionBarController()
                .setBackImage(R.mipmap.back_image_white)
                .setDivideColor(R.color.white)
                .setBaseTitle(R.string.user_detail_title, R.color.white)
                .addIconItem(R.id.abRightImage, R.mipmap.user_detail_set)
                .hideHorizontalDivide();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        //mRecyclerView.addItemDecoration(new PhotoDecoration(1, DisplayUtil.dp2px(10), true));
        mRecyclerView.addItemDecoration(new UserDetailDecoration(1, DisplayUtil.dp2px(5), DisplayUtil.dp2px((float) 17.5), DisplayUtil.dp2px(5), DisplayUtil.dp2px((float) 17.5)
                , true));
        if (mAdapter == null){
            mAdapter = new QuickAdapter<Visitor>(UserDetailActivity.this, R.layout.user_detail_visit_item) {
                @Override
                protected void onBindViewHolder(BaseAdapterHelper helper, final Visitor visitor, int position) {
                    if (mVisitorList.size() >= 6){
                        if (position == mVisitorList.size() - 1){//
                            helper.displayImageByResource(R.id.ivUserDetailItemIcon, R.mipmap.user_detail_more);
                            helper.setScaleType(R.id.ivUserDetailItemIcon, ImageView.ScaleType.FIT_XY);
                            helper.setOnClickListener(R.id.ivUserDetailItemIcon, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    VisitorListActivity.show(UserDetailActivity.this);
                                }
                            });
                        }else {
                            helper.disCircleImageByUrl(R.id.ivUserDetailItemIcon, visitor.mIcon, R.mipmap.ic_msg_default);
                            helper.setScaleType(R.id.ivUserDetailItemIcon, ImageView.ScaleType.CENTER_CROP);
                            helper.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OtherUserDetailActivity.show(UserDetailActivity.this, visitor.mUid);
                                }
                            });
                        }
                    }else {
                        helper.disCircleImageByUrl(R.id.ivUserDetailItemIcon, visitor.mIcon, R.mipmap.ic_msg_default);
                        helper.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OtherUserDetailActivity.show(UserDetailActivity.this, visitor.mUid);
                            }
                        });
                    }
                }
            };
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadVisitor();
        UserInfo userInfo = AccountManager.getInstance().getUser();
        if (userInfo == null){
//            SplashLoginActivity.show(this);
            OverseaSplashLoginActivity.show(this);
            LogManager.e("=========user detail userinfo is null==========");
            return;
        }
        if (!TextUtils.isEmpty(userInfo.mIcon)){
            ImageLoaderManager.displayCircleBorderImageByUrl(this, mIvHeadIcon, userInfo.mIcon, DisplayUtil.dp2px(2), R.mipmap.ic_msg_default);
        }
        if (!TextUtils.isEmpty(userInfo.mResidence) || !TextUtils.isEmpty(userInfo.mBirthday)){
            mViewStubLocation.inflate();
            if (!TextUtils.isEmpty(userInfo.mResidence)){
                mRlLocation = (RelativeLayout) findViewById(R.id.rlUserDetailLocation);
                mTvLocation = (TextView) findViewById(R.id.tvUserDetailLocation);
                mRlLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(userInfo.mResidence);
            }
            if (!TextUtils.isEmpty(userInfo.mBirthday)){
                mRlStar = (RelativeLayout) findViewById(R.id.rlUserDetailStar);
                mTvStar = (TextView) findViewById(R.id.tvUserDetailStar);
                mRlStar.setVisibility(View.VISIBLE);
                String star = DateUtil.parseConstellation(Long.valueOf(userInfo.mBirthday));
                mTvStar.setText(star);
            }
        }
        String signature = userInfo.mSignature;
        if (TextUtils.isEmpty(signature)){
            mTvSignature.setVisibility(View.GONE);
        }else {
            mTvSignature.setVisibility(View.VISIBLE);
            mTvSignature.setText(signature);
        }
        mTvNickname.setText(userInfo.mNickName);
        mTvLikeCount.setText(getString(R.string.user_detail_likes_count, userInfo.mLikeCount));
        if (userInfo.mProfile != null) {
            mTvVisitCount.setText(getString(R.string.user_detail_visitor_count, userInfo.mProfile.mVisitTotal));
        }

    }

    private void loadVisitor(){
        if (mVisitorRequest == null){
            mVisitorRequest = GetVisitorRequest.createVisitorRequest(null, String.valueOf(5), new OnResponseListener<WrapperVisitor>(){
                @Override
                public void onSuccess(int code, String msg, WrapperVisitor wrapperVisitor, boolean cache) {
                    mVisitorList.clear();
                    if (wrapperVisitor == null){
                        return;
                    }
                    mTvVisitCount.setText(getString(R.string.user_detail_visitor_count, wrapperVisitor.mVisitCount));
                    List<Visitor> visitors = wrapperVisitor.mVisitorList;
                    if (visitors == null || visitors.isEmpty()){
                        mTvNoVisitor.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    mTvNoVisitor.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (visitors.size() >0 && visitors.size() < 5){
                        mVisitorList.addAll(visitors);
                    }else if (visitors.size() >= 5){
                        for (int i = 0; i < 5; i++) {
                            mVisitorList.add(visitors.get(i));
                        }
                        mVisitorList.add(new Visitor());//更多按钮
                    }
                    //最近访客
                    if (!mVisitorList.isEmpty()){
                        mAdapter.addAll(mVisitorList);
                    }

                    AccountManager.getInstance().getUser().mProfile.mVisitTotal = wrapperVisitor.mVisitCount;
                    NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_VISITOR_TOTAL, null);

                }

                @Override
                public void onFailure(int code, String msg) {
                    mTvNoVisitor.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            });
        }
        mVisitorRequest.sendRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVisitorRequest != null){
            mVisitorRequest.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
            case R.id.abRightImage://设置
                UserSettingActivity.show(UserDetailActivity.this);
                finish();
                break;
        }
    }
}
