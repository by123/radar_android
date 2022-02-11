package com.brotherhood.o2o.test;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by by.huang on 2015/7/3.
 */
public class TestView extends TextView {

    private GestureDetector gd;
    private int scrollingOffsetX;
    private int scrollingOffsetY;

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gd = new GestureDetector(context, new InnerGestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(scrollingOffsetX, scrollingOffsetY);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    class InnerGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            scrollingOffsetX += -distanceX;
            scrollingOffsetY += -distanceY;
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

}