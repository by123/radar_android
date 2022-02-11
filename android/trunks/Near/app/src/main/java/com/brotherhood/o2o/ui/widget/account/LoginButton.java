package com.brotherhood.o2o.ui.widget.account;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.DisplayUtil;

/**
 * 登录按钮
 * Created by jl.zhang on 2015/12/7.
 */
public class LoginButton extends View {

    private Context mContext;
    private Handler mHandler;

    private boolean mStarted;
    private long DEFAULT_INVALIDATE_RATE = 50L;
    private static final int PADDING = 5;
    private int STROKE_WIDTH = 10;

    private Paint mBackPaint;
    private Paint mTextPaint;
    private Paint mBorderPaint;

    private int mWidth;
    private int mHeight;
    private int mRadius;
    private Rect mTextBounds;

    private int mDrawType = AnimType.INIT;

    private String mLoginText;
    private static final int mTextSize = 18;//sp
    private float mTextAlpha = 255*60/100;
    private float mBgAlpha = 255*70/100;

    private int mBgColor = Color.parseColor("#ff7451");
    private int mTextColor = Color.parseColor("#ffffff");

    private boolean mClickable = true;

    public LoginButton(Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(mContext);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(mContext);
    }

    /**
     * 改变绘制模式（登录———>loading———>(登录失败)登录）
     * @param drawType
     */
    public void changeDrawType(int drawType){
        mDrawType = drawType;
    }
    /**
     * 按钮是否可点击
     * @return
     */
    public boolean isClickable(){
        return mClickable;
    }

    /**
     * 手机号、验证码填入后登录按钮设置为可点击状态
     */
    public void setLoginAble(boolean isAble){
        if (isAble){
            mClickable = true;
            mTextAlpha = 255;
            mBgAlpha = 255;
        }else {
            mClickable = false;
            mTextAlpha = 255*60/100;
            mBgAlpha = 255*70/100;
        }
        setClickable(mClickable);
        mDrawType = AnimType.INIT;
        invalidate();
    }


    private void init(Context context){
        mTextBounds = new Rect();
        //登录
        mLoginText = context.getResources().getString(R.string.login_title_text);
        //圆角矩形外圈背景（点登录按钮后，逐渐转为进度loading）
        mBorderPaint = new Paint();
        mBorderPaint.setStrokeWidth(STROKE_WIDTH);
        mBorderPaint.setColor(mBgColor);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        //圆角矩形内部背景(控制变淡)
        mBackPaint = new Paint();
        mBackPaint.setColor(mBgColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setAntiAlias(true);

        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DisplayUtil.sp2px(mTextSize));


    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            int width = mWidth - (PADDING * 2);
            int height = mHeight - (PADDING * 2);
            mRadius = width/3 > height ? height/2 : width/6;//如果高度的1/6，或者宽度的1/2，取更小的一个
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mDrawType){
            case AnimType.INIT:
                mBackPaint.setAlpha((int) mBgAlpha);
                canvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight)
                        , mRadius
                        , mRadius
                        , mBackPaint);
                mTextPaint.getTextBounds(mLoginText, 0, mLoginText.length(), mTextBounds);
                mTextPaint.setAlpha((int) mTextAlpha);
                canvas.drawText(mLoginText, mWidth / 2 - mTextBounds.width() / 2, mHeight / 2 + mTextBounds.height() / 2, mTextPaint);
                break;
            case AnimType.TO_LOADING://转为loading动画
                mClickable = false;
                int leftRect = mWidth/2 ;
                int rightRect = mWidth/2;
                mBackPaint.setAlpha((int) mBgAlpha);
                canvas.drawRoundRect(new RectF(leftRect, mHeight / 2 - mRadius, rightRect, mHeight / 2 + mRadius)
                        , mRadius
                        , mRadius
                        , mBackPaint);
                canvas.drawRoundRect(new RectF(leftRect, mHeight / 2 - mRadius, rightRect, mHeight / 2 + mRadius)
                        , mRadius
                        , mRadius
                        , mBorderPaint);
                break;
            case AnimType.TO_LOGIN://转为登录按钮(登录失败后)
                mClickable = true;
                mBackPaint.setAlpha((int) mBgAlpha);
                mTextPaint.setAlpha((int) mTextAlpha);
                break;
        }
    }
    // TODO: 2015/12/7  登录——>loading 动画

    // third animation which change back color and change to a circle
    //private long mThirdStartT;
    //private long mThirdStopT;
    //private long THIRD_DURATION = 400;
    //
    //
    //
    //private void initThirdAni() {
    //    mThirdStartT = System.currentTimeMillis();
    //    mThirdStopT = mThirdStartT + THIRD_DURATION;
    //}

    //private float getThirdRatio() {
    //    long now = System.currentTimeMillis();
    //    if (now >= mThirdStopT) {
    //        //mAniState = AnimType.THIRD_STOP;
    //        //mAniState = AnimType.FOURTH_START;
    //        //initFourthAni();
    //        return 1;
    //    }
    //
    //    float ratio = (now - mThirdStartT)/(float)THIRD_DURATION;
    //    return ratio >= 1 ? 1 : ratio;
    //}
    //
    //
    //private int getHorizonRadius() {
    //    float ratio = getThirdRatio();
    //    int horizonRadius = mRadius + (int) ((1-ratio) * (mWidth/2 - PADDING - mRadius));
    //    return horizonRadius;
    //}
    //
    //private int getThirdColor() {
    //    float ratio = getThirdRatio();
    //    int alpha = (int) ((1-ratio) * 0xff);
    //    return Color.argb(alpha, Color.red(mColor), Color.green2(mColor), Color.blue2(mColor));
    //}
    //
    //private int getThirdBorderColor() {
    //    float ratio = getThirdRatio();
    //    int redStart = Color.red(mColor);
    //    int greenStart = Color.2(mColor);
    //    int blueStart = Color.blue2(mColor);
    //
    //    int curRed = redStart + (int) ((Color.red(COLOR_GREY) - redStart) * ratio);
    //    int curGreen = greenStart + (int) ((Color.green2(COLOR_GREY) - greenStart) * ratio);
    //    int curBlue = blueStart + (int) ((Color.blue2(COLOR_GREY) - blueStart) * ratio);
    //
    //    return Color.argb(0xff, curRed, curGreen, curBlue);
    //}

    public interface AnimType{
        public static final int INIT = 0;
        public static final int TO_LOADING = 1;
        public static final int TO_LOGIN = 2;
    }
}
