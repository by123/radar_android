package com.brotherhood.o2o.chat.ui.view;


import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.Res;


/**
 * 波纹动画
 */
public class WaveCycleView extends TextView {

    private static final String tag = WaveCycleView.class.getSimpleName();

    /**
     * 波纹的颜色
     */
    private int mRippleColor;
    /**
     * 默认的波纹的最小值
     */
    private int mMinSize = 50;
    /**
     * 波纹动画效果是否正在进行
     */
    private boolean animationRunning = false;

    private int currentProgress = 0;
    /**
     * 动画中波纹的个数
     */
    private int mRippleNum = 3;
    /**
     * //无限长的数值，使动画不停止
     */
    private int mTotalTime = 1000 * 1000;

    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;

    private int mode = MODE_OUT;

    private int mPeriod = 30;
    private int mCenterX;
    private int mCenterY;
    private int mRadius;
    private Paint mPaint;
    private ObjectAnimator mAnimator;

    public WaveCycleView(Context context) {
        super(context);
        initPaint();
        initAnimation();
    }

    public WaveCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initAnimation();
    }

    public WaveCycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initAnimation();
    }

    private void initPaint() {
        mPaint = new Paint();
        this.mRippleColor = Res.getColor(R.color.near_main_orange_color);
        mPaint.setStrokeWidth(70);
        SweepGradient lg = new SweepGradient(0, 0, mRippleColor, mRippleColor);
        mPaint.setShader(lg);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mRippleColor);
    }

    private void initAnimation() {
        mAnimator = ObjectAnimator.ofInt(this, "currentProgress", 0, 100);
        mAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setEvaluator(mProgressEvaluator);
        mAnimator.setDuration(mTotalTime);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void startRippleAnimation() {
        if (!animationRunning) {
            mAnimator.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (animationRunning) {
            mAnimator.end();
            animationRunning = false;
        }
    }

    public boolean isRippleAnimationRunning() {
        return animationRunning;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        this.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resultWidth = 0;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (modeWidth == MeasureSpec.EXACTLY) {
            resultWidth = sizeWidth;
        } else {
            resultWidth = mMinSize;
            if (modeWidth == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
        }

        int resultHeight = 0;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (modeHeight == MeasureSpec.EXACTLY) {
            resultHeight = sizeHeight;
        } else {
            resultHeight = mMinSize;
            if (modeHeight == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, sizeHeight);
            }
        }
        mCenterX = resultWidth / 2;
        mCenterY = resultHeight / 2;
        mRadius = Math.max(resultWidth, resultHeight) / 2 - 18;
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (int i = 0; i < mRippleNum; i++) {
            int progress = (currentProgress + i * 100 / (mRippleNum)) % 100;
            if (mode == 1)
                progress = 100 - progress;

            mPaint.setAlpha(255 - 255 * progress / 100);
            canvas.drawCircle(mCenterX, mCenterY, mRadius * progress / 100, mPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isRippleAnimationRunning())
            stopRippleAnimation();
    }

    /**
     * 自定义估值器
     */
    private TypeEvaluator mProgressEvaluator = new TypeEvaluator() {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            fraction = (fraction * mTotalTime / mPeriod) % 100;
            return fraction;
        }
    };

}
