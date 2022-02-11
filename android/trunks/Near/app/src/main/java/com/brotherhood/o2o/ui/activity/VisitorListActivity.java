package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.Visitor;
import com.brotherhood.o2o.bean.account.WrapperVisitor;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.BaseAdapterHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.QuickAdapter;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.request.GetVisitorRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.BaseRecyclerView;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 访客列表
 */
public class VisitorListActivity extends BaseActivity {

    @ViewInject(id = R.id.rvBaseRecycler)
    private BaseRecyclerView mBaseRecyclerView;

    private List<Visitor> mVisitorList = new ArrayList<>();
    private QuickAdapter<Visitor> mAdapter;

    private GetVisitorRequest mVisitorRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visit_list_layout;
    }

    public static void show(Context context) {
        Intent it = new Intent(context, VisitorListActivity.class);
        context.startActivity(it);
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }


    @Override
    protected boolean showLoading() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController().setDivideColor(R.color.black).setBackImage(R.mipmap.back_image_black).setHeadBackgroundColor(R.color.white).setBaseTitle(R
                .string.slide_menu_visitor, R.color.black);
        mStateView.setViewForState(StateViewManager.getEmpty(VisitorListActivity.this, R.string.no_visitor, R.mipmap.ic_msg_no_friend_normal), MultiStateView.VIEW_STATE_EMPTY);
        //mStateView.setViewForState(R.layout.error_view, MultiStateView.VIEW_STATE_ERROR);
        //mStateView.setViewForState(R.layout.loading_view, MultiStateView.VIEW_STATE_LOADING);
        //mStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        mAdapter = new QuickAdapter<Visitor>(VisitorListActivity.this, R.layout.visitor_list_item) {
            @Override
            protected void onBindViewHolder(BaseAdapterHelper helper, final Visitor visitor, int position) {
                if (visitor == null) {
                    return;
                }
                helper.disCircleImageByUrl(R.id.ivVisitorIcon, visitor.mIcon, R.mipmap.ic_msg_default);
                helper.setText(R.id.tvVisitorName, visitor.mNickname);
                int age = DateUtil.parseAge(visitor.mBirthday);
                helper.setText(R.id.tvVisitorAge, String.valueOf(age));
                //todo  设置年龄背景、drawableLeft
                int gender = visitor.mGender;
                if (gender == 0) {
                    helper.setBackgroundRes(R.id.tvVisitorAge, R.drawable.sex_male_oval_bg);
                    helper.setTextViewDrawableLeft(R.id.tvVisitorAge, R.mipmap.sex_male_white);
                } else if (gender == 1) {
                    helper.setBackgroundRes(R.id.tvVisitorAge, R.drawable.sex_female_oval_bg);
                    helper.setTextViewDrawableLeft(R.id.tvVisitorAge, R.mipmap.sex_female_white);
                }
                helper.setText(R.id.tvVisitorSignature, visitor.mSignature);
                if (TextUtils.isEmpty(visitor.mVisitTime)) {
                    return;
                }
                final String visitTime = DateUtil.parseUnixTimeToString(Long.valueOf(visitor.mVisitTime), "MM-dd HH:mm");
                if (TextUtils.isEmpty(visitTime)) {
                    return;
                }
                String[] visitTimeArray = visitTime.split(" ");
                if (visitTimeArray == null) {
                    return;
                }
                if (visitTimeArray[0] != null) {
                    helper.setText(R.id.tvVisitorDate, visitTimeArray[0]);
                }
                if (visitTimeArray[1] != null) {
                    helper.setText(R.id.tvVisitorTime, visitTimeArray[1]);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(visitor.mUid)) {
                            return;
                        }
                        OtherUserDetailActivity.show(VisitorListActivity.this, visitor.mUid);
                    }
                });
            }
        };
        mBaseRecyclerView.setAdapter(mAdapter);
        getVisitInfo();
        mStateView.setViewForState(StateViewManager.getEmpty(VisitorListActivity.this, R.string.no_visitor, R.mipmap.ic_msg_no_friend_normal), MultiStateView.VIEW_STATE_EMPTY);
    }


    private void getVisitInfo() {
        if (mVisitorRequest == null) {
            mVisitorRequest = GetVisitorRequest.createVisitorRequest(null, String.valueOf(100), new OnResponseListener<WrapperVisitor>() {
                @Override
                public void onSuccess(int code, String msg, WrapperVisitor wrapperVisitor, boolean cache) {
                    if (wrapperVisitor == null || wrapperVisitor.mVisitorList == null || wrapperVisitor.mVisitorList.isEmpty()) {
                        //没有好友来访
                        showEmptyView();
                        return;
                    }
                    mVisitorList.addAll(wrapperVisitor.mVisitorList);
                    mAdapter.addAll(mVisitorList);
                    showContentView();
                }

                @Override
                public void onFailure(int code, String msg) {
                   showErrorView();
                    ColorfulToast.orange(VisitorListActivity.this, msg, Toast.LENGTH_SHORT);
                }
            });
        }
        mVisitorRequest.sendRequest();
    }

    @Override
    protected void onStart() {
        //oncreate绘制完以后，再获取并更改TextView的宽度
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVisitorRequest != null) {
            mVisitorRequest.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
        }
    }
}
