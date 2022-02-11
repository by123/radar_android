package com.brotherhood.o2o.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DirectLayout extends RelativeLayout {

    // 获取坐标方向变化的数组
    private float mDegree = 0f;
    private PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public DirectLayout(Context context) {
        super(context);
    }

    public DirectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = mPaint;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.setDrawFilter(filter);
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int cx = w / 2;
        int cy = h / 2;
        canvas.rotate(-mDegree, cx, cy);

    }

    @SuppressLint("NewApi")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // if (getRealPostion(ev) == null) {
        // return dispatchTouchEvent(ev);
        // }
        try {
            return super.dispatchTouchEvent(getRealPostion(ev));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private MotionEvent getRealPostion(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        float center_x = this.getWidth() / 2.0f;
        float center_y = this.getHeight() / 2.0f;

        float distance_x = x - center_x;
        float distance_y = y - center_y;

        double distance = Math.sqrt(distance_x * distance_x + distance_y * distance_y);
        float rad1 = (float) Math.asin(distance_y / distance);

        if (distance_x < 0) {
            rad1 += Math.PI;
        }
        float rad;
        if (distance_x < 0) {
            rad = rad1 + (float) ((-mDegree) * Math.PI / 180.0f);
        } else {
            rad = (float) ((-mDegree) * Math.PI / 180.0f) - rad1;
        }

        distance_x = (float) (Math.cos(rad) * distance);
        distance_y = (float) (Math.sin(rad) * distance);

        float real_x = center_x + distance_x;
        float real_y = center_y - distance_y;

        return MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), real_x, real_y,
                ev.getMetaState());

    }

    @SuppressLint("NewApi")
    public void setValues(float mDegree) {
        this.mDegree = mDegree;
    }

}
