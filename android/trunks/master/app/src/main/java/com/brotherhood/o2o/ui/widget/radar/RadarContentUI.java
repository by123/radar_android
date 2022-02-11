package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.brotherhood.o2o.explore.ExploreComponent;
import com.brotherhood.o2o.explore.controller.ExploreFragment;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.CacheRef;
import com.brotherhood.o2o.config.Constants;

/**
 * 雷达控件
 *
 * @author
 */
public class RadarContentUI extends RelativeLayout {

    private static int iRecordCount = 0;
    private long lastTime = 0;

    private Scroller mScroller;

    public GestureDetector mGestureDetector;

    public RadarContentUI(Context context) {
        this(context, null);
    }

    public RadarContentUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setLongClickable(true);
        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context,
                new CustomGestureListener());
    }

    // 调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);

    }

    public void customSmoothScrollBy(int dx, int dy, int time) {
        if (mScroller == null) {
            smoothScrollBy(dx, dy);
            return;
        }

        if (getChildCount() == 0)
            return;

        final int heigh = getHeight() - getPaddingTop() - getPaddingBottom();
        final int maxY = Math.max(0, getChildAt(0).getHeight() - heigh);
        final int scrollY = getScrollY();
        dy = Math.max(0, Math.min(scrollY + dx, maxY)) - scrollY;

        mScroller.startScroll(getScrollX(), scrollY, 0, dy, time);
        invalidate();

    }

    public void customSmoothScrollTo(int x, int y, int time) {
        customSmoothScrollBy(x, y, time);
    }

    // 调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        ByLogout.out("get getFinalX" + mScroller.getFinalX());
        ByLogout.out("get getFinalY" + mScroller.getFinalY());
        ByLogout.out("get dx" + dx + "");
        ByLogout.out("get dy" + dy + "");
        // 设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy);
        invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public void smoothScrollBy(int dx, int dy, int time) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy, time);
        invalidate();
    }

    @Override
    public void computeScroll() {

        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int iCount = event.getPointerCount();
        Log.e("RadarUI", iCount + "");

        if (iCount == 2) {

            Log.e("RadarUI", "sssssssss");
            if (System.currentTimeMillis() - lastTime > 1000)
                iRecordCount = 0;

            lastTime = System.currentTimeMillis();

            iRecordCount++;
            Log.e("RadarUI", iRecordCount + "");
            if (iRecordCount > 80) {
                iRecordCount = 0;
                ShowChangePosition();
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                int x = (int) event.getX();
                int y = (int) event.getY();
                ByLogout.out("content ui y" + x + " " + y + "");

                RadarViewMove(Constants.RADARVIEW_DOWN);
                RadarViewMove(Constants.RADARVIEW_MOVE);
                HideResultView();
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                RadarViewMove(Constants.RADARVIEW_UP);
                ByLogout.out("get Sy" + getScrollY());
                // 根据滚动的值来判断是否更新：
                customSmoothScrollTo(0, 0, 800);
                HideScanStatus();
                break;
            default: {
                return mGestureDetector.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private void RadarViewMove(int action) {
        try {
            if (getFragment() != null) {
                Message msg = getFragment().mHandler.obtainMessage(action);
                msg.sendToTarget();
            }
        } catch (Exception e) {
        }

    }

    private void HideResultView() {
        if (getFragment() != null) {
            if (CacheRef.getInstance().getmUserId() > 0) {
                Message msg = getFragment().mHandler
                        .obtainMessage(Constants.HIDE_GALLERY_VIEW);
                msg.obj = getScrollY();
                msg.sendToTarget();
            }
        }

    }

    private void ShowChangePosition() {
        if (getFragment() != null) {
            if (CacheRef.getInstance().getmUserId() > 0) {
                getFragment().mHandler
                        .sendEmptyMessage(Constants.SHOW_CHANGE_POSITION);
            }
        }
    }

    private void UpdateScanStatus() {
        if (getFragment() != null) {
            if (CacheRef.getInstance().getmUserId() > 0) {
                Message msg = getFragment().mHandler
                        .obtainMessage(Constants.SHOW_RADAR_PULL_DOWM);
                msg.obj = getScrollY();
                msg.sendToTarget();
            }
        }
    }

    private void UpdateScanStatusExt() {
        if (getFragment() != null) {
            if (CacheRef.getInstance().getmUserId() > 0) {
                Message msg = getFragment().mHandler
                        .obtainMessage(Constants.SHOW_RADAR_PULL_UP);
                msg.obj = getScrollY();
                msg.sendToTarget();
            }
        }
    }

    private void HideScanStatus() {
        if (getFragment() != null) {
            if (CacheRef.getInstance().getmUserId() > 0) {
                ExploreFragment mExploreFragment = getFragment();
                if (getScrollY() > -200) {

                    Message msg = mExploreFragment.mHandler
                            .obtainMessage(Constants.RADAR_PULL_DOWN_DISTANCE_SHORT);
                    msg.sendToTarget();
                } else {
                    Message msg = mExploreFragment.mHandler
                            .obtainMessage(Constants.RADAR_FINDING_AROUND_PEOPLE);
                    msg.sendToTarget();
                    //屏蔽
//					if (!mExploreFragment.radarIsRefreshing) {
//						RadarApplication1.getInstance().bFlashRadarData = true;
//						RadarApplication1.getInstance().initTencentLocation();
//						mExploreFragment.radarIsRefreshing = true;
//					}
                }

            }
        }
    }

    class CustomGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            int dis = (int) ((distanceY - 0.5) / 2);
            smoothScrollBy(0, dis);

            ByLogout.out("scrollY->" + getScrollY());
            if (CacheRef.getInstance().getmUserId() > 0) {

                if (getScrollY() < 0 && getScrollY() < -200)
                    UpdateScanStatusExt();
                else if (getScrollY() < 0 && getScrollY() < -50)
                    UpdateScanStatus();

                UpdateAlpValue();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return false;
        }
    }

    public void UpdateAlpValue() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler
                    .obtainMessage(Constants.UPDATE_ALP_VALUE);
            msg.obj = getScrollY();
            msg.sendToTarget();
        }
    }

    private ExploreFragment getFragment() {
        return ExploreComponent.shareComponent().getExploreFragment();
    }

}
