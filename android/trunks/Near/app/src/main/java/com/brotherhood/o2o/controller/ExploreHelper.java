package com.brotherhood.o2o.controller;

import android.graphics.PointF;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.radar.RadarEvent;
import com.brotherhood.o2o.bean.radar.RadarPeople;
import com.brotherhood.o2o.bean.radar.RadarPoi;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.ui.widget.radar.DirectLayout;
import com.brotherhood.o2o.ui.widget.radar.HeadViewBuilder;
import com.brotherhood.o2o.util.DisplayUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/16.
 */
public class ExploreHelper {

    private static ExploreHelper mHelper;
    private static byte[] sync = new byte[0];
    public double Radar_Center_W = (Constants.SCREEN_WIDTH * 15 / 32);
    public double Radar_Center_H = (Constants.SCREEN_WIDTH * 15 / 32);
    public double RADAR_MARGIN_LEFT = (Constants.SCREEN_WIDTH / 32);
    //雷达扫描动画
    public final static int SCAN_TIME = 4000;

    public static ExploreHelper getInstance() {
        if (mHelper == null) {
            synchronized (sync) {
                if (mHelper == null) {
                    mHelper = new ExploreHelper();
                }
            }
        }
        return mHelper;
    }

    /**
     * 雷达扫描动画
     *
     * @param view
     */
    public void startScanAnim(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(SCAN_TIME);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                showPoiAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                showPoiAnimation();
                startShowOther();
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }

