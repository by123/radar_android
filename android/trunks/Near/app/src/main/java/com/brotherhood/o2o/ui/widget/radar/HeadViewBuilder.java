package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.radar.RadarPeople;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/29.
 */


public class HeadViewBuilder {
    private Context mContext;
    private View mView;
    private ImageView mPointImg;

    public HeadViewBuilder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 雷达服务信标
     *
     * @return
     */
    public View buildHeadViewServer(String url, int count) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.radar_headview_server, null);
        ImageView ivHeadServerBg = (ImageView) mView.findViewById(R.id.ivRadarHeadServerBg);
        ImageView ivHeadServerIcon = (ImageView) mView.findViewById(R.id.ivRadarHeadServer);
        if (count == 1) {
            ivHeadServerBg.setImageResource(R.mipmap.radar_event_normal_bg);
        } else if (count == 2) {
            ivHeadServerBg.setImageResource(R.mipmap.radar_event_double_bg);
        } else if (count >= 3) {
            ivHeadServerBg.setImageResource(R.mipmap.radar_event_three_bg);
        }
        ImageLoaderManager.displayCircleImageByUrl(mContext, ivHeadServerIcon, url, R.mipmap.ic_msg_default);
        return mView;
    }

    /**
     * 周边（人）点信标
     *
     * @return
     */

    public View buildHeadViewPoint(int gender) {
        if (gender == 0) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.radar_headview_point_male, null);
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.radar_headview_point_female, null);
        }
        return mView;
    }

    public void clickHeadViewPoint(View view, int gender) {
        ImageView imageView = (ImageView) view.findViewById(R.id.img_point);
        ViewUtil.setViewBackground(imageView, R.mipmap.radar_people_press_bg);
    }

    public void restoreHeadViewPoint(ArrayList<RadarPeople> datas) {
        if (datas != null && datas.size() > 0) {
            for (RadarPeople data : datas) {
                if (data.mHeadView != null){
                    ImageView imageView = (ImageView) data.mHeadView.findViewById(R.id.img_point);
                    ViewUtil.setViewBackground(imageView, null);
                }
            }
        }
    }
}
