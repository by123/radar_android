package com.brotherhood.o2o.personal.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/6/3.
 */

public class PersonalFragment extends BaseFragment {
    @InjectView(R.id.layout_userinfo)
    View mUserInfoView;

    @InjectView(R.id.layout_order)
    View mOrderView;

    @InjectView(R.id.layout_coupon)
    View mCouponView;

    @InjectView(R.id.layout_systeminfo)
    View mSystemInfoView;

//    @InjectView(R.id.layout_invitefriend)
//    View mInviteFriendView;

    @InjectView(R.id.layout_advice)
    View mAdviceView;

    @InjectView(R.id.layout_about)
    View mAboutView;

    @InjectView(R.id.point_systemmsg)
    View mSystemMsgPoint;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_personal, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mSystemMsgPoint.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.layout_userinfo)
    void onUserInfoLayoutClick() {
    }

    @OnClick(R.id.layout_order)
    void onOrderLayoutClick() {

    }

    @OnClick(R.id.layout_coupon)
    void onCouponLayoutClick() {
    }

    @OnClick(R.id.layout_systeminfo)
    void onSysteminfoLayoutClick() {
    }

    @OnClick(R.id.layout_advice)
    void onAdviceLayoutClick() {
    }

    @OnClick(R.id.layout_about)
    void onAboutLayoutClick() {
    }
}
