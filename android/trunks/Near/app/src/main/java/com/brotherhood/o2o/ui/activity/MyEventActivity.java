package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.radar.RadarEvent;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.BaseAdapterHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.QuickAdapter;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.request.GetMyEventRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的活动列表
 */
public class MyEventActivity extends BaseActivity {

    @ViewInject(id = R.id.rvEventList)
    private RecyclerView mRecyclerView;

    private QuickAdapter<RadarEvent> mAdapter;
    private List<RadarEvent> mEventList = new ArrayList<>();

    private GetMyEventRequest mEventRequest;

    public static void show(Context context){
        Intent it = new Intent(context, MyEventActivity.class);
        context.startActivity(it);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_event_layout;
    }

    private void loadData(){
        if (mEventRequest == null){
            // TODO: 2015/12/21 1、第三个参数是否要传？ 2、返回对象不是RadarEvent,需要重新定义
            mEventRequest = GetMyEventRequest.createEventRequest(3, System.currentTimeMillis()+"", 10, new OnResponseListener<RadarEvent>() {

                @Override
                public void onSuccess(int code, String msg, RadarEvent radarEvent, boolean cache) {

                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        }
        mEventRequest.sendRequest();


        for (int i = 0; i < 10; i++) {
            RadarEvent event = new RadarEvent();
            event.mPlace = "地点"+i;
            event.mTitle = "深圳"+i;
            event.mSupplier = "提供者"+i;
            event.mEndTime = "测试时间";
            mEventList.add(event);
        }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .setHeadBackgroundColor(R.color.white)
                .setBaseTitle(R.string.slide_menu_myactivity, R.color.black);
        loadData();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new PhotoDecoration(1, DisplayUtil.dp2px(10), true));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new QuickAdapter<RadarEvent>(this, R.layout.my_activity_item) {
            @Override
            protected void onBindViewHolder(BaseAdapterHelper helper, RadarEvent radarEvent, int position) {
                helper.setText(R.id.tvMyEventItemTitle, radarEvent.mTitle);
                helper.setText(R.id.tvMyEventItemLocation, radarEvent.mPlace);
                helper.setText(R.id.tvMyEventItemSupplier, radarEvent.mSupplier);
                helper.setText(R.id.tvMyEventItemTime, radarEvent.mEndTime);
                helper.displayRoundImageByUrl(R.id.ivMyEventItemIcon, radarEvent.mIcon, R.mipmap.ic_launcher, DisplayUtil.dp2px(5));
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addAll(mEventList);
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventRequest != null){
            mEventRequest.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
        }
    }
}
