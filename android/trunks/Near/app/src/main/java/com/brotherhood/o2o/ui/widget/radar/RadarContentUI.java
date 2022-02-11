package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.controller.ExploreComponent;
import com.brotherhood.o2o.ui.fragment.ExploreFragment;

/**
 * 实现包裹的控件(雷达盘)的滚动效果，及通知界面对相应滚动状态做界面上的处理
 *
 * @author
 */
public class RadarContentUI extends RelativeLayout {

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
        mGestureDetector = new GestureDetector(context, new CustomGestureListener());
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
        if (getChildCount() == 0) {
            return;
        }
        final int heigh = getHeight() - getPaddingTop() - getPaddingBottom();//RadarContentUI内部容器高度
        final int maxY = Math.max(0, getChildAt(0).getHeight() - heigh);//雷达盘高度与父容器高度差
        final int scrollY = getScrollY();//当前Y坐标
        dy = Math.max(0, Math.min(scrollY + dx, maxY)) - scrollY;
        //开始一个动画控制，由(startX , startY)在duration时间内前进(dx,dy)个单位，即到达坐标为(startX+dx , startY+dy)出
        mScroller.startScroll(getScrollX(), scrollY, -getScrollX(), dy, time);
        invalidate();
    }

    public void customSmoothScrollTo(int x, int y, int time) {
        customSmoothScrollBy(x, y, time);
    }

    // 调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        // 设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public void smoothScrollBy(int dx, int dy, int time) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy, time);
        invalidate();
    }

    @Override
    public void computeScroll() {//根据当前已经消逝的时间计算当前的坐标点，保存在mCurrX和mCurrY值中

        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            updateAlpaStatus();
            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                hideResultView();
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP://松开
                customSmoothScrollTo(0, 0, 800);//800mills移动到（0,0）的位置
                // 根据滚动的值来判断是否更新：
                hidePullDownStatus();
                break;
            case MotionEvent.ACTION_CANCEL:
                customSmoothScrollTo(0, 0, 800);//800mills移动到（0,0）的位置
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
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int disY = (int) ((distanceY - 0.5) / 2);
            int disX = (int) ((distanceX - 0.5) / 2);
            smoothScrollBy(disX, disY);
            if (getScrollY() < -200) {
                updateBackUpStatus();
            } else if (getScrollY() < -50) {
                updatePullDownStatus();
            }else if (getScrollY() > 50){
                updatePullUpStatus();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void hideResultView() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler.obtainMessage(Constants.HIDE_RESULT_VIEW);
            msg.obj = getScrollY();
            msg.sendToTarget();
        }

    }

    /**
     * 通知界面，状态为下拉
     */
    private void updatePullDownStatus() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler.obtainMessage(Constants.SHOW_RADAR_PULL_DOWM);
            msg.obj = getScrollY();
            msg.sendToTarget();
        }
    }

    /**
     * 通知界面，状态为从下拉转为上拉回到原本位置
     */
    private void updateBackUpStatus() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler.obtainMessage(Constants.SHOW_RADAR_BACK_UP);//雷达盘从下拉转为上拉回到原本位置
            msg.obj = getScrollY();
            msg.sendToTarget();
        }
    }

    /**
     * 通知界面，状态为从下拉转为上拉回到原本位置
     */
    private void updatePullUpStatus() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler.obtainMessage(Constants.SHOW_RADAR_PULL_UP);//雷达盘从下拉转为上拉回到原本位置
            msg.obj = getScrollY();
            msg.sendToTarget();
        }
    }

    private void hidePullDownStatus() {
        if (getFragment() != null) {
            ExploreFragment mExploreFragment = getFragment();
            if (getScrollY() > -200) {
                Message msg = mExploreFragment.mHandler.obtainMessage(Constants.RADAR_PULL_DOWN_DISTANCE_SHORT);
                msg.sendToTarget();
            } else {
                Message msg = mExploreFragment.mHandler.obtainMessage(Constants.RADAR_FINDING_AROUND_PEOPLE);
                msg.obj = getScrollY();
                msg.sendToTarget();
            }
        }
    }

    public void updateAlpaStatus() {
        if (getFragment() != null) {
            Message msg = getFragment().mHandler.obtainMessage(Constants.UPDATE_ALPHA_VALUE);
            if (mScroller == null){
                return;
            }
            int maxX = Math.abs(mScroller.getCurrX());
            int maxY = Math.abs(mScroller.getCurrY());

            msg.obj = Math.max(maxX, maxY);
            msg.sendToTarget();
        }
    }

    private ExploreFragment getFragment() {
        return ExploreComponent.shareComponent().getExploreFragment();
    }

}
