package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.PullToZoomScrollViewEx;

/**
 * 活动详情
 */
public class EventDetailActivity extends BaseActivity {

    @ViewInject(id = R.id.svPullToZoom)
    private PullToZoomScrollViewEx mPullToZoomScrollView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_detail_layout;
    }

    public static void show(Context context){
        Intent it = new Intent(context, EventDetailActivity.class);
        context.startActivity(it);
    }

    @Override
    protected boolean addOverlayActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.CENTER_TYPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController()
                .setBackImage(R.mipmap.back_image_white)
                .setDivideColor(R.color.white)
                .addIconItem(R.id.abRightImage, R.mipmap.event_activity_like);
        loadViewForCode();
    }

    private void loadViewForCode() {
        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.svPullToZoom);
        //View headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.event_detail_zoom_view, null, false);//ivEventDetailZoom
        View contentView = LayoutInflater.from(this).inflate(R.layout.event_detail_content_view, null, false);
        //scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
    }
}