    private void showPoiAnimation() {
        if (mRadarPeopleList != null && mRadarPeopleList.size() > 0) {
            for (final RadarPeople item : mRadarPeopleList) {
                long delayTimes = Constants.HEAD_DELAY
                        + (long) (item.mDegree * (SCAN_TIME / (Math.PI * 2)));
                if (item.mHeadView != null && item.mHeadView.getVisibility() != View.VISIBLE) {
                    item.mHeadView.postDelayed(new Runnable() {

                        @Override
                        public void run() {//POI 淡入动画
                            if (item.isShow) {
                                item.mHeadView.setVisibility(View.VISIBLE);
                                if (!item.isCovered) {
                                    final AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.setDuration(200);
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(item.mHeadView, Constants.ALPHA, 0f, 1f));
                                    animatorSet.start();
                                }
                            }
                        }
                    }, delayTimes);
                }

            }
        }
        if (mRadarEventList != null && mRadarEventList.size() > 0) {
            for (final RadarEvent item : mRadarEventList) {
                long delayTimes = Constants.HEAD_DELAY
                        + (long) (item.mDegree * (SCAN_TIME / (Math.PI * 2)));
                if (item.mHeadView != null && item.mHeadView.getVisibility() != View.VISIBLE) {
                    item.mHeadView.postDelayed(new Runnable() {

                        @Override
                        public void run() {//POI 淡入动画
                            if (item.isShow) {
                                item.mHeadView.setVisibility(View.VISIBLE);
                                final AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.setDuration(200);
                                animatorSet.playTogether(ObjectAnimator.ofFloat(item.mHeadView, Constants.ALPHA, 0f, 1f));
                                animatorSet.start();
                            }
                        }
                    }, delayTimes);
                }
            }
        }
        if (mWebEventList != null && mWebEventList.size() > 0) {
            for (final RadarEvent item : mWebEventList) {
                long delayTimes = Constants.HEAD_DELAY
                        + (long) (item.mDegree * (SCAN_TIME / (Math.PI * 2)));
                if (item.mHeadView != null && item.mHeadView.getVisibility() != View.VISIBLE) {
                    item.mHeadView.postDelayed(new Runnable() {

                        @Override
                        public void run() {//POI 淡入动画
                            item.mHeadView.setVisibility(View.VISIBLE);
                            final AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.playTogether(ObjectAnimator.ofFloat(item.mHeadView, Constants.ALPHA, 0f, 1f));
                            animatorSet.start();
                        }
                    }, delayTimes);
                }
            }
        }
    }

    /**
     * 绘制附近的人在雷达盘上的分布
     */
    private static int CENTER_WIDTH = DisplayUtil.dp2px(76);
    private static int POINT_WIDTH = DisplayUtil.dp2px(24);
    private static int SERVER_WIDTH = (int) NearApplication.mInstance.getResources().getDimension(R.dimen.radar_server_width);
    private static int PEOPLE_WIDTH = DisplayUtil.dp2px(40);
    private static int OFF_SET = DisplayUtil.dp2px(15);//偏移量
    private int width = POINT_WIDTH;
    private ArrayList<RadarPeople> mRadarPeopleList;
    private ArrayList<RadarEvent> mRadarEventList;
    private ArrayList<RadarEvent> mWebEventList;

    /**
     * 人物信标数据传递
     *
     * @param oldPeople 旧数据
     * @param newPeople 新数据
     */
    private void transmitData(RadarPeople oldPeople, RadarPeople newPeople) {
        if (oldPeople == null || newPeople == null || oldPeople.mUid != newPeople.mUid) {
            return;
        }
        oldPeople.mCoverPeopleList = newPeople.mCoverPeopleList;
        oldPeople.mDistance = newPeople.mDistance;
        oldPeople.mGender = newPeople.mGender;
        oldPeople.mAvatar = newPeople.mAvatar;
        oldPeople.mBirthday = newPeople.mBirthday;
        oldPeople.mIsFriend = newPeople.mIsFriend;
        oldPeople.mNickname = newPeople.mNickname;
        oldPeople.mOnline = newPeople.mOnline;
        oldPeople.mPhone = newPeople.mPhone;
        oldPeople.mRefresh = newPeople.mRefresh;
        oldPeople.mResidence = newPeople.mResidence;
        oldPeople.mSignature = newPeople.mSignature;
        oldPeople.mVirtual = newPeople.mVirtual;
        oldPeople.mSignature = newPeople.mSignature;
        oldPeople.mLocation = newPeople.mLocation;
        getXYValue(oldPeople);
        double realRadius = Constants.dLargestDistance;//雷达半径对应的实际距离
        double radarRadius = Radar_Center_W;//雷达盘的半径
        width = POINT_WIDTH;
        /**
         * 信标与雷达中心的距离 / 雷达盘半径  =  (信标与中心的实际距离 - 最近信标离中心的距离) / (最远信标离中心的距离 - 最近信标离中心的距离)
         */
        double bValue = (oldPeople.mDistance - Constants.dSmallestDistance) * radarRadius / realRadius;
        computeMove(bValue, oldPeople);
    }

    /**
     * 重绘人物信标
     *
     * @param mScanImg     扫描针
     * @param mRadarLayout 信标容器
     * @param requestDatas 人物信标数据
     */

    public void reDrawHeadInfo(View mScanImg, DirectLayout mRadarLayout, ArrayList<RadarPeople> requestDatas) {
        if (mRadarPeopleList != null && mRadarPeopleList.size() > 0) {
            for (RadarPeople data : mRadarPeopleList) {
                mRadarLayout.removeView(data.mHeadView);
            }
        }
        if (requestDatas == null || requestDatas.isEmpty()) {
            return;
        }
        /**
         * headview缓存实现
         * 1、uid相同的，赋值
         * 2、雷达盘上不存在该uid，添加
         * 3、新数据中不存在雷达盘上的uid，删除
         */
        //ArrayList<RadarPeople> addPeopleList = new ArrayList<>();
        //List<RadarPeople> deletePeopleList = new ArrayList<>();
        //boolean delete = true;
        //if (mRadarPeopleList != null && mRadarPeopleList.size() > 0) {
        //    for (RadarPeople data:mRadarPeopleList) {
        //        for(RadarPeople newData:requestDatas){
        //            if (data.mUid == newData.mUid){
        //                newData.isAdd = false;
        //                delete = false;
        //                transmitData(data, newData);
        //            }
        //        }
        //        if (delete){
        //            deletePeopleList.add(data);
        //        }
        //        delete = true;
        //    }
        //    for (RadarPeople people:requestDatas) {
        //        if (people.isAdd){
        //            addPeopleList.add(people);
        //        }
        //    }
        //    if (!deletePeopleList.isEmpty()){
        //        for (RadarPeople deletePeople:deletePeopleList) {
        //            mRadarLayout.removeView(deletePeople.mHeadView);
        //        }
        //    }
        //    if (!addPeopleList.isEmpty()){
        //        addNewPeople(mRadarLayout, addPeopleList);
        //    }
        //
        //}else {
        addNewPeople(mRadarLayout, requestDatas);
        //}
    }

    private void addNewPeople(DirectLayout mRadarLayout, ArrayList<RadarPeople> requestDatas) {
        HeadViewBuilder builder = new HeadViewBuilder(NearApplication.mInstance.getApplicationContext());
        //雷达半径对应的实际距离
        double realRadius = Constants.dLargestDistance;
        //雷达盘的半径
        double radarRadius = Radar_Center_W;
        this.mRadarPeopleList = requestDatas;
        synchronized (requestDatas) {
            for (int i = 0; i < requestDatas.size(); i++) {
                RadarPeople data = requestDatas.get(i);
                data.mType = 0;
                View headView = builder.buildHeadViewPoint(data.mGender);
                width = POINT_WIDTH;
                /**
                 * 信标与雷达中心的距离 / 雷达盘半径  =  (信标与中心的实际距离 - 最近信标离中心的距离) / (最远信标离中心的距离 - 最近信标离中心的距离)
                 */
                double bValue = (data.mDistance - Constants.dSmallestDistance) * radarRadius / realRadius;
                computePosition(bValue, mRadarLayout, data, headView);
            }
        }
    }

    /**
     * 移动人物信标
     */
    public void moveHeadInfo() {
        if (mRadarPeopleList == null || mRadarPeopleList.isEmpty()) {
            return;
        }
        //雷达半径对应的实际距离
        double realRadius = Constants.dLargestDistance;
        double radarRadius = Radar_Center_W;
        synchronized (mRadarPeopleList) {
            for (int i = 0; i < mRadarPeopleList.size(); i++) {
                RadarPeople data = mRadarPeopleList.get(i);
                width = POINT_WIDTH;
                /**
                 * 信标与雷达中心的距离 / 雷达盘半径  =  (信标与中心的实际距离 - 最近信标离中心的距离) / (最远信标离中心的距离 - 最近信标离中心的距离)
                 */
                double distance = data.mDistance - Constants.dSmallestDistance;
                if (Constants.dLargestDistance == Constants.dSmallestDistance) {
                    distance = data.mDistance;
                }
                double bValue = distance * radarRadius / realRadius;
                computeMove(bValue, data);
            }
        }
    }

    /**
     * 移动活动信标
     */
    public void moveEventInfo() {
        if (mRadarEventList == null || mRadarEventList.isEmpty()) {
            return;
        }
        //雷达半径对应的实际距离
        double realRadius = Constants.dLargestDistance;
        double radarRadius = Radar_Center_W;
        synchronized (mRadarEventList) {
            for (int i = 0; i < mRadarPeopleList.size(); i++) {
                RadarPeople data = mRadarPeopleList.get(i);
                width = SERVER_WIDTH;
                /**
                 * 信标与雷达中心的距离 / 雷达盘半径  =  (信标与中心的实际距离 - 最近信标离中心的距离) / (最远信标离中心的距离 - 最近信标离中心的距离)
                 */
                double bValue = (data.mDistance - Constants.dSmallestDistance) * radarRadius / realRadius;
                computeMove(bValue, data);
            }
        }
    }

    /**
     * 如果活动数量为1，背景为radar_event_normal_bg 正常位置
     * 如果活动数量为2，背景为radar_event_double_bg 最近位置
     * 如果活动数量>3，背景为radar_event_three_bg  最近位置
     *
     * @param mScanImg     扫描针
     * @param mRadarLayout 信标容器
     * @param requestDatas 活动信标数据
     */
    public void reDrawEventInfo(View mScanImg, DirectLayout mRadarLayout, ArrayList<RadarEvent> requestDatas) {
        if (mRadarEventList != null && mRadarEventList.size() > 0) {
            for (RadarEvent data : mRadarEventList) {
                mRadarLayout.removeView(data.mHeadView);
            }
        }
        if (requestDatas == null || requestDatas.isEmpty()) {
            return;
        }
        this.mRadarEventList = requestDatas;
        // 计算当前雷达半径对应的实际距离
        double realRadius = Constants.dLargestDistance;
        //雷达盘的半径对应的距离
        double radarRadius = Radar_Center_W;
        HeadViewBuilder builder = new HeadViewBuilder(NearApplication.mInstance.getApplicationContext());
        synchronized (requestDatas) {
            View headView = null;
            RadarEvent data = requestDatas.get(0);
            int eventCount = getEventCount(requestDatas);
            if (eventCount == 1) {
                headView = builder.buildHeadViewServer(data.mIconIn, 1);
            } else if (eventCount == 2) {
                headView = builder.buildHeadViewServer(data.mIconIn, 2);
            } else if (eventCount >= 3) {
                headView = builder.buildHeadViewServer(data.mIconIn, 3);
            } else {// <= 0 return;
                return;
            }
            data.mType = 1;
            width = SERVER_WIDTH;
            /**
             * 信标与雷达中心的距离 / 雷达盘半径  =  (信标与中心的实际距离 - 最近信标离中心的距离) / (最远信标离中心的距离 - 最近信标离中心的距离)
             */
            double bValue = (data.mDistance - Constants.dSmallestDistance) * radarRadius / realRadius;
            computePosition(bValue, mRadarLayout, data, headView);
        }
    }

    /**
     * 优惠券
     *
     * @param mScanImg
     * @param mRadarLayout
     * @param requestDatas
     */
    public void reDrawWebEventInfo(View mScanImg, DirectLayout mRadarLayout, ArrayList<RadarEvent> requestDatas) {
        if (mWebEventList != null && mWebEventList.size() > 0) {
            for (RadarEvent data : mWebEventList) {
                mRadarLayout.removeView(data.mHeadView);
            }
        }
        if (requestDatas == null || requestDatas.isEmpty()) {
            return;
        }
        this.mWebEventList = requestDatas;
        HeadViewBuilder builder = new HeadViewBuilder(NearApplication.mInstance.getApplicationContext());
        synchronized (requestDatas) {
            int size = requestDatas.size();
            if (size > 3) {
                size = 3;
            }
            for (int i = 0; i < size; i++) {
                RadarEvent data = requestDatas.get(i);
                View headView = builder.buildHeadViewServer(data.mIconIn, 1);
                double xValue = Radar_Center_W / 2;
                double yValue = Radar_Center_H / 2;
                if (i == 1) {
                    xValue = Radar_Center_W * 3 / 2;
                    yValue = Radar_Center_H / 2;
                } else if (i == 2) {
                    xValue = Radar_Center_W;
                    yValue = Radar_Center_H * 3 / 2;
                }
                data.mPosX = xValue;
                data.mPosY = yValue;
                data.mType = 1;

                double degree = Math.toDegrees(Math.atan2(data.mPosY - Radar_Center_H, data.mPosX - Radar_Center_W));
                degree = (degree + 360);
                degree = (degree > 360) ? (degree - 360) : degree;
                data.mDegree = Math.toRadians(degree);
                data.mHeadView = headView;
                ViewHelper.setTranslationX(headView, (float) xValue);
                ViewHelper.setTranslationY(headView, (float) yValue);
                mRadarLayout.addView(headView);
                headView.setVisibility(View.GONE);
            }
        }
    }


    public int getEventCount(ArrayList<RadarEvent> requestDatas) {
        int eventCount = 0;
        for (int j = 0; j < requestDatas.size(); j++) {
            if (requestDatas.get(j).mDistance - Constants.dSmallestDistance <= Constants.dLargestDistance) {
                eventCount++;
            }
        }
        return eventCount;
    }


    /**
     * 清空所有图层
     */
    public void removeAllPois(DirectLayout mRadarLayout) {
        if (mRadarLayout == null) {
            return;
        }
        //优化为不会每次刷新都重新拉取数据
        if (mRadarPeopleList != null && !mRadarPeopleList.isEmpty()) {
            for (RadarPeople item : mRadarPeopleList) {
                mRadarLayout.removeView(item.mHeadView);
            }
        }
        if (mRadarEventList != null && !mRadarEventList.isEmpty()) {
            for (RadarEvent item : mRadarEventList) {
                mRadarLayout.removeView(item.mHeadView);
            }
        }
        if (mWebEventList != null && !mWebEventList.isEmpty()) {
            for (RadarEvent item : mWebEventList) {
                mRadarLayout.removeView(item.mHeadView);
            }
        }
    }

    /**
     * 计算图层在雷达上的位置
     *
     * @param bValue       雷达盘上信标与雷达中心距离
     * @param mRadarLayout
     * @param data
     * @param headView
     */
    private void computePosition(double bValue, DirectLayout mRadarLayout, RadarPoi data, View headView) {
        getXYValue(data);
        data.isShow = true;
        translatePosition(bValue, data, headView);
        data.mHeadView = headView;
        mRadarLayout.addView(headView);
        headView.setVisibility(View.GONE);
        if (Radar_Center_W < bValue) {
            data.isShow = false;
        } else {
            data.isShow = true;
        }
    }

    /**
     * 移动坐标
     *
     * @param bValue
     * @param data
     * @param headView
     */
    private void translatePosition(double bValue, RadarPoi data, View headView) {
        double xValue = 0.0;
        double yValue = 0.0;
        if (bValue < CENTER_WIDTH / 2 + OFF_SET) {//圆心到信标距离小于内圆半径
            bValue = CENTER_WIDTH / 2 + OFF_SET;
            xValue = bValue * Math.cos(data.mDegree);
            yValue = bValue * Math.sin(data.mDegree);
            //雷达圆心坐标(Radar_Center_W，Radar_Center_H)
            xValue = Radar_Center_W + xValue - width / 2;
            yValue = Radar_Center_H + yValue - width / 2;
        } else {
            if (bValue - Radar_Center_W <= 0 && bValue + width > Radar_Center_W) {//信标在雷达边界时
                xValue = (Radar_Center_W - width) * Math.cos(data.mDegree);
                yValue = (Radar_Center_H - width) * Math.sin(data.mDegree);
                xValue = Radar_Center_W + xValue - width / 2;
                yValue = Radar_Center_H + yValue - width / 2;
            } else {
                xValue = Radar_Center_W + bValue * Math.cos(data.mDegree) - width / 2;
                yValue = Radar_Center_H + bValue * Math.sin(data.mDegree) - width / 2;
            }
        }
        data.mPosX = xValue;
        data.mPosY = yValue;
        if (headView != null) {
            ViewHelper.setTranslationX(headView, (float) xValue);
            ViewHelper.setTranslationY(headView, (float) yValue);
        }
    }

    /**
     * 信标移动动画
     *
     * @param bValue
     * @param data
     */
    private void computeMove(double bValue, RadarPoi data) {
        if (data.mHeadView == null) {
            return;
        }
        translatePosition(bValue, data, data.mHeadView);
        boolean oldState = data.isShow;
        if (Radar_Center_W < bValue) {
            data.isShow = false;
        } else {
            data.isShow = true;
        }
        if (!oldState && data.isShow) { //由不可见到可见
            if (data.mHeadView.getVisibility() != View.VISIBLE) {
                data.mHeadView.setVisibility(View.VISIBLE);
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(data.mHeadView, Constants.ALPHA, 0f, 1f);
            animator.setDuration(200);
            animator.start();

        } else if (oldState && !data.isShow) { //由可见到不可见
            ObjectAnimator animator = ObjectAnimator.ofFloat(data.mHeadView, Constants.ALPHA, 1f, 0f);
            animator.setDuration(200);
            animator.start();
        }
    }

    /**
     * 根据 经纬度信息获取X,Y坐标;
     */
    public void getXYValue(RadarPoi bean) {
        double degree = getDegree(bean.mLocation.mLatitude, bean.mLocation.mLongitude);
        while (degree > 360) {
            degree = degree - 360;
            if (degree <= 360)
                break;
        }
        bean.mDegree = Math.toRadians(degree);
    }

    /**
     * 弧度角度转换原理： π弧度相当于180角度  π/180为1°对应的弧度
     */
    private double getDegree(double lat, double lon) {
        //中心位置坐标
        double lat1 = Math.toRadians(LocationManager.getInstance().getCachedCurrentAddressOrNil().mLatitude);
        double lon1 = Math.toRadians(LocationManager.getInstance().getCachedCurrentAddressOrNil().mLongitude);
        //信标坐标
        double lat2 = Math.toRadians(lat);
        double lon2 = Math.toRadians(lon);
        double dLon = (lon2 - lon1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.toDegrees(Math.atan2(y, x));
        brng = (brng + 360);
        brng = (brng > 360) ? (brng - 360) : brng;
        return brng;
    }

    /**
     * 雷达点跳动动画
     */
    private void startShowOther() {
        if (mRadarPeopleList != null && !mRadarPeopleList.isEmpty()) {
            for (final RadarPeople data : mRadarPeopleList) {
                if (data.isCovered) {
                    continue;
                }
                jumpPointAnimation(data);
            }
        }
        if (mRadarEventList != null && !mRadarEventList.isEmpty()) {
            for (final RadarEvent item : mRadarEventList) {
                jumpPointAnimation(item);
            }
        }
        if (mWebEventList != null && !mWebEventList.isEmpty()) {
            for (final RadarEvent item : mWebEventList) {
                jumpPointAnimation(item);
            }
        }
    }

    /**
     * 跳点动画
     *
     * @param data
     */
    private void jumpPointAnimation(RadarPoi data) {
        final AnimatorSet setAmimation = new AnimatorSet();
        setAmimation.playTogether(ObjectAnimator.ofFloat(data.mHeadView, Constants.SCALE_X, 1.2f, 1.0f)
                , ObjectAnimator.ofFloat(data.mHeadView, Constants.SCALE_Y, 1.2f, 1.0f));

        long delayTimes = Constants.HEAD_DELAY
                + (long) (data.mDegree * (SCAN_TIME / (Math.PI * 2)));
        if (data.mHeadView != null) {
            data.mHeadView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAmimation.setDuration(300).start();
                }
            }, delayTimes);
        }
    }

    /**
     * 集码效果
     */
    private ValueAnimator valueAnimator;
    private static int CodeAnim_Time = 500;

    public interface OnCodeAnimListener {
        void OnFinish();
    }

    public void startActivityCodeAnim(final View mView, final TextView mTextView, int startX, int startY, int endX, int endY, final OnCodeAnimListener listener) {

        LogManager.d(endY + "endY");
        valueAnimator = ValueAnimator.ofObject(new BezierEvaluator(startX, startY),
                new PointF(startX, startY), new PointF(endX, endY));
        valueAnimator.setDuration(CodeAnim_Time);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                ViewHelper.setX(mView, pointF.x);
                ViewHelper.setY(mView, pointF.y);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(
                                mView, Constants.SCALE_X,
                                1.0f, 0.3f), ObjectAnimator.ofFloat(
                                mView, Constants.SCALE_Y,
                                1.0f, 0.3f));
                animatorSet.setDuration(CodeAnim_Time);
                animatorSet.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mView.setVisibility(View.GONE);
                mTextView.setBackgroundResource(R.drawable.shape_draw_red);
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(
                                mTextView, Constants.SCALE_X,
                                0f, 1f), ObjectAnimator.ofFloat(
                                mTextView, Constants.SCALE_Y,
                                0f, 1f));
                animatorSet.setDuration(CodeAnim_Time);
                animatorSet.start();
                listener.OnFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setTarget(mView);
        valueAnimator.setupEndValues();
        valueAnimator.setRepeatCount(0);
        valueAnimator.start();
    }

    class BezierEvaluator implements TypeEvaluator<PointF> {
        int x = 0;
        int y = 0;

        public BezierEvaluator(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue,
                               PointF endValue) {
            final float t = fraction;
            float oneMinusT = 1.0f - t;
            PointF point = new PointF();

            PointF point0 = (PointF) startValue;

//            PointF point1 = new PointF();
//            if(x < Constants.SCREEN_WIDTH * 15/32 )
//            {
//                point1.set(-Constants.SCREEN_WIDTH/8, Constants.SCREEN_HEIGHT*2/3);
//            }
//            else
//            {
//                point1.set(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT*2/3);
//
//            }

            PointF point2 = (PointF) endValue;

//            point.x = oneMinusT * oneMinusT *  (point0.x)
//                    + 2 * oneMinusT * t * (point1.x)
//                    + t * t  * (point2.x);
//
//            point.y = oneMinusT * oneMinusT * (point0.y)
//                    + 2 * oneMinusT  * t * (point1.y)
//                    + t * t  * (point2.y);


            point.x = oneMinusT * (point0.x)
                    + t * (point2.x);

            point.y = oneMinusT * (point0.y)
                    + t * (point2.y);
            return point;
        }
    }

    /**
     * 底部列表出现动画
     *
     * @param view
     */
    public void showRecycleViewAnim(View view) {
        view.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, Constants.TRANSLATION_X, Constants.SCREEN_WIDTH, 0));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator());//反弹效果动画拦截器
        animatorSet.start();
    }

    /**
     * 底部布局出现动画
     *
     * @param mBottomLayout
     */
    public void showBottomLayout(View mBottomLayout, View mRadarLayout, final View mRecyclerView) {
        mRecyclerView.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.VISIBLE);
        double rate = Constants.SCREEN_HEIGHT * 1.0 / Constants.SCREEN_WIDTH * 1.0;
        int transDistance;
        if (rate < 1920 * 1.0 / 1080 * 1.0) {
            transDistance = DisplayUtil.dp2px(80);
        } else {
            transDistance = DisplayUtil.dp2px(60);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBottomLayout, Constants.SCALE_Y, 0f, 1f),
                ObjectAnimator.ofFloat(mRadarLayout, Constants.TRANSLATION_Y, 0, -transDistance));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showRecycleViewAnim(mRecyclerView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }


    /**
     * 展示滑动块
     *
     * @param view1
     * @param view2
     * @param view3
     */
    public void showDragBtnAnimation(final View view1, final View view2, final View view3) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animOne = ObjectAnimator.ofFloat(view1, Constants.ALPHA, 0f, 1f);
        ObjectAnimator animTwo = ObjectAnimator.ofFloat(view2, Constants.ALPHA, 0f, 1f);
        ObjectAnimator animThree = ObjectAnimator.ofFloat(view3, Constants.ALPHA, 0f, 1f);
        animatorSet.playTogether(animOne, animTwo, animThree);
        animatorSet.setDuration(2000);
        animatorSet.start();
    }

    /**
     * 隐藏底部列表动画
     *
     * @param bottomView  选中列表
     * @param compassView 罗盘
     */
    public void hideResultAnimation(View bottomView, View compassView, final OnResultHiddenListener listener) {
        bottomView.clearAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        double rate = Constants.SCREEN_HEIGHT * 1.0 / Constants.SCREEN_WIDTH * 1.0;
        //LogManager.d();
        int transDistance;
        if (rate < 1920 / 1080) {
            transDistance = DisplayUtil.dp2px(80);
        } else {
            transDistance = DisplayUtil.dp2px(60);
        }
        ObjectAnimator compassBackAnim = ObjectAnimator.ofFloat(compassView, Constants.TRANSLATION_Y, -transDistance, 0);
        ObjectAnimator bottomHideAnim = ObjectAnimator.ofFloat(bottomView, Constants.SCALE_Y, 1f, 0f);
        animatorSet.playTogether(compassBackAnim, bottomHideAnim);
        animatorSet.setDuration(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener == null) {
                    return;
                }
                listener.onHidden();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public interface OnResultHiddenListener {
        void onHidden();
    }
}
