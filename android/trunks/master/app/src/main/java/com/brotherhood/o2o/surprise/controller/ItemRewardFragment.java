package com.brotherhood.o2o.surprise.controller;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.controller.OrderSuccessActivity;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.controller.AddDeliveryLocationActivity;
import com.brotherhood.o2o.location.controller.DeliverySuccessActivity;
import com.brotherhood.o2o.surprise.controller.adapter.ItemRewardAdapter;
import com.brotherhood.o2o.surprise.helper.SurpriseUrlFetcher;
import com.brotherhood.o2o.surprise.model.ItemRewardInfo;
import com.brotherhood.o2o.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 实物奖励的列表页
 */
public class ItemRewardFragment extends BaseFragment {

    @InjectView(R.id.list_item)
    RecyclerView mRecyclerView;

    @InjectView(R.id.layout_reward_null)
    View mRewardNullLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.surprise_frag_item_reward, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        requestData();
    }

    private void initView(final List<ItemRewardInfo> infos) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            Paint mPaint;

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                if (mPaint == null) {
                    mPaint = new Paint();
                    mPaint.setAntiAlias(true);
                    mPaint.setColor(Color.parseColor("#0c000000"));
                }

                final int count = parent.getChildCount();
                View child;
                Rect rect = new Rect();
                for (int i = 0; i < count; i++) {
                    child = parent.getChildAt(i);
                    rect.left = child.getLeft();
                    rect.right = child.getRight();
                    rect.bottom = child.getBottom();
                    rect.top = rect.bottom - 1;
                    c.drawRect(rect, mPaint);
                }
            }
        });
        final ItemRewardAdapter adapter = new ItemRewardAdapter(infos);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ItemRewardAdapter.OnRecyclerItemListener() {
            @Override
            public void OnItemClick(View view, int postion) {
                ItemRewardInfo itemRewardInfo = infos.get(postion);
                int mode=itemRewardInfo.mMode;
                int statu=itemRewardInfo.mStatus;
                if(mode == 1)
                {
                    OrderSuccessActivity.show(getActivity(),itemRewardInfo.mId);
                }
                else if(mode == 2)
                {
                    if(statu == 0)
                    {
                        AddDeliveryLocationActivity.show(getActivity(), itemRewardInfo.mId);
                    }
                    else
                    {
                        DeliverySuccessActivity.show(getActivity(), itemRewardInfo.mId);
                    }
                }
            }
        });
    }

    private void requestData() {
        SurpriseUrlFetcher.requestSurpriseList(1, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    List<ItemRewardInfo> infos = ItemRewardInfo.getDatas(jsonStr);
                    if (infos.size() == 0) {
                        mRewardNullLayout.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mRewardNullLayout.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
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
