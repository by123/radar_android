package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.brotherhood.o2o.R;

/**
 * 自定义圆角图片控件，自定义哪个角、角度
 * Created by jl.zhang on 2015/12/18.
 */

public class CornerImageView extends ImageView {

    private Paint paint;
    private Paint paint2;
    private int leftTop;
    private int rightTop;
    private int leftBottom;
    private int rightBottom;

    public CornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CornerImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView);
            leftTop = a.getDimensionPixelSize(R.styleable.CornerImageView_leftTopRadius, 0);
            rightTop = a.getDimensionPixelSize(R.styleable.CornerImageView_rightTopRadius, 0);
            leftBottom = a.getDimensionPixelSize(R.styleable.CornerImageView_leftBottomRadius, 0);
            rightBottom = a.getDimensionPixelSize(R.styleable.CornerImageView_rightBottomRadius, 0);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        if (leftTop > 0) {
            drawLeftTop(canvas2);
        }
        if (rightTop > 0) {
            drawRightTop(canvas2);
        }
        if (leftBottom > 0) {
            drawLeftBottom(canvas2);
        }
        if (rightBottom > 0) {
            drawRightBottom(canvas2);
        }
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }

    private void drawLeftTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, leftTop);
        path.lineTo(0, 0);
        path.lineTo(leftTop, 0);
        path.arcTo(new RectF(
                        0,
                        0,
                        leftTop * 2,
                        leftTop * 2),
                -90,
                -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), rightTop);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - rightTop, 0);
        path.arcTo(new RectF(
                        getWidth() - rightTop * 2,
                        0,
                        getWidth(),
                        0 + rightTop * 2),
                -90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLeftBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - leftBottom);
        path.lineTo(0, getHeight());
        path.lineTo(leftBottom, getHeight());
        path.arcTo(new RectF(
                        0,
                        getHeight() - leftBottom * 2,
                        0 + leftBottom * 2,
                        getHeight()),
                90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - rightBottom, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - rightBottom);
        path.arcTo(new RectF(
                getWidth() - rightBottom * 2,
                getHeight() - rightBottom * 2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }
}
