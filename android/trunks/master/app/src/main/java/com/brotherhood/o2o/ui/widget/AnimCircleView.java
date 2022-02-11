package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.brotherhood.o2o.R;

/**
 * Created by by.huang on 2015/7/20.
 */
public class AnimCircleView extends View {

    private final Paint paint;
    private final Context context;

    public AnimCircleView(Context context) {
        this(context, null);
    }

    public AnimCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
        this.paint.setStyle(Paint.Style.STROKE); //绘制空心圆
        this.paint.setColor(getResources().getColor(R.color.main_red));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth()/2;
        int height=getHeight()/2;
        canvas.drawCircle(center, height, 100, paint);
        paint.setAlpha(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawCircle(center, height, 80,paint);
        super.onDraw(canvas);
    }


}
