package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.brotherhood.o2o.util.DisplayUtil;

/**
 * Created by laimo.li on 2015/12/23.
 */
public class SwLin extends ViewGroup {

    public static final int MENU_WIDTH = DisplayUtil.dp2px(80);

    public static final int SCREEN_MENU = 0;
    public static final int SCREEN_MAIN = 1;
    private static final int SCREEN_INVALID = -1;

    private int mCurrentScreen;
    private int mNextScreen = SCREEN_INVALID;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;

    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 1000;
    public int mTouchState = TOUCH_STATE_REST;
    private boolean mAllowLongPress;

    public SwLin(Context context) {
        this(context, null, 0);

    }

    public SwLin(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(getContext());
        mCurrentScreen = SCREEN_MENU;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public SwLin(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureViews(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureViews(int widthMeasureSpec, int heightMeasureSpec) {
        View view0 = getChildAt(0);
        View view1 = getChildAt(1);
        view0.measure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("view1.getLayoutParams().width:"
                + view1.getLayoutParams().width);
        System.out.println("view0:" + view0.getMeasuredWidth());
        System.out.println("view0:" + view0.getWidth());
        System.out.println("view0:" + view0.getRight());
        System.out.println("view0:" + view0.getLeft());
        view1.measure(view1.getLayoutParams().width, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new IllegalStateException(
                    "The childCount of SlidingMenu must be 2");
        }
        View view0 = getChildAt(0);
        View view1 = getChildAt(1);
        System.out.println("view0:" + view0.getMeasuredWidth());
        System.out.println("view1:" + view1.getMeasuredWidth());

        final int width = view1.getMeasuredWidth();
        view0.layout(0, 0, view0.getMeasuredWidth(), view0.getMeasuredHeight());

        view1.layout(view0.getRight(), 0,
                view0.getMeasuredWidth() + MENU_WIDTH,
                view1.getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            child.setFocusable(true);
            child.setClickable(true);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (listener == null) {
            return true;
        }
        if (listener.startTouch(getTag().toString())) {
            listener.canTouch(false);

            return true;
        }
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;

                if (xMoved || yMoved) {

                    if (xMoved) {
                        // Scroll if the user moved far enough along the X axis
                        mTouchState = TOUCH_STATE_SCROLLING;
                        enableChildrenCache();
                    }
                    // Either way, cancel any pending longpress
                    if (mAllowLongPress) {
                        mAllowLongPress = false;
                        // Try canceling the long press. It could also have been
                        // scheduled
                        // by a distant descendant, so use the mAllowLongPress flag
                        // to block
                        // everything
                        final View currentScreen = getChildAt(mCurrentScreen);
                        currentScreen.cancelLongPress();
                    }
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mAllowLongPress = true;

                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                clearChildrenCache();
                mTouchState = TOUCH_STATE_REST;
                mAllowLongPress = false;

                break;
        }

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
        return mTouchState != TOUCH_STATE_REST;
    }

    void enableChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(true);
            System.out.println("enableChildrenCache");

        }
    }

    void clearChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(false);
            System.out.println("clearChildrenCache");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    System.out.println("deltaX:" + deltaX);
                    System.out.println("getScrollX() :" + getScrollX());
                    // scrollBy(deltaX, 0);

                    if (deltaX > 0) {

                        if (deltaX + getScrollX() <= getChildAt(1).getWidth()) {
                            scrollBy(deltaX, 0);
                        }

                    } else if (deltaX < 0) {

                        if (getScrollX() > 0) {
                            scrollBy(deltaX, 0);
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    if (mCurrentScreen == SCREEN_MAIN) {
                        // Fling hard enough to move left
                        snapToScreen(SCREEN_MENU);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen == SCREEN_MENU) {
                        // Fling hard enough to move right
                        snapToScreen(SCREEN_MAIN);
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                mTouchState = TOUCH_STATE_REST;
                break;

        }

        return true;
    }

    protected void snapToScreen(int whichScreen) {

        enableChildrenCache();

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        boolean changingScreens = whichScreen != mCurrentScreen;

        mNextScreen = whichScreen;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingScreens
                && focusedChild == getChildAt(mCurrentScreen)) {
            focusedChild.clearFocus();
        }

        if (listener != null)
            listener.changeScreen(mNextScreen, getTag().toString());
        final int newX = (whichScreen) * getChildAt(1).getWidth();
        final int delta = newX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);

        invalidate();
    }

    protected void snapToDestination() {
        if (getScrollX() == 0) {
            return;
        }
        final int screenWidth = getChildAt(1).getWidth();
        final int whichScreen = (screenWidth + getScrollX() + (screenWidth / 2))
                / screenWidth;
        snapToScreen(whichScreen);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        } else if (mNextScreen != SCREEN_INVALID) {
            mCurrentScreen = Math.max(0,
                    Math.min(mNextScreen, getChildCount() - 1));
            mNextScreen = SCREEN_INVALID;
            clearChildrenCache();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int scrollX = getScrollX();
        System.out.println("dispatchDraw:" + scrollX);

        super.dispatchDraw(canvas);
        canvas.translate(scrollX, 0);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentScreen() > 0) {
                snapToScreen(getCurrentScreen() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentScreen() < getChildCount() - 1) {
                snapToScreen(getCurrentScreen() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    public int getCurrentScreen() {
        return mCurrentScreen;
    }

    public interface ScreenListener {
        boolean startTouch(String tag);

        void canTouch(boolean flag);

        void changeScreen(int screen, String tag);
    }

    private ScreenListener listener;

    public void setScreenListener(ScreenListener l) {
        this.listener = l;
    }

    public void showScreen(int screen) {
        if (screen != SCREEN_MAIN && screen != SCREEN_MENU) {
            return;
        }
        mCurrentScreen = screen;
        snapToScreen(mCurrentScreen);

    }

    public boolean isShowMenu(){
        return mCurrentScreen == SCREEN_MAIN;
    }

}

