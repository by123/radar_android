package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.brotherhood.o2o.util.DisplayUtil;

/**
 * 雷达上遮罩（拖动雷达范围按钮时显示）
 * Created by jl.zhang on 2015/12/10.
 */
public class CoverView extends View {

    private Paint mBigTextPaint;
    private Paint mSmallTextPaint;
    private Paint mBackgroundPaint;
    private static final int BIG_TEXT_SIZE = 40;//sp
    private static final int SMALL_TEXT_SIZE = 17;//sp
    private static final int VERTICAL_MARGIN = 10;//dp
    private static final int HORIZONTAL_MARGIN = 10;//dp
    private int mPointX;
    private int mPointY;
    private int mScreenWidth;
    private int mScreenHeight;
    private String mDistance = "100m";
    private String mPeopleCount = "60人";
    //private String mActivitiyCount = "3活动";

    public CoverView(Context context) {
        super(context);
        init();
    }

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBigTextPaint = new Paint();
        mBigTextPaint.setAntiAlias(true);
        mBigTextPaint.setColor(Color.parseColor("#ffffff"));
        mBigTextPaint.setStyle(Paint.Style.FILL);
        mBigTextPaint.setTextSize(DisplayUtil.sp2px(BIG_TEXT_SIZE));

        mSmallTextPaint = new Paint();
        mSmallTextPaint.setAntiAlias(true);
        mSmallTextPaint.setColor(Color.parseColor("#ffffff"));
        mSmallTextPaint.setStyle(Paint.Style.FILL);
        mSmallTextPaint.setTextSize(DisplayUtil.sp2px(SMALL_TEXT_SIZE));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mScreenWidth = DisplayUtil.getScreenWidth(getContext());
        mScreenHeight = DisplayUtil.getScreenHeight(getContext());
        setBackgroundColor(Color.parseColor("#00000000"));
        if (mPointX == 0) {
            mPointX = DisplayUtil.dp2px(54);//x起始点54dp处
        }
        if (mPointY == 0) {
            mPointY = DisplayUtil.getScreenHeight(getContext()) - DisplayUtil.dp2px(38);
        }
    }

    public void setCurrentPoint(int x, int y, String distance, String peopleCount, String activityCount) {
        mPointX = x;
        mPointY = y;
        mDistance = distance;
        mPeopleCount = peopleCount;
        //mActivitiyCount = activityCount;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            Path blackPath = new Path();
            blackPath.moveTo(mPointX, mPointY);
            blackPath.lineTo(mPointX, 0);
            blackPath.lineTo(DisplayUtil.getScreenWidth(getContext()), 0);
            blackPath.lineTo(DisplayUtil.getScreenWidth(getContext()), DisplayUtil.getScreenHeight(getContext()));
            blackPath.lineTo(mPointX, DisplayUtil.getScreenHeight(getContext()));
            blackPath.lineTo(mPointX, mPointY);
            mBackgroundPaint.setColor(Color.parseColor("#36000000"));
            blackPath.close(); //把开始的点和最后的点连接在一起，构成一个封闭图形
            canvas.drawPath(blackPath, mBackgroundPaint);//画黑色背景
            float bigSize = mBigTextPaint.measureText(mDistance);//返回字符串宽度
            Rect bigRect = new Rect();
            mBigTextPaint.getTextBounds(mDistance, 0, 1, bigRect);
            float smallSize1 = mSmallTextPaint.measureText(mPeopleCount);//返回单位宽度
            Rect smallRect1 = new Rect();
            mSmallTextPaint.getTextBounds(mPeopleCount, 0, 1, smallRect1);
            //float smallSize2 = mSmallTextPaint.measureText(mActivitiyCount);//返回单位宽度
            //Rect smallRect2 = new Rect();

            int distancePointY =  mPointY - (smallRect1.height() * 2) - DisplayUtil.dp2px(VERTICAL_MARGIN * 2) - DisplayUtil.dp2px(100);
            int peoplePointY = mPointY - smallRect1.height() - DisplayUtil.dp2px(VERTICAL_MARGIN) - DisplayUtil.dp2px(100);
            int activityPointY = mPointY - DisplayUtil.dp2px(100);
            if (distancePointY < DisplayUtil.dp2px(100)){
                distancePointY = DisplayUtil.dp2px(100);
            }
            if (peoplePointY < distancePointY + DisplayUtil.dp2px(VERTICAL_MARGIN) + bigRect.height()/2){
                peoplePointY = distancePointY + DisplayUtil.dp2px(VERTICAL_MARGIN)  + bigRect.height()/2;
            }
            if (activityPointY < peoplePointY + DisplayUtil.dp2px(VERTICAL_MARGIN) + smallRect1.height()/2){
                activityPointY = peoplePointY + DisplayUtil.dp2px(2*VERTICAL_MARGIN) + smallRect1.height()/2;
            }
            if (mPointX >= mScreenWidth / 2) {//画左边
                canvas.drawText(mDistance, mPointX - bigSize - DisplayUtil.dp2px(HORIZONTAL_MARGIN), distancePointY, mBigTextPaint);
                canvas.drawText(mPeopleCount, mPointX - smallSize1 - DisplayUtil.dp2px(HORIZONTAL_MARGIN), peoplePointY, mSmallTextPaint);
                //mSmallTextPaint.getTextBounds(mActivitiyCount, 0, 1, smallRect2);
                //canvas.drawText(mActivitiyCount, mPointX - smallSize2 - DisplayUtil.dp2px(HORIZONTAL_MARGIN), activityPointY, mSmallTextPaint);
            } else {//画右边
                canvas.drawText(mDistance, mPointX + DisplayUtil.dp2px(HORIZONTAL_MARGIN), distancePointY, mBigTextPaint);
                canvas.drawText(mPeopleCount, mPointX + DisplayUtil.dp2px(HORIZONTAL_MARGIN), peoplePointY, mSmallTextPaint);
                //mSmallTextPaint.getTextBounds(mActivitiyCount, 0, 1, smallRect2);
                //canvas.drawText(mActivitiyCount, mPointX + DisplayUtil.dp2px(HORIZONTAL_MARGIN), activityPointY, mSmallTextPaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
