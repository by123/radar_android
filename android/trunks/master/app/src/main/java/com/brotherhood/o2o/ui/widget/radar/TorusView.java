package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.brotherhood.o2o.utils.Utils;

/**
 * 雷达外两层圆环
 * 
 * @author by
 */
public class TorusView extends View {
    public TorusView(Context context) {
        super(context);
    }

    public TorusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TorusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRadius != 0) {
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);

            int center_width = getWidth() / 2;
            int center_height = getHeight() / 2;
            int width = Utils.dip2px(55);
            mPaint.setARGB(12, 0, 0, 0);
            mPaint.setStrokeWidth(width);
            canvas.drawCircle(center_width, center_height, width / 2 + mRadius - 20, mPaint);
            canvas.drawCircle(center_width, center_height, width / 2 + mRadius - 20 + width + 1, mPaint);

            mPaint.setARGB(12, 0, 0, 0);
            canvas.drawCircle(center_width, center_height, width / 2 + mRadius - 20 + 1, mPaint);
        }

    }

    public int mRadius = 0;

    public void setRadius(int radius) {
        mRadius = radius;
        invalidate();
    }

}
