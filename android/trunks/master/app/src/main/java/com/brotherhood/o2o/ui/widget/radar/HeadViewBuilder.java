package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.model.RadarItemBean;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/29.
 */
public class HeadViewBuilder {
    private Context mContext;
    private View mView;
    private SimpleDraweeView mPointImg;

    public HeadViewBuilder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 雷达头像信标
     *
     * @return
     */
    public View buildHeadViewPeople(String url) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.headview_people, null);
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) mView.findViewById(R.id.img_point);
        ImageLoader.getInstance().setImageUrl(simpleDraweeView, url, 1, null, Utils.dip2px(40), Utils.dip2px(40));
        return mView;
    }

    public void clickHeadViewPeople(View view)
    {

    }

    public void restoreHeadViewPeople()
    {

    }

    /**
     * 雷达服务信标
     *
     * @return
     */
    public View buildHeadViewServer() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.headview_server, null);
        CircleProgressView circleProgressView= (CircleProgressView) mView.findViewById(R.id.circleprogressview);
        circleProgressView.setProgress(50);
        return mView;
    }

    /**
     * 雷达点信标
     *
     * @return
     */
    public View buildHeadViewPoint(int gender) {
        if (gender == 0) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.headview_point_male, null);
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.headview_point_female, null);
        }
        return mView;
    }

    public void clickHeadViewPoint(View view, int gender) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.img_point);
        if (gender == 0) {
            ImageLoader.getInstance().setImageResource(simpleDraweeView, R.drawable.ic_radar_male_press);
        } else {
            ImageLoader.getInstance().setImageResource(simpleDraweeView, R.drawable.ic_radar_female_press);
        }
    }

    public void restoreHeadViewPoint(ArrayList<RadarItemBean> datas) {
        if (datas != null && datas.size() > 0) {
            for (RadarItemBean data : datas) {
                if(data.mIsFriend == 0 && data.mType == 0)
                {
                    SimpleDraweeView simpleDraweeView = (SimpleDraweeView) data.mHeadView.findViewById(R.id.img_point);
                    if (data.mGender == 0) {
                        ImageLoader.getInstance().setImageResource(simpleDraweeView, R.drawable.ic_radar_male_normal);
                    } else {
                        ImageLoader.getInstance().setImageResource(simpleDraweeView, R.drawable.ic_radar_female_normal);
                    }
                }
            }
        }
    }


}
