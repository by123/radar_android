package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;

/**
 * 模糊遮罩
 */
public class BlurMaskView extends View {

    private Paint mPaint;
    private int mDrawableId;

    public BlurMaskView(Context context) {
        this(context, null);
    }

    public BlurMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setImageDrawable(@DrawableRes int drawableId){
        this.mDrawableId = drawableId;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), mDrawableId);
        Bitmap shadowBitmap = srcBitmap.extractAlpha();// 获取位图的Alpha通道图
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.INNER));// 设置画笔遮罩滤镜 ,传入度数和样式
        canvas.drawBitmap(shadowBitmap, 0, 0, mPaint);// 先绘制阴影
        canvas.drawBitmap(srcBitmap, 0, 0, null);// 画原图
    }
}
