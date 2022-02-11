package com.brotherhood.o2o.surprise.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.surprise.controller.adapter.CouponRewardAdapter;
import com.brotherhood.o2o.surprise.helper.SurpriseUrlFetcher;
import com.brotherhood.o2o.surprise.model.CouponRewardInfo;
import com.brotherhood.o2o.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 优惠券奖励的列表页
 */
public class CouponRewardFragment extends BaseFragment {

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @InjectView(R.id.layout_coupon_null)
    View mCouponNullLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.surprise_frag_coupon_reward, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        requestData();
    }

    private void initView(List<CouponRewardInfo> infos) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        CouponRewardAdapter adapter = new CouponRewardAdapter(infos);
        mRecyclerView.setAdapter(adapter);


    }

    private void requestData() {
        SurpriseUrlFetcher.requestSurpriseList(2, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    List<CouponRewardInfo> infos = CouponRewardInfo.getDatas(jsonStr);
                    if (infos.size() == 0) {
                        mRecyclerView.setVisibility(View.GONE);
                        mCouponNullLayout.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mCouponNullLayout.setVisibility(View.GONE);
                        initView(infos);
                    }
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }
}
